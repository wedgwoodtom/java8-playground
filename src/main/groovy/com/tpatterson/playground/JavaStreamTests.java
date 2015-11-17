package com.tpatterson.playground;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Just some playground tests to experiment with new APIs
 * <p/>
 * See some of the following:
 * http://www.concretepage.com/java/jdk-8/
 */
public class JavaStreamTests
{
    private List<Movie> movieList = new ArrayList<>();

    @BeforeMethod
    public void setup()
    {
        movieList = Arrays.asList(
                new Movie("Star Wars 1", "MV001"),
                new Movie("Star Wars 2", "MV002"),
                new Movie("Star Wars 3", "MV003"),
                new Movie("Chicken Run", "MV004")
        );
    }

    /**
     * Map a list of objects to a map of objects
     */
    @Test
    public void testStreamToObjectMap()
    {
        Map<String, Movie> titleToMovieMap =
                movieList.stream().collect(
                        Collectors.toMap(Movie::getTitle, movie -> movie));

        Assert.assertTrue(titleToMovieMap.size() == 4);
    }

    @Test
    public void testComparator()
    {
        Comparator<Movie> titleComparitor = Comparator.comparing(Movie::getTitle);
        Collections.sort(movieList, titleComparitor);
        movieList.forEach(s -> System.out.println(s.getTitle() + " " + s.getId()));
    }


    @Test
    public void testMultiComparator()
    {
        Comparator<Movie> titleComparitor = Comparator.comparing(Movie::getTitle);
        Comparator<Movie> idComparitor = Comparator.comparing(Movie::getId);
        Collections.sort(movieList, titleComparitor.thenComparing(idComparitor));
        movieList.forEach(s -> System.out.println(s.getTitle() + " " + s.getId()));
    }

    /**
     * Map a list of objects to a map of objects
     */
    @Test
    public void testStreamToStringMap()
    {
        Map<String, String> titleToMovieMap =
                movieList.stream().collect(
//                        Collectors.toMap(Movie::getTitle, movie -> movie.id));
                        Collectors.toMap(Movie::getTitle, Movie::getId));

        Assert.assertTrue(titleToMovieMap.size() == 4);
    }

    /**
     * Map a list of objects to a map of objects
     */
    @Test
    public void testFilterStreamToObject()
    {
        Movie desiredMovie =
                movieList.stream().filter(movie -> movie.getId().equals("MV003")).findFirst().get();

        Assert.assertTrue(desiredMovie.getId().equals("MV003"));
    }

    /**
     * Map a list of objects to a map of objects
     */
    @Test
    public void testFilterStreamToCollection()
    {
        List<Movie> starWarsMovies =
                movieList.stream().filter(movie -> movie.getTitle().startsWith("Star Wars")).collect(Collectors.toList());

        Assert.assertTrue(starWarsMovies.size() == 3);
    }


    @Test
    public void testSumming()
    {
        List<Integer> list = Arrays.asList(30, 10, 20, 35);
        int result = list.stream().collect(Collectors.summingInt(i -> i));
        Assert.assertTrue(result == 95);
    }

    @Test
    public void testMinMax()
    {
        List<Integer> list = Arrays.asList(30, 10, 20, 35);

        int max;
        int min;

        //Get Max
        max = list.stream().collect(Collectors.maxBy(new IntegerComp())).get();
        //Get Min
        min = list.stream().collect(Collectors.minBy(new IntegerComp())).get();

        Assert.assertTrue(max == 35);
        Assert.assertTrue(min == 10);
    }


    @Test
    public void toSet()
    {
        Set<String> set = Stream.of("AA", "AA", "BB").collect(Collectors.toSet());
        set.forEach(s -> System.out.println(s));
    }

    @Test
    public void toMap()
    {
        Map<String, String> map = Stream.of("AA", "BB", "CC").collect(Collectors.toMap(k -> k, v -> v + v));
        map.forEach((k, v) -> System.out.println("key:" + k + "  value:" + v));
    }


    @Test
    public void testStringJoiner()
    {
        StringJoiner sj = new StringJoiner("-");
        sj.add("Ram");
        System.out.println(sj);
        sj.add("Shyam").add("Mohan");
        System.out.println(sj);
    }

    @Test
    public void testFilterWithPredicate()
    {
        List<Employee> list = Employee.getEmpList();
        Predicate<Employee> filterPredicate = e -> e.id > 1 && e.sal < 6000;
        Consumer<Employee> printConsumer = e -> System.out.println(e.id + ", " + e.sal);
        list.stream().filter(filterPredicate).forEach(printConsumer);
    }

    @Test
    public void testMapObjectToObject()
    {
        List<Employee> list = Employee.getEmpList();
        Stream<Player> players = list.stream().map(e -> new Player(e.id, e.name));
        players.forEach(p -> System.out.println(p.id + ", " + p.name));
    }

    @Test
    public void testAnyAllNoneMatch()
    {
        Predicate<Employee> p1 = e -> e.id < 10 && e.name.startsWith("A");
        Predicate<Employee> p2 = e -> e.sal < 10000;
        List<Employee> list = Employee.getEmpList();
        //using allMatch
        boolean b1 = list.stream().allMatch(p1);
        System.out.println(b1);
        boolean b2 = list.stream().allMatch(p2);
        System.out.println(b2);
        //using anyMatch
        boolean b3 = list.stream().anyMatch(p1);
        System.out.println(b3);
        boolean b4 = list.stream().anyMatch(p2);
        System.out.println(b4);
        //using noneMatch
        boolean b5 = list.stream().noneMatch(p1);
        System.out.println(b5);
    }

    class IntegerComp implements Comparator<Integer>
    {
        @Override
        public int compare(Integer i1, Integer i2)
        {
            if (i1 >= i2)
            {
                return 1;
            } else
            {
                return -1;
            }
        }
    }

}
