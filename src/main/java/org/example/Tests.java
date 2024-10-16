package org.example;

public class Tests {

    @BeforeSuite
    public static void testBeforeStatic(){
        //System.out.println("Выполняется testBeforeStatic");
        throw new IllegalArgumentException("Ошибка при выполнении testBeforeStatic");
    }
    @BeforeSuite
    public void testBeforeNonStatic(){
        System.out.println("Выполняется testBeforeNonStatic");
    }

    @Test(priority = 4)
    public static void testStatic(){
        System.out.println("Выполняется testStatic");
    }

    @Test(priority = 12)
    public void testNonStatic1(){
        System.out.println("Выполняется testNonStatic1");
    }
    @Test(priority = 3)
    public void testNonStatic2(){
        System.out.println("Выполняется testNonStatic2");
    }
    @Test(priority = 7)
    public void testNonStatic3(){
        System.out.println("Выполняется testNonStatic3");
    }

    @AfterSuite
    public static void testAfterStatic(){
        System.out.println("Выполняется testAfterStatic");
    }
    @AfterSuite
    public void testAfterNonStatic(){
        System.out.println("Выполняется testAfterNonStatic");
    }

    @CsvSource("10, Java, 20, true")
    public void testMethodCsvSource(int a, String b, int c, boolean d) {
        System.out.println("Выполняется testMethod: "+"a: "+a+", b: "+b+", c: "+c+", d: "+ d);
    }
}
