package com.youmin.imsystem.common;

import javafx.util.Pair;

import javax.validation.Validation;
import java.util.*;
import java.util.stream.Collectors;

public class test {
    public static void main(String[] args) {
        System.out.println(Validation.buildDefaultValidatorFactory().getValidator().validate(null));
    }
}
