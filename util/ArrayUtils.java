package ru.vsu.cs.util;

import java.util.Random;

public class ArrayUtils {
    private static final Random RND = new Random();

    //мой метод преобразования строки в массив типа int
    public static int[] changeStringToIntArr(String string) {
        String[] array = string.split(" ");
        int[] result = new int[array.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(array[i]);
        }

        return result;
    }

    public static int[] toPrimitive(Integer[] arr) {
        if (arr == null) {
            return null;
        }
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            // автоматическая распаковка из объекта
            result[i] = arr[i];
        }
        return result;
    }

    public static double[] toPrimitive(Double[] arr) {
        if (arr == null) {
            return null;
        }
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            // автоматическая распаковка из объекта
            result[i] = arr[i];
        }
        return result;
    }

    public static Integer[] toObject(int[] arr) {
        if (arr == null) {
            return null;
        }
        Integer[] result = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            // автоматическая упаковка в объект
            result[i] = arr[i];
        }
        return result;
    }

    /**
     * Создание одномерного массива целых чисел, заполненного случайными числами
     * @param length Кол-во элементов в массиве
     * @param minValue Минимальное значение для случайных чисел (включая)
     * @param maxValue Максимальное значение (не включая)
     * @return Массив int[]
     */
    public static int[] createRandomIntArray(int length, int minValue, int maxValue) {
        int[] arr = new int[length];
        for (int i = 0; i < length; i++)
            arr[i] = minValue + RND.nextInt(maxValue - minValue);
        return arr;
    }

    /**
     * @see #createRandomIntArray(int, int, int)
     */
    public static int[] createRandomIntArray(int length, int maxValue) {
        return createRandomIntArray(length, 0, maxValue);
    }
}
