package com.tpatterson.playground.work.deploy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import com.tpatterson.playground.work.LicenseRequest;
import com.tpatterson.playground.work.ServiceUriBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class DeployTool
{
//    String hostList = "phl1tplicws13,phl1tplicws14,phl1tplicws19,phl1tplicws20,phl1tplicws21,phl1tplicws22,phl1tplicws23,phl1tplicws24";
    String hostList = "lon3tplicws21.lon.corp.theplatform.com,lon3tplicws22.lon.corp.theplatform.com,lon3tplicws23.lon.corp.theplatform.com,lon3tplicws24.lon.corp.theplatform.com,lon3tplicws25.lon.corp.theplatform.com,lon3tplicws26.lon.corp.theplatform.com,lon3tplicws27.lon.corp.theplatform.com,lon3tplicws28.lon.corp.theplatform.com,lon3tplicws29.lon.corp.theplatform.com,lon3tplicws30.lon.corp.theplatform.com";


    @Test
    public void sshToDeleteAdapterCacheDirs()
    {
        String user = "USER";
        String password = "PASSWORD!";
        String command = "sudo rm -rf /app/osgi/adapter-cache";

        String[] hosts = StringUtils.split(hostList, ",");
        for (String host : hosts)
        {
            try
            {
                if (!StringUtils.isEmpty(host))
                {
                    System.out.println("Executing: " + command + " on " + host);
                    executeSSHCommand(host, user, password, command);
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
    public void testGLR()
    {
        String releasePid = "drmmonRegular";
        // TODO: Signin bits - must be manually generated now
        String accountId = "http://access.auth.theplatform.com/data/Account/2706202973";
        String endUserAuth = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJkcm1tb25SZWd1bGFyL0xpY2Vuc2VGbGlwTW9uaXRvciIsImlzcyI6IjEiLCJleHAiOjE4NjQwODUwNTAsImlhdCI6MTU0ODcyNTA1MDAyMSwianRpIjoiYTM0M2NkY2ItYzIxYy00Y2ZiLWFkN2UtMmFkY2ZjZTI2YWVjIiwiZGlkIjoiZHJtbW9uUmVndWxhciIsInVubSI6IkxpY2Vuc2VGbGlwTW9uaXRvciIsImN0eCI6IntcInVzZXJOYW1lXCI6XCJMaWNlbnNlRmxpcE1vbml0b3JcIixcImF0dHJpYnV0ZXNcIjp7XCJzdWJzY3JpcHRpb25MZXZlbFwiOlwic3RhbmRhcmRcIn19XG4iLCJvaWQiOm51bGx9.ViExlv7QB7RXRsBA2F3ACXrkY6PUjBDkwZFMH2zAZ26KvW2nygLYrjF8G8Ap1_Kx_jV-61xt5ofekDPngFOzasNpNS1tsETgh2CI1ZvPfZcWqSCs_Q1qOigkTQCEkPddKjYT5YQ9qCHiMv2aErpT0lsvOhidONmv0zZjY2O7pOqdm3FuruDRf6jka_-3brmV_UWJiXHqBHVe94MsLdEuNgAR9JmywzkLqCKAw_GF7hbLhTeU2sVGXrqqSgs3EcMdv0htgtn0QlKq65bl51KES5e8R1umUJ6tpvRuBSMv3-AudlbO2y7A-B7YMLfvaD5wZH5wK9r_zWrrxqe9-6AHsg";
        String serviceAuth = "Y2OJnU9b0DLiVKCq0jvosfDW8JDU8FCQ";

        String[] hosts = StringUtils.split(hostList, ",");

        for (String host : hosts)
        {
            try
            {
                callGLR(host, releasePid, accountId, serviceAuth, endUserAuth);
                System.out.println("GLR passed for host:"+host);
            }
            catch (Exception error)
            {
                System.out.println("GLR FAILED for host:"+host+ ", error:"+error.getMessage());
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

    private String executeSSHCommand(String host, String user, String password, String command)
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

    private void callGLR(String host, String releasePid, String accountId, String serviceToken, String endUserAuth) throws Exception
    {
        String licenseUrl = lwsHostUrl(host);
        String cid = UUID.randomUUID().toString();

        String requestURL = ServiceUriBuilder.create()
            .withUriBuilder(licenseUrl, "/web/License")
            .withForm("json")
            .withSchema(2.6)
            .withHttpTrue()
            .withCid(cid)
            .build();

        LicenseRequest licenseRequest = new LicenseRequest();
        licenseRequest.setAuth(endUserAuth);
        licenseRequest.setProtectionScheme("widevine");
        licenseRequest.setReleasePids(Collections.singletonList(releasePid));

        String getLicenseResponseString = "{\"getLicenseResponse\":" + licenseRequest.toJsonString() + "}";


        String contents = Executor.newInstance().auth(new UsernamePasswordCredentials(accountId, serviceToken))
            .execute(
                Request.Post(requestURL)
                    .bodyString(getLicenseResponseString, ContentType.APPLICATION_JSON)
            )
            .returnContent()
            .asString();

        JsonElement responseBody = new JsonParser().parse(contents);
        JsonObject jsonResponse = responseBody.getAsJsonObject();
        JsonArray getLicenseResponse = jsonResponse.getAsJsonArray("getLicenseResponseResponse");

        if (getLicenseResponse.toString().contains("error"))
        {
            throw new RuntimeException(
                "Error calling license: " + getLicenseResponse.get(0).getAsJsonObject()
                    .getAsJsonPrimitive("error").getAsString());
        }

    }

    private String endUserSignIn()
    {
        return null;
    }


}
