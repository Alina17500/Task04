package ru.vsu.cs.course1.sort;

import java.util.Comparator;

public class BubbleSort implements Sorts {
    private int countComparisons; //считает кол-во сравнений
    private int countExchanges; //считает кол-во обменов

    public BubbleSort(int countComparisons, int countExchanges) {
        this.countComparisons = countComparisons;
        this.countExchanges = countExchanges;
    }

    public static <T> void sort(T[] data, Comparator<T> comparator) {
        int size = data.length;

        for (int i = 0; i < size; i++) {
            for (int j = size - 1; j >= i + 1; j--) {
                if (comparator.compare(data[j - 1], data[j]) > 0) {  //<=> data[j - 1] > data[j]
                    T value = data[j - 1];
                    data[j - 1] = data[j];
                    data[j] = value;
                }
            }
        }
    }

    public static <T extends Comparable<T>> void sort(T[] data) {

        class TempComparator implements Comparator<T> {
            @Override
            public int compare(T a, T b) {
                return a.compareTo(b);
            }
        }
        sort(data, new TempComparator());

        //sort(data, (a, b) -> a.compareTo(b));

        sort(data, Comparable::compareTo);
    }

    @Override
    public int[] sort(int[] array) {
        int size = array.length;

        for (int i = 0; i < size; i++) {
            for (int j = size - 1; j >= i + 1; j--) {
                this.countComparisons++;
                if (array[j - 1] > array[j]) {
                    int value = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = value;
                    this.countExchanges++;
                }
            }
        }

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
