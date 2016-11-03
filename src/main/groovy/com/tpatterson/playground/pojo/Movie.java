package com.tpatterson.playground.pojo;

import java.util.Optional;

public class Movie
{
    private String title;
    private String id;
    private String genre;
    private Optional<String> description;

    public Movie(String title, String id, String genre)
    {
        this.title = title;
        this.id = id;
        this.genre = genre;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }


    public Optional<String> getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = Optional.ofNullable(description);
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public void printData()
    {
        System.out.println("Title:"+getTitle() +" and Id:"+getId());
    }

    @Override
    public String toString()
    {
        return "Movie{" +
            "title='" + title + '\'' +
            ", id='" + id + '\'' +
            ", genre='" + genre + '\'' +
            ", description=" + description +
            '}';
    }
}
