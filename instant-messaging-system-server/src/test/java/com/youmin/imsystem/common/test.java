package com.youmin.imsystem.common;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class test {
    public static void main(String[] args) {
        Pair<Integer, String> a = new Pair<Integer, String>(1,"a");
        Pair<Integer, String> b = new Pair<Integer, String>(1,"b");
        Pair<Integer, String> c = new Pair<Integer, String>(2,"b");
        ArrayList<Pair<Integer,String>> objects = new ArrayList<>();
        objects.add(a);
        objects.add(b);
        objects.add(c);
        Map<Integer, List<Pair<Integer, String>>> collect = objects.stream().collect(Collectors.groupingBy(Pair::getKey));
        System.out.println(collect);
    }
}
