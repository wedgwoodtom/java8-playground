package com.tpatterson.playground.work.perform;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by tom.patterson on 9/5/17.
 *
 * Use Splunk API to analyze cache hits for LWS calls (and later Widevine)
 *
 */
public class PerformAccountCacheAudit
{
    @Test
    public void calculateCacheStats() throws Exception
    {
        System.out.println("Building list of LICWS calls");
        List<String> licwsCids = extractCidList("/data/licwsCalls.txt");
        System.out.println("Found "+licwsCids.size()+" LICWS Calls");

        System.out.println("Building list of EDS calls");
        List<String> edsCids = extractCidList("/data/edsCalls.txt");
        System.out.println("Found "+edsCids.size()+" EDS Calls");
        Map<String, String> edsCidMap = new HashMap<>();
        for (String cid :  edsCids)
        {
            edsCidMap.put(cid, cid);
        }

        int cacheMiss = 0;
        for (String licwsCid: licwsCids)
        {
            if (edsCidMap.containsKey(licwsCid))
            {
                cacheMiss++;
            }
        }
        int n = licwsCids.size();
        System.out.println("Cache miss rate is "+(float)cacheMiss/n +" for "+n+" LICWS calls.");
    }

    private List<String> extractCidList(String filePath) throws IOException
    {
        List<String> cids = new ArrayList<>();
        Path file = new File(filePath).toPath();
        try (Stream<String> lines = Files.lines (file, StandardCharsets.UTF_8))
        {
            for (String line : (Iterable<String>) lines::iterator)
            {
//                System.out.println(line);
                String cid = extractCid(line);
                if (cid != null)
                {
                    cids.add(cid);
//                    System.out.println(cid);
                }

            }
        }
        return cids;
    }

    private String extractCid(String logLine)
    {
        // TODO: Hack to get the cid, try using RE
        int cidStart = logLine.indexOf("cid=");
        int quoteIndex = logLine.indexOf("\"", cidStart+4);
        int ampIndex = logLine.indexOf("&", cidStart+4);
        int cidEnd = -1;
        if (quoteIndex == -1)
        {
            cidEnd = ampIndex;
        }
        else if (ampIndex == -1)
        {
            cidEnd = quoteIndex;
        }
        else
        {
            cidEnd = Math.min(quoteIndex, ampIndex);
        }

        if (cidStart>0 && cidEnd>cidStart)
        {
            return logLine.substring(cidStart+4, cidEnd);
        }

        return null;
    }

}




// Use Apache
//        File file = new File("/data/licwsCalls.txt");
//        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
//        try
//        {
//            while (it.hasNext())
//            {
//                String line = it.nextLine();
//                System.out.println(line);
//                // do something with line
//            }
//        } finally
//        {
//            LineIterator.closeQuietly(it);
//        }

