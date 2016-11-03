package com.tpatterson.playground.java8;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Java8ThreadTests
{
    @Test
    public void testLambdaToRunnable()
    {
        Thread[] threads = {

            // Pass a lambda to a thread
            new Thread(
                () -> {
                    longOperation();
                }),

            // Pass a method reference to a thread
            new Thread(Java8ThreadTests::longOperation)
        };

        // Start all threads
        Arrays.stream(threads).forEach(Thread::start);

        // Join all threads
        Arrays.stream(threads).forEach(
            thread -> {
                try
                {
                    thread.join();
                }
                catch (InterruptedException ignore)
                {
                }
            });
    }

    @Test
    public void testLambdaWithParams()
    {
        String id = "MyId";
        int numTimes = 230;

        new Thread(
            () -> longOperationWithParams(id, numTimes)
        ).start();
    }

    @Test
    public void testExecuter()
    {
        ExecutorService service = Executors
            .newFixedThreadPool(5);

        Future[] answers = {
            service.submit(() -> longOperation()),
            service.submit(Java8ThreadTests::longOperation)
        };

        Arrays.stream(answers).forEach(
            answer -> {
                try
                {
                    System.out.println(answer.get());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        );
    }

    public int longOperationWithParams(String id, int numTimes)
    {
        System.out.println(
            "Running on thread #"
                + Thread.currentThread().getId()
                + " with params: " + id + ", " + numTimes);

        // [...]
        return 42;
    }

    public static int longOperation()
    {
        System.out.println(
            "Running on thread #"
                + Thread.currentThread().getId());

        // [...]
        return 42;
    }
}
