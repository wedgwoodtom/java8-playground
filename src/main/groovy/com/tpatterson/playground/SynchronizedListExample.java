package com.tpatterson.playground;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tom.patterson on 11/3/16.
 */
public class SynchronizedListExample
{

    public static void main(String[] args)
    {

        List<String> syncList = Collections.synchronizedList(new ArrayList<String>());

        syncList.add("one");
        syncList.add("two");
        syncList.add("three");

        // when iterating over a synchronized list, we need to synchronize access to the synchronized list
        synchronized (syncList)
        {
            syncList.stream()
                .forEach( item -> System.out.println("item: " + item));

//            Iterator<String> iterator = syncList.iterator();
//            while (iterator.hasNext())
//            {
//                System.out.println("item: " + iterator.next());
//            }
        }
    }
}