package com.tpatterson.playground;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class SemaphoreTests
{
    @Test
    public void testSemaphoreConnectionLimiter() throws IOException, InterruptedException
    {
        URL someUrl = new URL("http://someUrl");

        ConnectionLimiter connectionLimiter = new ConnectionLimiter(3);
        connectionLimiter.acquire(someUrl);
        connectionLimiter.acquire(someUrl);
        connectionLimiter.acquire(someUrl);
        // would block
        //connectionLimiter.acquire(someUrl);
        connectionLimiter.release(null);
        connectionLimiter.acquire(someUrl);
        connectionLimiter.release(null);
        connectionLimiter.release(null);
        connectionLimiter.release(null);
    }

    @Test
    public void testSemaphorePrintManagerEx() throws IOException, InterruptedException
    {
        PrintManager printManager = new PrintManager();

        // do some printing
        List<Thread> clients = new ArrayList<>();
        for (int i=0; i<10; i++)
        {
            clients.add(new Thread(new PrintClient(printManager, i)));
        }

        clients.forEach(client -> client.start());

        clients.forEach(client -> {
                try
                {
                    client.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            });
    }

    public class ConnectionLimiter
    {
        private final Semaphore semaphore;

        private ConnectionLimiter(int maxConcurrentRequests)
        {
            semaphore = new Semaphore(maxConcurrentRequests);
        }

        public URLConnection acquire(URL url) throws InterruptedException,
            IOException
        {
            semaphore.acquire();
            return url.openConnection();
        }

        public void release(URLConnection conn)
        {
            try
            {
                // clean up
            }
            finally
            {
                semaphore.release();
            }
        }
    }

    public class PrintManager
    {
        private int numberOfPrinters = 3;
        private Semaphore printerMutex = new Semaphore(numberOfPrinters);

        public void print()
        {
            try
            {
                printerMutex.acquire();

                // print - simulate print job
                int oneToTenSecMillis = ThreadLocalRandom.current().nextInt(1000, 10000 + 1);
                Thread.sleep(oneToTenSecMillis);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                printerMutex.release();
            }
        }
    }

    public class PrintClient implements Runnable
    {
        private PrintManager printManager;
        private int clientId;

        public PrintClient(PrintManager printManager, int clientId)
        {
            this.printManager = printManager;
            this.clientId = clientId;
        }

        @Override
        public void run()
        {
            System.out.println("Client " + clientId + " print started");
            printManager.print();
            System.out.println("Client " + clientId + " print complete");
        }
    }

}
