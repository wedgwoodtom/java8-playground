package com.tpatterson.playground.builder;

import org.testng.annotations.Test;

public class PersonBuilderTest
{
    @Test
    public void testBuildPersonWithPersonBuilder()
    {
        Person tom = new PersonBuilder()
            .firstName("Tom")
            .lastName("Patterson")
            .city("Seattle")
            .build();
    }

    @Test
    public void testBuildPersonWithInnerClassBuilder()
    {
        Person tom = new Person.Builder()
            .firstName("Tom")
            .lastName("Patterson")
            .salutation("Mr")
            .female(false)
            .city("Seattle")
            .build();
    }

}
