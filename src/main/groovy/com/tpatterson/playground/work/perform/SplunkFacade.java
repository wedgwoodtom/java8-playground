package com.tpatterson.playground.work.perform;

import com.splunk.*;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Simple facade
 */
public class SplunkFacade
{

    public SplunkFacade() throws Exception
    {
//        ServiceArgs loginArgs = new ServiceArgs();
//        loginArgs.setUsername("tom.patterson");
//        loginArgs.setPassword("gh55stly!");
//        loginArgs.setHost("splunk-prod.corp.theplatform.com");
//        loginArgs.setSSLSecurityProtocol(SSLSecurityProtocol.SSLv3);
//        loginArgs.setScheme("https");
//        loginArgs.setPort(80);
//        loginArgs.setPort(8089);

        // https://splunk-prod.corp.theplatform.com/en-US/app/thePlatform/search?earliest=%40d&latest=now&q=search%20host%3D*3tplicws*%20%22miss%22%20%22Perform_LiveSport%22&display.page.search.mode=fast&dispatch.sample_ratio=1&display.general.type=events&display.page.search.tab=events&display.visualizations.charting.chart=bar&sid=1504649052.1130167_86D8F9A5-15C9-47D5-8190-FD18348E6103

//        Service service = Service.connect(loginArgs);
        Service service = createSplunkService("tom.patterson", "gh55stly!",
            "sea1splksrch05", 8089 );

// Retrieve the collection
//        JobCollection jobs = service.getJobs();
//        System.out.println("There are " + jobs.size() + " jobs available to 'admin'\n");
//        for (Job job : jobs.values()) {
//            System.out.println(job.getName());
//        }


//        Entity restApi = service.getConfs().get("limits").get("restapi");
//        int maxResults = Integer.parseInt((String)restApi.get("maxresultrows"));
//        System.out.println("Your system is configured to return a maximum of " + maxResults + " results");

        String mySearch = "search * | head 5";

//        JobExportArgs jobArgs = new JobExportArgs();
//        jobArgs.setSearchMode(JobExportArgs.SearchMode.NORMAL);
//        jobArgs.setEarliestTime("rt-30m");
//        jobArgs.setLatestTime("rt");
//        jobArgs.setOutputMode(JobExportArgs.OutputMode.XML);
//        InputStream stream = service.export(mySearch, jobArgs);
//        printResult(stream);

        Job job = service.getJobs().create(mySearch);
        //Job job = service.getJobs().create(mySearch, jobArgs);


// Wait for the job to finish
        while (!job.isDone()) {
            Thread.sleep(2000);
        }

// Display results
        InputStream results = job.getResults();
        String line = null;
        System.out.println("Results from the search job as XML:\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

    }

    void printResult(InputStream results) throws Exception
    {
        String line = null;
        System.out.println("Results from the search job as XML:\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
    }


    public Service createSplunkService(String username, String password, String splunkHost, int splunkPort)
    {

        // Overriding the static method setSslSecurityProtocol to implement the security protocol of choice
        HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1);

        Service service = new Service(splunkHost, splunkPort);
        String credentials = username+":"+password;
        String basicAuthHeader = new String(Base64.encodeBase64(credentials.getBytes()));
        service.setToken("Basic " + basicAuthHeader);

        return service;
    }



}
