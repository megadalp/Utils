package org.dalp.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ClassFinder {
    // FYI Это мое поделие :)
    public static String[] getClassNames(Class<?> clss) {
        /** Получаем список ИМЕН классов указанного пакета */
        // FYI Берем каталог с классами пакета, к которому принадлежит переданный класс
        String[] classNames = new String[0];
        try {
            String pathToClasses = clss
                .getProtectionDomain().getCodeSource().getLocation()
                .toString().replaceFirst("^file:/", "")
                + "/"
                + clss.getPackageName().replace(".", "/");
            // FYI Вытаскиваем оттуда все классы
            classNames = (new File(pathToClasses)).list();
        } catch (Exception e) {
            // throw new RuntimeException(e);
        }
        assert classNames != null;
        // Убираем расширение .java
        for (int i = 0; i < classNames.length; i++) {
            classNames[i] = classNames[i].replaceFirst("\\.[^.]+$", "");
        }

        return classNames;
    }

    private static final char PKG_SEPARATOR = '.';
    private static final char DIR_SEPARATOR = '/';
    private static final String CLASS_FILE_SUFFIX = ".class";
    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    // FYI Это добавка от меня, чтобы выводило также ВСЕ классы пакета
    static List<Class<?>> find(String scannedPackage) throws URISyntaxException {
        return find(scannedPackage, "");
    }

    /**
     * Дровишки отсюда:
     *   https://ru.stackoverflow.com/questions/517078/Поиск-класса-по-его-части-его-имени-java
     *   а там отсюда, возможны изменения
     *   https://stackoverflow.com/questions/15519626/how-to-get-all-classes-names-in-a-package
     * @param scannedPackage
     * @param beginStr
     * @return
     * @throws URISyntaxException
     */
    public static List<Class<?>> find(String scannedPackage, String beginStr) throws URISyntaxException {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }

        File scannedDir = new File(scannedUrl.toURI());

        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage, beginStr));
        }
        return classes;
    }

    private static List<Class<?>> find(File file, String scannedPackage, String beginStr) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource, beginStr));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                Class foundedClass = Class.forName(className);
                String[] bits = foundedClass.getName().split("\\.");
                String lastOne = bits[bits.length - 1];

                if (
                    // FYI Это добавка от меня, чтобы выводило также ВСЕ классы пакета
                    beginStr.isEmpty() ||
                    // FYI а чо мелочиться, пусть регвыром ищет
                    lastOne.toLowerCase().matches("^.*?" + beginStr.toLowerCase() + ".*?$")
                ) {
                    classes.add(foundedClass);
                }

            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }
}