package com.tpatterson.playground;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Semaphore;

public class SemaphoreTests
{

    @Test
    public void testSemaphore() throws IOException, InterruptedException
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
}
