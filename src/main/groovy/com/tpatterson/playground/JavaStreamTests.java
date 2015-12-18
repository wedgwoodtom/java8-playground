package com.tpatterson.playground;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Year;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
                new Movie("Star Wars 1", "MV001", "SciFi"),
                new Movie("Star Wars 2", "MV002", "SciFi"),
                new Movie("Star Wars 3", "MV003", "SciFi"),
                new Movie("Chicken Run", "MV004", "Animated"),
                new Movie("Saw", "MV005", "Horror")
        );
    }

    /**
     * Map a list of objects to a map of objects
     */
    @Test
    public void testStreamToObjectMap()
    {
        Map<String, Movie> titleToMovieMap =
                movieList
                    .stream()
                    .collect(Collectors.toMap(Movie::getTitle, movie -> movie));

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
                movieList
                    .stream()
                    .collect(
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
                movieList
                    .stream()
                    .filter(movie -> movie.getId().equals("MV003"))
                    .findFirst().get();

        Assert.assertTrue(desiredMovie.getId().equals("MV003"));
    }

    /**
     * Map a list of objects to a map of objects
     */
    @Test
    public void testFilterStreamToCollection()
    {
        List<Movie> starWarsMovies =
                movieList
                    .stream()
                    .filter(movie -> movie.getTitle().startsWith("Star Wars"))
                    .collect(Collectors.toList());

        Assert.assertTrue(starWarsMovies.size() == 3);
    }


//           roster
//                .stream()
//                .filter(
//                        p -> p.getGender() == Person.Sex.MALE
//                                && p.getAge() >= 18
//                                && p.getAge() <= 25)
//                .map(p -> p.getEmailAddress())
//                .forEach(email -> System.out.println(email));
//


    @Test
    public void testSumming()
    {
        List<Integer> list = Arrays.asList(30, 10, 20, 35);
        int result = list
            .stream()
            .collect(Collectors.summingInt(i -> i));
        Assert.assertTrue(result == 95);
    }

    @Test
    public void testMinMax()
    {
        List<Integer> list = Arrays.asList(30, 10, 20, 35);

        int max;
        int min;

        //Get Max
        max = list
            .stream()
            .collect(Collectors.maxBy(new IntegerComp())).get();
        //Get Min
        min = list
            .stream()
            .collect(Collectors.minBy(new IntegerComp())).get();

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
        list.stream()
            .filter(filterPredicate)
            .forEach(printConsumer);
    }

    @Test
    public void testPredicateAnd()
    {
        Predicate<Movie> horror = movie -> "Horror".equals(movie.getGenre());

        Predicate<Movie> animated = movie -> {
            return "Animated".equals(movie.getGenre());
        };

        List<Movie> movies = movieList.stream()
            .filter(horror
                    .or(animated))
            .collect(Collectors.toList());
        movies.forEach((Movie s) -> System.out.println("Title:"+s.getTitle() +" and Id:"+s.getId()));
    }


    @Test
    public void testMapObjectToObject()
    {
        List<Employee> list = Employee.getEmpList();
        Stream<Player> players = list.stream().map(e -> new Player(e.id, e.name));
        players.forEach(p -> System.out.println(p.id + ", " + p.name));
    }

    @Test
    public void testSortArray()
    {
        String[] strArray = {"abe","adb","deb","abc","ghi","acd","acg","acb"};
        Arrays.sort(strArray,String::compareToIgnoreCase);
        Arrays.stream(strArray).forEach(System.out::println);
        Stream.of(strArray).forEach(s -> System.out.println(s));
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

    @Test
    public void testFunction()
    {
        Function<Integer,String> ob = f1 -> "Age:"+f1;
        System.out.println(ob.apply(20));
    }


    @Test
    public void testLambdaCalculator()
    {
        Calculator cal =(int a, int b) -> a+b;
        int res = cal.add(5, 6);
        System.out.println(res);
    }

    @Test
    public void testLambdaComparitor()
    {
        Comparator<Movie> comp= (Movie m1, Movie m2) -> m1.getTitle().compareTo(m2.getTitle());
        Collections.sort(movieList, comp);
        System.out.println("...after sorting...");
        for(Movie m : movieList){
            System.out.println(m.getTitle());
        }

    }

    @Test
    public void testForEach()
    {
       // 1, Consumer interface example
       Consumer<Movie> style = (Movie s) -> System.out.println("Title:"+s.getTitle() +" and Id:"+s.getId());
       movieList.forEach(style);

       // 2, method reference
       movieList.forEach(Movie::printData);

       // 3, lambda
       movieList.forEach(m -> m.printData());

       movieList.forEach(m -> {
               m.printData();
           });
    }

    @Test
    public void testOptional()
    {
        Movie movie = new Movie("title", "id", "genre");
        movie.setDescription(null);

        String movieDescription = movie.getDescription().orElse("None");
        Assert.assertTrue(movieDescription.equals("None"));

        movie.setDescription("description");
        movieDescription = movie.getDescription().orElse("None");
        Assert.assertTrue(movieDescription.equals("description"));
    }


    @Test
    public void testYear()
    {
        System.out.println("Year.now():"+Year.now());
        System.out.println("Year.MAX_VALUE:"+Year.MAX_VALUE);
        System.out.println("Year.isLeap(2014):"+Year.isLeap(2014));
        System.out.println("Year.isLeap(2016):"+ Year.isLeap(2016));
    }

    @Test
    public void testConcatStream()
    {
        List<String> list1 = Arrays.asList("A1","A2","A3");
        List<String> list2 = Arrays.asList("B1","B2","B3");
        Stream<String> resStream = Stream.concat(list1.stream(), list2.stream());
        resStream.forEach(s->System.out.println(s));
    }

    @Test
    public void testStreamOf()
    {
        Stream.of("Ram","Shyam","Mohan").forEach(s->System.out.println(s));
    }

    @Test
    public void testFlatmap()
    {
        // Stream.flatMap returns the stream object. We need to pass a function as an argument.
        // Function will be applied to each element of stream.
        List<String> list1 = Arrays.asList("AAA","BBB");
        List<String> list2 = Arrays.asList("CCC","DDD");
        Stream.of(list1,list2).flatMap(list -> list.stream()).forEach(s->System.out.println(s));

    }

    @Test
    public void testStreamToArray()
    {
        Object[] ob = Stream.of("A","B","C","D").toArray();
        for (Object o : ob) {
            System.out.println(o.toString());
        }
    }

}

