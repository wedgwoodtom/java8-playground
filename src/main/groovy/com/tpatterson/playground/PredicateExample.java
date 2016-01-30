package com.tpatterson.playground;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PredicateExample
{
    private List<Apple> inventory = new ArrayList<>();

    @BeforeMethod
    public void setup()
    {
        inventory = Arrays.asList(
                new Apple("green", 10),
                new Apple("red", 20)
        );
    }

    @Test
    public void testBasicFilterWithPredicate()
    {
        List<Employee> list = Employee.getEmpList();
        Predicate<Employee> employeePredicate = e -> e.id > 1 && e.sal < 6000;
        Consumer<Employee> printConsumer = e -> System.out.println(e.id + ", " + e.sal);
        list.stream().filter(employeePredicate).forEach(printConsumer);
    }

    @Test
    public void filterApples()
    {
        Assert.assertTrue(
                filterApples(inventory, Apple::isGreenApple).size() == 1
        );

        // Also, with just a lambda
        Assert.assertTrue(
            filterApples(inventory, (Apple a) -> "green".equals(a.getColor()) ).size() == 1
        );


        /**
         * So you don’t even need to write a method definition that’s used only once; the code is crisper and clearer
         * because you don’t need to search to find the code you’re passing. But if such a lambda exceeds a few lines
         * in length (so that its behavior isn’t instantly clear),
         * then you should instead use a method reference to a method with a descriptive name
         */
    }

    static List<Apple> filterApples(List<Apple> inventory,
                                    Predicate<Apple> p)
    {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : inventory)
        {
            if (p.test(apple))
            {
                result.add(apple);
            }
        }
        return result;
    }


}
