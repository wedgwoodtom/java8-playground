package com.tpatterson.playground;

import com.tpatterson.playground.pojo.Movie;
import org.apache.commons.lang.ObjectUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ToStringTests
{

    @Test
    public void testApacheCommonsObjectUtilsToString()
    {
        Movie nullMovie = null;
        Assert.assertEquals(ObjectUtils.toString(nullMovie), "");

        Movie movie = new Movie("title", "id", "genre");
        Assert.assertTrue(ObjectUtils.toString(movie).contains("title"));
    }

}
