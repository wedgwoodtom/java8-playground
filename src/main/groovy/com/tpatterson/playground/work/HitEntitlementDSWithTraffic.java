package com.tpatterson.playground.work;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * A test to repro EDS deploy issue XXX
 */
public class HitEntitlementDSWithTraffic
{
    private static String RO_TOKEN = "BsAN9B3K3yUcDr769wosQXB4kOBi0JCK";  // team.phobos@theplatform.com
    private static String RW_TOKEN = "udinx-bItYWKXt48dwpkUVBmQOCCoNDO";  // tom.patterson@theplatform.com for Prod account for Entitlement update


    private static long TEST_RUNTIME_MINUTES = 5;
    private static long TEST_RUNTIME = TimeUnit.MINUTES.toMillis(TEST_RUNTIME_MINUTES);
    private static int NUM_THREADS = 100;

    private static String EDS_URL_RW = "http://sea1tpentds09.sea1.com:10500/eds";
    private static String EDS_URL_RO1 = "http://phl1tpentds51.corp.theplatform.com:10500/eds";
    private static String EDS_URL_RO2 = "http://phl1tpentds52.corp.theplatform.com:10500/eds";
    private static String EDS_URL_RO_VIP = "http://phl1.read.data.entitlement.theplatform.com/eds";

//    private static String EDS_URL_SEA_RO1 = "http://sea1tp2entds53.sea1.com:10500/eds";
    private static String EDS_URL_SEA_RO1 = "http://sea1tpentds51.sea1.com:10500/eds";

    @Test
    public void generateTraffic() throws Exception
    {
        System.out.println("Starting Test");
        ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
        threadPool.submit(new EntitlementWriter(EDS_URL_RW));
        for (int n=0; n<10; n++)
        {
//            threadPool.submit(new EntitlementReader(EDS_URL_RO_VIP));
//            threadPool.submit(new EntitlementReader(EDS_URL_RO1));
//            threadPool.submit(new EntitlementReader(EDS_URL_RO2));
            threadPool.submit(new EntitlementReader(EDS_URL_SEA_RO1));
            threadPool.submit(new AliveCheck(EDS_URL_SEA_RO1));

            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "SE GO TVE"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "Astral TVE"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "Telecable - TVE"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "KevinHobbs (VMS)"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "Sky Navajo - Prod"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "FLAC FOD BRA"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "FLAC FOD LATAM"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "TV2 Play"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "Moviecity"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "Sky Go TV - Prod"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "Telecable - TVE"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "NBCU TVE - G4"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "NBCU TVE Stage"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "NBCU TVE Stage - NewsCOM"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "NBCU TVE Stage - AOL"));
            threadPool.submit(new DRReader(EDS_URL_SEA_RO1, "NBCU TVE Stage - Sprout"));

//            threadPool.submit(new MultiEntitlementReader(EDS_URL_RO_VIP));
//            threadPool.submit(new MultiEntitlementReader(EDS_URL_RO1));
//            threadPool.submit(new MultiEntitlementReader(EDS_URL_RO2));
//            threadPool.submit(new MultiEntitlementReader(EDS_URL_SEA_RO1));
        }

        threadPool.awaitTermination(TEST_RUNTIME_MINUTES, TimeUnit.MINUTES);
        System.out.println("Test Complete");
    }

    static void checkForError(String response)
    {
        if (response.contains("\"isException\": true"))
        {
            throw new RuntimeException("Error response from service!!" + response);
        }
    }

    private class EntitlementWriter implements Runnable
    {
        private String edsUrl;
        private long stopTime;

        public EntitlementWriter(String edsUrl)
        {
            stopTime = System.currentTimeMillis() + TEST_RUNTIME;
            this.edsUrl = edsUrl;
        }

        public void run()
        {
            int i=0;
            while (System.currentTimeMillis() < stopTime)
            {
                i++;
                try
                {
                    String update = "{\n"
                        + "    \"id\": \""+edsUrl+"/data/Entitlement/89919433"+"\",\n"
                        + "    \"description\": \""+ UUID.randomUUID()+"\"\n"
                        + "}";

                    String response = Request.Post(
                        edsUrl + "/data/Entitlement?schema=1.3.8&form=cjson&pretty=true&token="+RW_TOKEN)
                        .bodyString(update, ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent()
                        .asString();

                    checkForError(response);
//                    System.out.println(i + " = " + response);
                }
                catch(Exception error)
                {
                    System.out.println("ERROR!!");
                    error.printStackTrace();
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException ignored)
                {
                }
            }

        }
    }

    private class DRReader implements Runnable
    {
        private String edsUrl;
        private String account;
        private long stopTime;

        public DRReader(String edsUrl, String account)
        {
            stopTime = System.currentTimeMillis() + TEST_RUNTIME;
            this.edsUrl = edsUrl;
            this.account = account;
        }

        public void run()
        {
            int i=0;
            while (System.currentTimeMillis() < stopTime)
            {
//                i++;
                i=1;
                try
                {
                    // http://sea1tpentds51.sea1.com:10500/eds/data/DistributionRight?schema=1.3.8&form=json&pretty=true&range=1-1&count=true
                    String contents = Request.Get(
                        edsUrl + "/data/DistributionRight?schema=1.3.8&form=cjson&pretty=true&range=" +i+ "-"+ i + "&token="+RO_TOKEN
                            +"&account="+URLEncoder.encode(account))
                        .execute()
                        .returnContent()
                        .asString();

                    checkForError(contents);
//                    System.out.println(1 + " = " + contents);
                }
                catch(Exception error)
                {
                    System.out.println("ERROR!!");
                    error.printStackTrace();
                }
            }
        }
    }


    private class EntitlementReader implements Runnable
    {
        private String edsUrl;
        private long stopTime;

        public EntitlementReader(String edsUrl)
        {
            stopTime = System.currentTimeMillis() + TEST_RUNTIME;
            this.edsUrl = edsUrl;
        }

        public void run()
        {
            int i=0;
            while (System.currentTimeMillis() < stopTime)
            {
                i++;
                try
                {
                    String contents = Request.Get(
                        edsUrl + "/data/Entitlement/89919433?schema=1.3.9&form=cjson&pretty=true&range=1-" + i + "&token="+RW_TOKEN
                            +"&account=KevinHobbs%20(VMS)")
                        .execute()
                        .returnContent()
                        .asString();

                    checkForError(contents);
//                    System.out.println(1 + " = " + contents);
                }
                catch(Exception error)
                {
                    System.out.println("ERROR!!");
                    error.printStackTrace();
                }
            }
        }
    }

    private class MultiEntitlementReader implements Runnable
    {
        private String edsUrl;
        private long stopTime;

        public MultiEntitlementReader(String edsUrl)
        {
            stopTime = System.currentTimeMillis() + TEST_RUNTIME;
            this.edsUrl = edsUrl;
        }

        public void run()
        {
            int i=0;
            while (System.currentTimeMillis() < stopTime)
            {
                i++;
                try
                {
                    String contents = Request.Get( edsUrl + "/data/Entitlement?schema=1.3.9&form=cjson&pretty=true&range=" +i+"-"+i+ "&token="+RO_TOKEN+"&account=Disney%20Preview")
                        .execute()
                        .returnContent()
                        .asString();

                    checkForError(contents);
//                    System.out.println(i + " = " + contents);
                }
                catch(Exception error)
                {
                    System.out.println("ERROR!!");
                    error.printStackTrace();
                }
            }
        }
    }

    private class AliveCheck implements Runnable
    {
        private String edsUrl;
        private long stopTime;

        public AliveCheck(String edsUrl)
        {
            stopTime = System.currentTimeMillis() + TEST_RUNTIME;
            this.edsUrl = edsUrl;
        }

        public void run()
        {
            // http://sea1tpentds51.sea1.com:10500/eds/management/alive
            int i=0;
            while (System.currentTimeMillis() < stopTime)
            {
                i++;
                try
                {
                    byte[] encodedBytes = Base64.encodeBase64("stats:stizzats".getBytes());
                    String USER_PASS = new String(encodedBytes);
                    String contents = Request.Get( edsUrl + "/management/alive")
                        .addHeader("Authorization", USER_PASS)
                        .execute()
                        .returnContent()
                        .asString();

                    checkForError(contents);
                    if (!contents.contains("Web Service is Ok"))
                    {
                        throw new RuntimeException("Web Service is NOT Ok!");
                    }
                }
                catch(Exception error)
                {
                    System.out.println("ERROR!!");
                    error.printStackTrace();
                }
            }
        }
    }


}


