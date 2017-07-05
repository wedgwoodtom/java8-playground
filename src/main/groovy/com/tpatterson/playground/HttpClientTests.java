package com.tpatterson.playground;

import ch.qos.logback.classic.util.CopyOnInheritThreadLocal;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HttpClientTests
{

    @Test
    public void testGet() throws Exception
    {
        String contents = Request.Get("http://www.google.com")
                .execute()
                .returnContent()
                .asString();

        Assert.assertTrue(!contents.isEmpty());
    }

    @Test
    public void testPostForGLR() throws Exception
    {
        /**
         * Request.Post("http://targethost/login")
         .bodyForm(Form.form().add("username",  "vip").add("password",  "secret").build())
         .execute().returnContent();
         */

        String request = String.format("%s = %d", "joe", 35);

        String glrResponse = Request.Post("http://targethost/login")
                .bodyString("", ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();

        Assert.assertTrue(glrResponse.matches("(.*)MATCHVALUE(.*)"));

    }

}
