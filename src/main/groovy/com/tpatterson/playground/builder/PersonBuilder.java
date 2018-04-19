package com.tpatterson.playground.builder;

/**
 * Too many params - use a builder!
 * <p>
 * This is an external class builder, but some folks make it as an internal class as well.
 *
 *
 *
 * Another build-use example:
 * String requestURL = ServiceUriBuilder.create()
 .withUriBuilder(fairplayUrl, "/web/FairPlay")
 .withAccount(accountId)
 .withQueryParam("token", token)
 .withForm("json")
 .withSchema(1.0)
 .withCid(cid)
 .build();

 *
 */

public class PersonBuilder
{
    private String lastName;
    private String firstName;
    private String middleName;
    private String salutation;
    private String suffix;
    private String streetAddress;
    private String city;
    private String state;
    private boolean isFemale;
    private boolean isEmployed;
    private boolean isHomeOwner;

    public PersonBuilder()
    {
    }

    public PersonBuilder lastName(String lastName)
    {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder firstName(String firstName)
    {
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder middleName(String middleName)
    {
        this.middleName = middleName;
        return this;
    }

    public PersonBuilder salutation(String salutation)
    {
        this.salutation = salutation;
        return this;
    }

    public PersonBuilder suffix(String suffix)
    {
        this.suffix = suffix;
        return this;
    }

    public PersonBuilder streetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
        return this;
    }

    public PersonBuilder city(String city)
    {
        this.city = city;
        return this;
    }

    public PersonBuilder state(String state)
    {
        this.state = state;
        return this;
    }

    public PersonBuilder female(boolean female)
    {
        this.isFemale = female;
        return this;
    }

    public PersonBuilder employed(boolean employed)
    {
        this.isEmployed = employed;
        return this;
    }

    public PersonBuilder homeOwner(boolean homeOwner)
    {
        this.isHomeOwner = homeOwner;
        return this;
    }

    public Person build()
    {
        return new Person(
            lastName, firstName, middleName, salutation, suffix, streetAddress, city, state, isFemale, isEmployed,
            isHomeOwner);
    }
}