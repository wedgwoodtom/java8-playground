package com.tpatterson.playground;

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

        String utilsToString = ObjectUtils.toString(movie);
        Assert.assertTrue(utilsToString.contains("title"));

        System.out.println(utilsToString);

    }

}
