package org.example;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class TestRunner {

    // проверим, что Test указан для обычных методов, а BeforeSuite и AfterSuite для статичных
    public static boolean isStatic(Method method, Annotation annotation) {
        boolean res = true;
        if ((method.getModifiers() & Modifier.STATIC) > 0) {
            if (annotation.annotationType().equals(Test.class)) {
                res = false;
            }
        } else if (annotation.annotationType().equals(BeforeSuite.class) | annotation.annotationType().equals(AfterSuite.class)) {
            res = false;
        }
        return res;
    }

    public static Map<String, String> runTests(Class testClass) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method[] methods = testClass.getDeclaredMethods();
        Map<String, String> resultTests = new HashMap<>();
        int beforeSuiteCnt = 0;
        int afterSuiteCnt = 0;
        Method beforeSuite = null;
        Method afterSuite = null;
        Tests obj = new Tests();

        for (Method m : methods) {
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                if (!isStatic(m, m.getAnnotation(BeforeSuite.class))) {
                    resultTests.put(m.getName(), " не прошел проверку статичности");
                    continue;
                } else {
                    beforeSuiteCnt++;
                    beforeSuite = m;
                }
            }
            if (m.isAnnotationPresent(AfterSuite.class)) {
                if (!isStatic(m, m.getAnnotation(AfterSuite.class))) {
                    resultTests.put(m.getName(), " не прошел проверку статичности");
                    continue;
                } else {
                    afterSuiteCnt++;
                    afterSuite = m;
                }
            }
        }

        if (beforeSuiteCnt > 1)
            resultTests.put(BeforeSuite.class.getName(), " методов с аннотациями @BeforeSuite больше одного");
        else if (beforeSuiteCnt == 1) {
            try {
                beforeSuite.invoke(obj);
                resultTests.put(beforeSuite.getName(), "beforeSuite выполнена");
            } catch (IllegalAccessException e) {
                resultTests.put(beforeSuite.getName(), "Ошибка: " + e.toString());
                //throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                //throw new RuntimeException(e);
                resultTests.put(beforeSuite.getName(), "Ошибка: " + e.toString());
            }
        }

        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                if (!isStatic(m, m.getAnnotation(Test.class))) {
                    resultTests.put(m.getName(), " не прошел проверку статичности");
                    continue;
                } else {
                    if (m.getAnnotation(Test.class).priority() < 1 | m.getAnnotation(Test.class).priority() > 10) {
                        resultTests.put(m.getName(), "У аннотации " + Test.class.getName() + " неправильный приоритет " + m.getAnnotation(Test.class).priority());
                        continue;
                    }
                }
            }
        }

        Arrays.stream(methods).filter(m -> m.isAnnotationPresent(Test.class) && isStatic(m, m.getAnnotation(Test.class)) && m.getAnnotation(Test.class).priority() >= 1 && m.getAnnotation(Test.class).priority() <= 10).sorted(Comparator.comparing(m -> m.getAnnotation(Test.class).priority())).forEach(m -> {
            try {
                m.invoke(obj);
                resultTests.put(m.getName(), " выполнена");
            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
                resultTests.put(m.getName(), "Ошибка: " + e.toString());
            } catch (InvocationTargetException e) {
                //throw new RuntimeException(e);
                resultTests.put(m.getName(), "Ошибка: " + e.toString());
            }
        });

        if (afterSuiteCnt > 1)
            resultTests.put(AfterSuite.class.getName(), " методов с аннотациями @AfterSuite больше одного");
        else if (afterSuiteCnt == 1) {
            try {
                afterSuite.invoke(obj);
                resultTests.put(afterSuite.getName(), " выполнена");
            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
                resultTests.put(afterSuite.getName(), "Ошибка: " + e.toString());
            } catch (InvocationTargetException e) {
                //throw new RuntimeException(e);
                resultTests.put(afterSuite.getName(), "Ошибка: " + e.toString());
            }
        }

        Arrays.stream(methods).filter(m -> m.isAnnotationPresent(CsvSource.class)).forEach(m -> {
            try {
                Object[] arguments = new Object[0];
                String[] pars = m.getAnnotation(CsvSource.class).value().split(",");
                int k = 0;
                for (Parameter p : m.getParameters()) {
                    arguments = appendValue(arguments, parseStr(pars[k], p.getType().getName()));
                    k++;
                }

                m.invoke(obj, arguments);
                resultTests.put(m.getName(), " выполнена");
            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
                resultTests.put(m.getName(), "Ошибка: " + e.toString());
            } catch (InvocationTargetException e) {
                //throw new RuntimeException(e);
                resultTests.put(m.getName(), "Ошибка: " + e.toString());
            }
        });
        return resultTests;
    }

    public static Object parseStr(String value, String type) {
        Object res = new Object();
        value = value.trim();
        if (type == "boolean")
            res = Boolean.getBoolean(value);
        else if (type == "int") res = Integer.parseInt(value);
        else if (type == "java.lang.String") res = (Object)value;
        return res;
    }

    public static Object[] appendValue(Object[] obj, Object newObj) {
        ArrayList temp = new ArrayList(Arrays.asList(obj));
        temp.add(newObj);
        return temp.toArray();
    }
}
