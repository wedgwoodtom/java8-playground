package com.tpatterson.playground.builder;

/**
 * Lots of params here.
 */
public class Person
{
    private final String lastName;
    private final String firstName;
    private final String middleName;
    private final String salutation;
    private final String suffix;
    private final String streetAddress;
    private final String city;
    private final String state;
    private final boolean isFemale;
    private final boolean isEmployed;
    private final boolean isHomewOwner;

    public Person(
        final String newLastName,
        final String newFirstName,
        final String newMiddleName,
        final String newSalutation,
        final String newSuffix,
        final String newStreetAddress,
        final String newCity,
        final String newState,
        final boolean newIsFemale,
        final boolean newIsEmployed,
        final boolean newIsHomeOwner)
    {
        this.lastName = newLastName;
        this.firstName = newFirstName;
        this.middleName = newMiddleName;
        this.salutation = newSalutation;
        this.suffix = newSuffix;
        this.streetAddress = newStreetAddress;
        this.city = newCity;
        this.state = newState;
        this.isFemale = newIsFemale;
        this.isEmployed = newIsEmployed;
        this.isHomewOwner = newIsHomeOwner;
    }


    /**
     * Sometimes done as an inner-class
     */
    public static class Builder
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

        public Builder()
        {
        }

        public Builder lastName(String lastName)
        {
            this.lastName = lastName;
            return this;
        }

        public Builder firstName(String firstName)
        {
            this.firstName = firstName;
            return this;
        }

        public Builder middleName(String middleName)
        {
            this.middleName = middleName;
            return this;
        }

        public Builder salutation(String salutation)
        {
            this.salutation = salutation;
            return this;
        }

        public Builder suffix(String suffix)
        {
            this.suffix = suffix;
            return this;
        }

        public Builder streetAddress(String streetAddress)
        {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder city(String city)
        {
            this.city = city;
            return this;
        }

        public Builder state(String state)
        {
            this.state = state;
            return this;
        }

        public Builder female(boolean female)
        {
            this.isFemale = female;
            return this;
        }

        public Builder employed(boolean employed)
        {
            this.isEmployed = employed;
            return this;
        }

        public Builder homeOwner(boolean homeOwner)
        {
            this.isHomeOwner = homeOwner;
            return this;
        }

        // TODO: Could add a validate method that verify that we are building a valid Person

        public Person build()
        {
            return new Person(
                lastName, firstName, middleName, salutation, suffix, streetAddress, city, state, isFemale, isEmployed,
                isHomeOwner);
        }
    }



}