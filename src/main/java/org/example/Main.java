package org.example;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Map<String, String> resultTests = new HashMap<>();
        Class<Tests> tests = Tests.class;
        resultTests = TestRunner.runTests(Tests.class);
        System.out.println("----- Результаты теста: "+System.lineSeparator()+resultTests.toString().replace(",",","+System.lineSeparator()));
    }
}