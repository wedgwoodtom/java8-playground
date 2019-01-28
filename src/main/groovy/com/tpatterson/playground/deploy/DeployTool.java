package com.tpatterson.playground.deploy;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Properties;

public class DeployTool
{
    String hostList = "phl1tplicws13,phl1tplicws14,phl1tplicws19,phl1tplicws20,phl1tplicws21,phl1tplicws22,phl1tplicws23,phl1tplicws24";


    @Test
    public void sshToDeleteAdapterCacheDirs()
    {
        String user = "tom.patterson";
        String password = "PASSWORD";
        String command = "sudo rm -rf /app/osgi/adapter-cache";

        String[] hosts = StringUtils.split(hostList, ",");
        for (String host : hosts)
        {
            try
            {
                if (!StringUtils.isEmpty(host))
                {
                    System.out.println("Executing: " + command + " on " + host);
                    execute(host, user, password, command);
                }
            }
            catch (Exception error)
            {
                System.out.println("Error in processing: " + host + ", " + error.toString());
            }
        }
    }


    @Test
    public void waitUntilAllAreAlive()
    {
        String[] hosts = StringUtils.split(hostList, ",");

        int numberAlive = 0;
        while (numberAlive != hosts.length)
        {
            numberAlive = 0;
            for (String host : hosts)
            {
                try
                {
                    if (isAlive(lwsHostUrl(host)))
                    {
                        numberAlive++;
                    }
                }
                catch (Exception error)
                {
                    System.out.println("Host: " + host + " is not alive, error=" + error.getMessage());
                }
            }

            System.out.println(numberAlive + " of " + hosts.length + " are alive.");

            if (numberAlive < hosts.length)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void verifyVersion()
    {
        String[] hosts = StringUtils.split(hostList, ",");
        for (String host : hosts)
        {
            System.out.println(host + " matches version: " + matchesVersion(lwsHostUrl(host), "3.15.0"));
        }
    }

    private String lwsHostUrl(String hostName)
    {
        return "http://" + hostName + ":10504/license";
    }

    private boolean isAlive(String host)
    {
        try
        {
            String contents = Request.Get(host + "/management/alive")
                .execute()
                .returnContent()
                .asString();

            Assert.assertTrue(contents.contains("Web Service is Ok"));
        }
        catch (Exception error)
        {
            throw new RuntimeException(error);
        }

        return true;
    }

    private boolean matchesVersion(String host, String version)
    {
        try
        {
            String contents = Executor.newInstance().auth("stats", "stizzats")
                .execute( Request.Get(host + "/management/status"))
                .returnContent()
                .asString();

            Assert.assertTrue(contents.contains(version));
        }
        catch (Exception error)
        {
            throw new RuntimeException(error);
        }

        return true;
    }

    private String execute(String host, String user, String password, String command)
    {
        StringBuffer result = new StringBuffer();
        try
        {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            // Create a JSch session to connect to the server
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            // Establish the connection
            session.connect();
//            System.out.println("Connected...");

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true)
            {
                while (in.available() > 0)
                {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                    {
                        break;
                    }
//                    System.out.print(new String(tmp, 0, i));
                    result.append(tmp);
                }
                if (channel.isClosed())
                {
//                    System.out.println("Exit Status: "
//                        + channel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }
            channel.disconnect();
            session.disconnect();
//            System.out.println("DONE!!!");
            if (channel.getExitStatus() != 0)
            {
                throw new RuntimeException(
                    "Command: " + command + " failed with status:" + channel.getExitStatus() + ", response=" + result.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return result.toString();
    }
}
