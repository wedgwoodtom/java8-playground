package com.tpatterson.playground;


import java.util.Arrays;
import java.util.List;

/**
 * Generic Methods
 * <p>
 * You can write a single generic method declaration that can be called with arguments of different types.
 * Based on the types of the arguments passed to the generic method, the compiler handles each method call appropriately.
 * Following are the rules to define Generic Methods −
 * <p>
 * - All generic method declarations have a type parameter section delimited by angle brackets (< and >)
 * that precedes the method's return type ( < T > in the next example).
 * <p>
 * - Each type parameter section contains one or more type parameters separated by commas.
 * A type parameter, also known as a type variable, is an identifier that specifies a generic type name.
 * <p>
 * - The type parameters can be used to declare the return type and act as placeholders for
 * the types of the arguments passed to the generic method, which are known as actual type arguments.
 * <p>
 * - A generic method's body is declared like that of any other method. Note that type parameters
 * can represent only reference types, not primitive types (like int, double and char).
 */
public class GenericMethodTest
{
    // generic method printArray
    public static <T> void printArray(T[] inputArray)
    {
        // Display array elements
        for (T element : inputArray)
        {
            System.out.printf("%s ", element);
        }
        System.out.println();
    }

    public static <T> void printList(List<T> inputArray)
    {
        // Display array elements
        for (T element : inputArray)
        {
            System.out.printf("%s ", element);
        }
        System.out.println();
    }


    public static void main(String args[])
    {
        // Create arrays of Integer, Double and Character
        Integer[] intArray = { 1, 2, 3, 4, 5 };
        Double[] doubleArray = { 1.1, 2.2, 3.3, 4.4 };
        Character[] charArray = { 'H', 'E', 'L', 'L', 'O' };

        System.out.println("Array integerArray contains:");
        printArray(intArray);  // pass an Integer array

        System.out.println("\nArray doubleArray contains:");
        printArray(doubleArray);  // pass a Double array

        System.out.println("\nArray characterArray contains:");
        printArray(charArray);  // pass a Character array


        List<String> strings = Arrays.asList("one", "two", "three");
        printList(strings);
    }
}
