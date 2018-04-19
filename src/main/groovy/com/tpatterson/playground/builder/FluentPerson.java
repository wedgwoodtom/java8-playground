package com.tpatterson.playground.builder;

/**
 *  Seems a bit better - I like the fluent style and implementation - less code, more readable, just seems simpler
 *
 *  import static insurance.FireItemBuilder.fireItem;
 // ...


 */
public class FluentPerson
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
    private boolean isHomewOwner;

    public FluentPerson()
    {}

    public String lastName()
    {
        return lastName;
    }

    public FluentPerson lastName(String lastName)
    {
        this.lastName = lastName;
        return this;
    }

    public String firstName()
    {
        return firstName;
    }

    public FluentPerson firstName(String firstName)
    {
        this.firstName = firstName;
        return this;
    }

    public String middleName()
    {
        return middleName;
    }

    public FluentPerson middleName(String middleName)
    {
        this.middleName = middleName;
        return this;
    }

    public String salutation()
    {
        return salutation;
    }

    public FluentPerson salutation(String salutation)
    {
        this.salutation = salutation;
        return this;
    }

    public String suffix()
    {
        return suffix;
    }

    public FluentPerson suffix(String suffix)
    {
        this.suffix = suffix;
        return this;
    }

    public String streetAddress()
    {
        return streetAddress;
    }

    public FluentPerson streetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
        return this;
    }

    public String city()
    {
        return city;
    }

    public FluentPerson city(String city)
    {
        this.city = city;
        return this;
    }

    public String state()
    {
        return state;
    }

    public FluentPerson state(String state)
    {
        this.state = state;
        return this;
    }

    public boolean isFemale()
    {
        return isFemale;
    }

    public FluentPerson female(boolean female)
    {
        isFemale = female;
        return this;
    }

    public boolean isEmployed()
    {
        return isEmployed;
    }

    public FluentPerson employed(boolean employed)
    {
        isEmployed = employed;
        return this;
    }

    public boolean isHomewOwner()
    {
        return isHomewOwner;
    }

    public FluentPerson homewOwner(boolean homewOwner)
    {
        isHomewOwner = homewOwner;
        return this;
    }
}