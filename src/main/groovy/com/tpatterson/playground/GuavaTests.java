package com.tpatterson.playground;


import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.theplatform.cs.wholesale.event.object.Asset;
import org.testng.Assert;
import com.google.common.base.Joiner;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuavaTests
{

    @Test
    public void testJoiner()
    {
        String[] fantasyGenres = {"Space Opera", "Horror", "Magic realism", "Religion"};
        String joined = Joiner.on(", ").join(fantasyGenres);
        Assert.assertTrue(joined.contains("Space Opera, "));


        Map<Integer, String> map = new HashMap();
        map.put(1, "Space Opera");
        map.put(2, "Horror");
        map.put(3, "Magic realism");
        String joinedMap = Joiner.on(", ").withKeyValueSeparator(" -> ").join(map);
        Assert.assertTrue(joinedMap.contains("1 -> Space Opera, "));
    }


    @Test
    public void testSplitter()
    {
        String input = "Some very stupid data with ids of invoces like 121432, 3436534 and 8989898 inside";
        Iterable<String> splitted = Splitter.on(" ").split(input);

        // can also do fixed length
        //  char matcher

        String str = "a,,b,     c,,,d";
        Iterable<String> result = Splitter.on(',')
                .omitEmptyStrings()
                .split(str);
    }

    @Test
    public void testMultiMap()
    {
        Multimap<Integer, String> multimap = HashMultimap.create();
        multimap.put(1, "a");
        multimap.put(2, "b");
        multimap.put(3, "c");
        multimap.put(1, "a2");

        Multimap<Integer, String> immutableMultimap = ImmutableSetMultimap.of(1, "a", 2, "b", 3, "c", 1, "a2");

        Multimap<Integer, String> multimapBuilder = new ImmutableSetMultimap.Builder<Integer, String>()
            .put(1, "a")
            .put(2, "b")
            .put(3, "c")
            .put(1, "a2")
            .build();
    }


    @Test
    public void testTable()
    {
        Table<Integer, String, String> table = HashBasedTable.create();
        table.put(1, "a", "1a");
        table.put(1, "b", "1b");
        table.put(2, "a", "2a");
        table.put(2, "b", "2b");
        Table transponedTable = Tables.transpose(table);
    }


    @Test
    public void testListPartition()
    {
        List<String> bigList = Lists.newArrayList("A", "B", "C", "D", "E", "F");

        List<List<String>> batchedList = Lists.partition(bigList, 2);
        int totalBatches = 0;
        for (List<String> batch : batchedList)
        {
            totalBatches += 1;
        }

        Assert.assertTrue(totalBatches==3);
    }


}
