package org.dalp.util;

import java.util.Arrays;

public class Formatter {
    /**
     * имена числительных внутри разрядов
     */
    final private static String[][] numeralName = {
        /* Единицы */{"", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать", "двадцать"},
        /* Десятки */{"", "", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"},
        /* Сотни   */{"", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"},
    };
    final private static String[][] numeralNameFemale = {
        /* Единицы */concat(new String[]{"", "одна", "две"}, Arrays.copyOfRange(numeralName[0], 3, numeralName[0].length - 4)),
        /* Десятки */numeralName[1],
        /* Сотни   */numeralName[2],
    };

    /**
     * названия разрядов числительных в разных количествах
     */
    final private static String[][] numeralTriadNameCases = {
        //1              2, 3 или 4      остальные числа
        {"", "", ""}, // у низшего разряда имен нет
        {"тысяча", "тысячи", "тысяч"},
        {"миллион", "миллиона", "миллионов"},
        {"миллиард", "миллиарда", "миллиардов"},
        {"триллион", "триллиона", "триллионов"},
        {"квадриллион", "квадриллиона", "квадриллионов"},
        {"квинтиллион", "квинтиллиона", "квинтиллионов"},
    };

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Целое число прописью
     */
    public static String asNumberInWords(Number number, boolean femaleUnits) {
        number = Math.abs(number.longValue());
        // размер числа ограничиваем типом long
        long val = number.longValue();

        // Разбираем целое число на цифры
        //FIXME - м.б. есть готовый и более кошерный метод для этого, я пока не дошел по книге. Ж:)
        // Вариант "перевести в строку, сделать String.split посимвольно" (или вообще регуляркой сразу по триадам) -
        // пока даже не рассматривал, потому что накладные расходы, потому что целочисленные вопросы надо и решать в
        // целочисленных операциях. ИМХО.

        // сюда собираем цифры переданного числа
        int[] digits = new int[Long.toString(Long.MAX_VALUE).length() - 1];
        // сюда считаем кол-во цифр
        int count = 0;
        while (val > 0) {
            digits[count++] = (int) (val % 10);
            val = (val / 10);
        }
        if (count > 0) {
            // сколько выйдет десятичных триад, включая не полные
            int triadsCount = (int) Math.round((count / 3.0) + 0.5);
            int[][] triads = new int[triadsCount][3];
            int t, n; // индексы массива триад
            int d; // индекс массива цифр
            // теперь идем по триадам, заполняем "единицы", "десятки", "сотни"
            for (t = 0; t < triadsCount; t++) {
                int triadFirstItem = t * 3;
                for (d = triadFirstItem, n = 0; ((n < 3) && (d < count)); d++, n++) {
                    triads[t][n] = digits[d];
                }
                //  - значения десятков до 19 включительно пишем в секцию "единиц" - особенности русского (и не только) языка
                if (triads[t][1] == 1) {
                    triads[t][1] = 0;
                    triads[t][0] += 10;
                }
            }

            String numeral = "";
            // идем по триадам задом наперед, формируем числительное
            String triadStr;
            for (t = triads.length - 1; t >= 0; t--) {
                triadStr = "";
                for (n = triads[t].length - 1; n >= 0; n--) {
                    // ноль в целых никак не озвучивается
                    if (triads[t][n] != 0) {
                        //                        vvvvvvvv--- Иногда единицы изменения - женского рода, типа тысяча, копейка, и т.п.
                        triadStr += " " + (((t == 1) || femaleUnits) ? numeralNameFemale[n][triads[t][n]] : numeralName[n][triads[t][n]]);
                    }
                }
                if (triadStr.length() > 0) {
                    numeral += " " + triadStr.trim() + " " + morph(triads[t][0], numeralTriadNameCases[t][0], numeralTriadNameCases[t][1], numeralTriadNameCases[t][2]);
                }
            }
            return numeral.trim();
        }

        return "ноль"; // FIXME - м.б. просто пустую строку?
    }

    public static String asNumberInWords(Number number) {
        return asNumberInWords(number, false);
    }

    public static String asNumberInWords(String number) {
        try {
            Number num = Math.abs(Double.parseDouble(number.trim().length() == 0 ? "0" : number));
            return asNumberInWords(num, false);
        } catch (NumberFormatException e) {
            return asNumberInWords(0, false);
        }
    }

    public static String asNumberInWords(String number, boolean femaleUnits) {
        try {
            Number num = Math.abs(Double.parseDouble(number.trim().length() == 0 ? "0" : number));
            return asNumberInWords(num, femaleUnits);
        } catch (NumberFormatException e) {
            return asNumberInWords(0, femaleUnits);
        }
    }

    /**
     * Сумма в рублях прописью
     */
    public static String asPriceInWords(Number price) {
        long priceInKops = Math.toIntExact(Math.round(price.doubleValue() * 100.0));
        long floatPart = priceInKops % 100;
        long intPart = (priceInKops - floatPart) / 100;

        String intWord = morph(intPart, "рубль", "рубля", "рублей");
        String floatWord = morph(floatPart, "копейка", "копейки", "копеек");

        return String.format("%d.%02d руб. (%s %s %s %s)", intPart, floatPart, asNumberInWords(intPart), intWord, asNumberInWords(floatPart, true), floatWord);
    }

    public static String asPriceInWords(String price) {
        try {
            Number num = Math.abs(Double.parseDouble(price.trim().length() == 0 ? "0" : price));
            return asPriceInWords(num);
        } catch (NumberFormatException e) {
            return asPriceInWords(0);
        }
    }

    /**
     * склонение единиц измерения в зависимости от количества
     * Пример вызова: Formatter.morph(1234, "штука", "штуки", "штук") выдаст "штук"
     */
    public static String morph(String number, String title1, String title234, String titleOthers) {
        try {
            Number num = Math.abs(Double.parseDouble(number.trim().length() == 0 ? "0" : number));
            return morph(num, title1, title234, titleOthers);
        } catch (NumberFormatException e) {
            return morph(0, title1, title234, titleOthers);
        }
    }

    public static String morph(Number number, String title1, String title234, String titleOthers) {
        number = Math.abs(number.longValue());
        return (number.longValue() >= 5 && number.longValue() <= 20)
            ? titleOthers
            :
            switch ((int) (number.longValue() % 10)) {
                case 1 -> title1;
                case 2, 3, 4 -> title234;
                default -> titleOthers;
            };
    }
}
