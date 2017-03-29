package com.tpatterson.playground;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by tompatterson on 3/28/17.
 */
public class ConvertFileToJson
{
    @Test
    public void testConvertFile() throws Exception
    {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("convertMe.txt").getFile());

        JsonObject jsonObject = new JsonObject();

        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            System.out.println(line);

            String[] lineParts = line.split(Pattern.quote(".\t"));
            if (lineParts.length == 2)
            {
                String key = "//"+lineParts[0]+"/";
                String value = lineParts[1];
                System.out.println("key: "+key+" = "+ value);
                jsonObject.addProperty(key, value);
            }
            else
            {
                System.out.println("Skipping: " + line);
            }


        }


       System.out.println(jsonObject);

    }
}
