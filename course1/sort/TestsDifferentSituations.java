package ru.vsu.cs.course1.sort;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.vsu.cs.util.ArrayUtils;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class TestsDifferentSituations {
    private final String sequence;
    private final String result;

    public TestsDifferentSituations(String sequence, String result) {
        this.sequence = sequence;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"1 2 3 0 90 9 89", "0 1 2 3 9 89 90"},
                {"150 150 150 150", "150 150 150 150"},
                {"0", "0"},
                {"34 48 72 91 100", "34 48 72 91 100"},
                {"11 34 3 45 559 23 20 5 67 8 877 34 60 46 2 2 18 10 40 28 17 37 34 26 19 92 92", "2 2 3 5 8 10 11 17 18 19 20 23 26 28 34 34 34 37 40 45 46 60 67 92 92 559 877"},
                {"190 82 55 21 18 5", "5 18 21 55 82 190"}
        });
    }

    @Test
    public void testBubbleSort() {
        Sorts bubbleSort = new BubbleSort(0,0);

        Assert.assertArrayEquals(ArrayUtils.changeStringToIntArr(result), bubbleSort.sort(ArrayUtils.changeStringToIntArr(sequence)));
    }

    @Test
    public void testCocktailSort() {
        Sorts cocktailSort = new CocktailSort(0,0);

        Assert.assertArrayEquals(ArrayUtils.changeStringToIntArr(result), cocktailSort.sort(ArrayUtils.changeStringToIntArr(sequence)));
    }
}
