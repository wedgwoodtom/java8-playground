package com.tpatterson.playground.pojo;

public class Apple
{
    private String color;
    private int weight;

    public Apple(String color, int weight)
    {
        this.color = color;
        this.weight = weight;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public static boolean isGreenApple(Apple apple)
    {
        return "green".equals(apple.getColor());
    }

    public static boolean isHeavyApple(Apple apple)
    {
        return apple.getWeight() > 150;
    }

}
