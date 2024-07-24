package com.youmin.imsystem.common;

import java.util.Objects;
import java.util.Optional;

public class test {
    public static void main(String[] args) {
        Integer i = 1;
        boolean present = Optional.ofNullable(null)
                .filter(i1 -> Objects.equals(i1, new Integer(1))).isPresent();
        System.out.println(present);
    }
}
