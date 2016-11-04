package com.tpatterson.playground;

import org.apache.commons.lang.time.StopWatch;

import java.io.IOException;

/**
 * Created by tom.patterson on 11/3/16.
 */
public class StopWatchTest
{

    public static void main(String[] args) throws IOException
    {

        StopWatch stopWatch = new StopWatch();

        System.out.println("STARTING STOPWATCH");
        stopWatch.start();

        sleep();

        System.out.println("SUSPENDING STOPWATCH");
        stopWatch.suspend();
        System.out.println("Stopwatch time: " + stopWatch);
        sleep();
        System.out.println("Stopwatch time: " + stopWatch + " (doesn't change since suspended)");
        System.out.println("RESUMING STOPWATCH");
        stopWatch.resume();

        sleep();

        System.out.println("Stopwatch time: " + stopWatch);
        System.out.println("SPLITTING STOPWATCH");
        stopWatch.split();
        sleep();
        // Note: stopWatch must be split to call toSplitString()
        System.out.println("Stopwatch split time: " + stopWatch.toSplitString()
            + " (reported time doesn't change but stopwatch still running)");
        System.out.println("Stopwatch time: " + stopWatch);
        sleep();
        System.out.println("Stopwatch split time: " + stopWatch.toSplitString()
            + " (reported time doesn't change but stopwatch still running)");
        System.out.println("Stopwatch time: " + stopWatch);
        System.out.println("UNSPLITTING STOPWATCH (removes split effect)");
        stopWatch.unsplit();

        sleep();

        System.out.println("STOPPING STOPWATCH");
        stopWatch.stop();
        System.out.println("Stopwatch time: " + stopWatch);

        System.out.println("RESETTING STOPWATCH");
        stopWatch.reset();
        System.out.println("Stopwatch time: " + stopWatch);

        // Note: stopWatch.start() can now be called since reset() was called.
    }

    public static void sleep()
    {
        System.out.println("1 second goes by");
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}