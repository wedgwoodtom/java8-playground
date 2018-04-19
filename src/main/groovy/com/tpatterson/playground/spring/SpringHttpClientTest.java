package com.tpatterson.playground.spring;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SpringHttpClientTest
{
    @Test
    public void testGet() throws Exception
    {
        HttpClient httpClient = new HttpClient();

        String contents = httpClient
            .executeGet("http://www.google.com")
            .getBody();

        Assert.assertTrue(!contents.isEmpty());
    }
}
