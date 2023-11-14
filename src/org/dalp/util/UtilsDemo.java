package org.dalp.util;

import java.util.List;

class UtilsDemo {

    public static void main(String[] args) throws java.lang.Exception {
        System.out.println("Formatter demo:");
        System.out.println("--");
        demoFormatter(args);

        System.out.println("\n");
        System.out.println("DalpGetClassNames demo:");
        System.out.println("--");
        demoDalpGetClassNames(args);

        System.out.println("\n");
        System.out.println("ClassFinder demo:");
        System.out.println("--");
        demoClassFinder(args);
    }

    public static void demoClassFinder(String[] args) throws java.lang.Exception {
        // пример поиска ClassFinder.find("com.myproject.java", "Hello");
        List<Class<?>> classes;
        // classes = ClassFinder.find("org.dalp.util");
        classes = ClassFinder.find("org.dalp.util", "m[ao]");
        for (Class item : classes) {
            System.out.println(item.getName());
        }
    }

    public static void demoDalpGetClassNames(String[] args) {
        String[] classNames = ClassFinder.getClassNames(Formatter.class);
        System.out.println(String.join("\n", classNames));
        // System.out.println("--");
        // classNames = ClassFinder.getClassNames(Formatter.class);
        // System.out.println(String.join("\n", classNames));
    }

    public static void demoFormatter(String[] args) {
        long testNumber;
        String testNumberString;
        if (args.length > 0) {
            System.out.println("Что передано");
            System.out.println(args[0] + " : " + Formatter.asNumberInWords(args[0], true) + " " + Formatter.morph(args[0], "штука", "штуки", "штук"));
            System.out.println(args[0] + " : " + Formatter.asNumberInWords(args[0]) + " " + Formatter.morph(args[0], "рубль", "рубля", "рублей"));
            System.out.println(args[0] + " : " + Formatter.asPriceInWords(args[0]));
            return;
        }
        System.out.println("Обычное использование");
        testNumber = 8980;
        System.out.println(testNumber + " as long : " + Formatter.asNumberInWords(testNumber, true) + " " + Formatter.morph(testNumber, "штука", "штуки", "штук"));
        testNumber = 31;
        System.out.println(testNumber + " as int : " + Formatter.asNumberInWords((int) testNumber, true) + " " + Formatter.morph((int) testNumber, "штука", "штуки", "штук"));
        System.out.println(1234.5678 + " as float : " + Formatter.asNumberInWords((float) 1234.5678, true) + " " + Formatter.morph((float) 1234.5678, "штука", "штуки", "штук"));
        System.out.println(1234.5678 + " as double : " + Formatter.asNumberInWords((double) 1234.5678, true) + " " + Formatter.morph((double) 1234.5678, "штука", "штуки", "штук"));
        System.out.println("1234.5678" + " as double : " + Formatter.asNumberInWords("1234.5678", true) + " " + Formatter.morph("1234.5678", "штука", "штуки", "штук"));
        testNumber = 1;
        System.out.println(testNumber + " : " + Formatter.asNumberInWords(testNumber) + " " + Formatter.morph(testNumber, "рубль", "рубля", "рублей"));
        testNumber = 0;
        System.out.println(testNumber + " : " + Formatter.asNumberInWords(testNumber) + " " + Formatter.morph(testNumber, "рубль", "рубля", "рублей"));
        System.out.println("");
        System.out.println("Слово 'минус' к отрицательным сами приставляйте, если надо");
        testNumber = -123;
        System.out.println(testNumber + " : " + Formatter.asNumberInWords(testNumber) + " " + Formatter.morph(testNumber, "рубль", "рубля", "рублей"));
        System.out.println("");
        System.out.println("Максималка (long)");
        testNumber = 922337203685477580L;
        System.out.println(testNumber + " : " + Formatter.asNumberInWords(testNumber) + " " + Formatter.morph(testNumber, "доллар", "доллара", "долларов"));
        System.out.println("");
        System.out.println("Сумма прописью");
        System.out.println(Formatter.asPriceInWords(751_342.507));
        System.out.println("");
        System.out.println("Пустое значение");
        testNumberString = "";
        System.out.println("'" + testNumberString + "'" + " : 0 " + Formatter.morph(testNumberString, "штука", "штуки", "штук"));
        testNumberString = "0";
        System.out.println("'" + testNumberString + "'" + " : 0 " + Formatter.morph(testNumberString, "штука", "штуки", "штук"));
    }
}
