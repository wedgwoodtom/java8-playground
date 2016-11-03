package com.tpatterson.playground.builder;

import org.testng.annotations.Test;

public class PersonBuilderTest
{
    @Test
    public void testBuildPerson()
    {
        Person tom = new PersonBuilder()
            .setFirstName("Tom")
            .setLastName("Patterson")
            .setCity("Seattle")
            .createPerson();
    }
}
