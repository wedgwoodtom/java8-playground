package com.tpatterson.playground.java8;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Java8LambdaTests
{
    // Check the Java8StreamTests for examples as well

    @Test
    public void testPredicateFunctionalInterface()
    {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        System.out.println("Print all numbers:");
        evaluate(list, (n) -> true);

        System.out.println("Print no numbers:");
        evaluate(list, (n) -> false);

        System.out.println("Print even numbers:");
        evaluate(list, (n) -> n % 2 == 0);

        System.out.println("Print odd numbers:");
        evaluate(list, (n) -> n % 2 == 1);

        System.out.println("Print numbers greater than 5:");
        evaluate(list, (n) -> n > 5);
    }

    @Test
    public void testSimpleConsumer()
    {
        //Opposite a Supplier, a Consumer acts upon a value but returns nothing
        Consumer<Integer> consumer = x -> display(x - 1);
        // Use the consumer with three numbers.
        consumer.accept(1);
        consumer.accept(2);
        consumer.accept(3);
    }

    @Test
    public void testSimpleSupplier()
    {
        // A Supplier provides values. We call get() on it to retrieve its value—it may return different values when called more than once.
        // Pass lambdas to the display method.
        // ... These conform to the Supplier class.
        // ... Each returns an Integer.
        display(() -> 10);
        display(() -> 100);
        display(() -> (int) (Math.random() * 100));
    }

    // Add a better Supplier/COnsumer example from here
    // https://dserradji.wordpress.com/2015/03/30/a-java-8-variation-on-the-producer-consumer-pattern/

    @Test
    public void testRemoveIf()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("cat");
        list.add("dog");
        list.add("cheetah");
        list.add("deer");

        // Remove elements that start with c.
        list.removeIf(element -> element.startsWith("c"));
        System.out.println(list.toString());
    }

    public static void display(Supplier<Integer> arg)
    {
        System.out.println(arg.get());
    }

    public static void display(int value)
    {

        switch (value)
        {
            case 1:
                System.out.println("There is 1 value");
                return;
            default:
                System.out.println(
                    "There are " + Integer.toString(value)
                        + " values");
                return;
        }
    }


    public static void evaluate(List<Integer> list, Predicate<Integer> predicate)
    {
        for (Integer n : list)
        {
            if (predicate.test(n))
            {
                System.out.println(n + " ");
            }
        }
    }
}
