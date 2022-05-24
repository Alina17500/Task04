package ru.vsu.cs.course1.sort;

import java.util.Comparator;

public class CocktailSort implements Sorts {
    private int countComparisons;
    private int countExchanges;

    public CocktailSort(int countComparisons, int countExchanges) {
        this.countComparisons = countComparisons;
        this.countExchanges = countExchanges;
    }

    public static <T> void cocktailSort(T[] data, Comparator<T> comparator) {
        int left = 0;
        int right = data.length - 1;
        T value;

        do {
            for (int i = left; i < right; i++) {
                if (comparator.compare(data[i], data[i + 1]) > 0) {
                    value = data[i];
                    data[i] = data[i + 1];
                    data[i + 1] = value;
                }
            }
            right--;

            for (int i = right; i > left; i--) {
                if (comparator.compare(data[i], data[i-1]) < 0) {
                    value = data[i];
                    data[i] = data[i - 1];
                    data[i - 1] = value;
                }
            }
            left++;
        } while (left < right);
    }

    public static <T extends Comparable<T>> void cocktailSort(T[] data) {

        class TempComparator implements Comparator<T> {
            @Override
            public int compare(T a, T b) {
                return a.compareTo(b);
            }
        }
        cocktailSort(data, new TempComparator());

        //cocktailSort(data, (a, b) -> a.compareTo(b));

        cocktailSort(data, Comparable::compareTo);
    }

    @Override
    public int[] sort(int[] array) {
        int value;
        int left = 0;
        int right = array.length - 1;

        do {
            for (int i = left; i < right; i++) {
                this.countComparisons++;
                if (array[i] > array[i + 1]) {
                    value = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = value;
                    this.countExchanges++;
                }
            }
            right--;

            for (int i = right; i > left; i--) {
                this.countComparisons++;
                if (array[i] < array[i - 1]) {
                    value = array[i];
                    array[i] = array[i - 1];
                    array[i - 1] = value;
                    this.countExchanges++;
                }
            }
            left++;
        } while (left < right);

        return array;
    }

    @Override
    public int getCountComparisons() {
        return countComparisons;
    }

    @Override
    public int getCountExchanges() {
        return countExchanges;
    }
}




