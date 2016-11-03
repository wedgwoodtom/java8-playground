package com.tpatterson.playground;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom.patterson on 11/3/16.
 * <p>
 * Generic Classes
 * <p>
 * A generic class declaration looks like a non-generic class declaration, except that the class
 * name is followed by a type parameter section.
 * <p>
 * As with generic methods, the type parameter section of a generic class can have one or more
 * type parameters separated by commas. These classes are known as parameterized classes or
 * parameterized types because they accept one or more parameters.
 */

public class GenericClassTest<T>
{
    private T t;
    private List<T> tList = new ArrayList<T>();

    public void add(T t)
    {
        this.t = t;
        tList.add(t);
    }

    public T get()
    {
        return t;
    }

    public static void main(String[] args)
    {
        GenericClassTest<Integer> integerBox = new GenericClassTest<Integer>();
        GenericClassTest<String> stringBox = new GenericClassTest<String>();

        integerBox.add(new Integer(10));
        stringBox.add(new String("Hello World"));

        System.out.printf("Integer Value :%d\n\n", integerBox.get());
        System.out.printf("String Value :%s\n", stringBox.get());
    }
}