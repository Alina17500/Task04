package ru.vsu.cs.course1.sort.demo;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

import org.jfree.data.xy.XYDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import ru.vsu.cs.course1.sort.*;
import ru.vsu.cs.util.ArrayUtils;
import ru.vsu.cs.util.JTableUtils;
import ru.vsu.cs.util.SwingUtils;


public class SortDemoFrame extends JFrame {
    public static final int EXPORT_WIDTH = 800;
    public static final int EXPORT_HEIGHT = 600;

    private JButton buttonSample;
    private JButton buttonRandom;
    private JTable tableArr;
    private JButton buttonBubbleSort;
    private JButton buttonWarmup;
    private JButton buttonCocktailSort;
    private JButton buttonPerformanceTest;
    private JPanel panelMain;
    private JPanel panelPerformance;
    private JButton buttonSaveChart;

    private ChartPanel chartPanel = null;
    private JFileChooser fileChooserSave;


    public SortDemoFrame() {
        this.setTitle("Task 4");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        JTableUtils.initJTableForArray(tableArr, 60, false, true, false, true);
        tableArr.setRowHeight(30);

        fileChooserSave = new JFileChooser();
        fileChooserSave.setCurrentDirectory(new File("./images"));
        FileFilter filter = new FileNameExtensionFilter("SVG images", "svg");
        fileChooserSave.addChoosableFileFilter(filter);
        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Save");

        buttonSaveChart.setVisible(false);

        // привязка обработчиков событий

        buttonSample.addActionListener(actionEvent -> {
            int[] arr = {3, 8, 2, 5, 6, 1, 9, 7, 0, 4};
            JTableUtils.writeArrayToJTable(tableArr, arr);
        });
        buttonRandom.addActionListener(actionEvent -> {
            int[] arr = ArrayUtils.createRandomIntArray(10, 100);
            JTableUtils.writeArrayToJTable(tableArr, arr);
        });

        buttonBubbleSort.addActionListener(actionEvent -> sortDemo(BubbleSort::sort));
        buttonCocktailSort.addActionListener(actionEvent -> sortDemo(CocktailSort::cocktailSort));

        //разогрев
        buttonWarmup.addActionListener(actionEvent -> warmupSorts());

        buttonPerformanceTest.addActionListener(actionEvent -> {
            String[] sortNames = {
                    "Пузырьком (BubbleSort)",
                    "Перемешиванием (CocktailSort)",
            };
            @SuppressWarnings("unchecked")
            Consumer<Integer[]>[] sorts = new Consumer[]{
                    (Consumer<Integer[]>) BubbleSort::sort,
                    (Consumer<Integer[]>) CocktailSort::cocktailSort,
            };

            int[] sizes = {
                    1000, 2000, 3000, 4000, 5000,
                    6000, 7000, 8000, 9000, 10000,
                    11000, 12000, 13000, 14000, 15000,
                    16000, 17000, 18000, 19000, 20000
            };
            performanceTestDemo(sortNames, sorts, sizes);
        });

        buttonSaveChart.addActionListener(actionEvent -> {
            if (chartPanel == null) {
                return;
            }
            try {
                if (fileChooserSave.showSaveDialog(SortDemoFrame.this) == JFileChooser.APPROVE_OPTION) {
                    String filename = fileChooserSave.getSelectedFile().getPath();
                    if (!filename.toLowerCase().endsWith(".svg")) {
                        filename += ".svg";
                    }
                    saveChartIntoFile(filename);
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });

        buttonSample.doClick();
    }

    //Проверка правильности сортировки
    //Массив проверяется на отсортированность
    public static boolean checkSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    //Разогрев (многокртный вызов методов)
    //Нужен для гарантированной JIT-компиляции метода в инструкции процессора
    public static void warmupSorts() {
        Random rnd = new Random();
        Integer[] arr1 = new Integer[20];
        Integer[] arr2 = arr1.clone();

        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < arr1.length; j++) {
                arr1[j] = rnd.nextInt(100);
            }
            System.arraycopy(arr1, 0, arr2, 0, arr1.length);
            BubbleSort.sort(arr2);
            System.arraycopy(arr1, 0, arr2, 0, arr1.length);
            CocktailSort.cocktailSort(arr2);
        }
    }

    //для тестирования эффективности сортировок
    //sorts - список сортировок в виде массива Consumer
    //sizes - размер массивов, для которых тетстируется эффективность
    private static double[][] performanceTest(String[] sorts, int[] sizes) {
        Sorts bubbleSort;
        Sorts cocktailSort;
        Random random = new Random();
        double[][] result = new double[sorts.length][sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int[] arrForBubbleSort = new int[sizes[i]];
            int[] arrForCocktailSort = new int[sizes[i]];

            for (int j = 0; j < arrForBubbleSort.length; j++) {
                arrForBubbleSort[j] = random.nextInt(100000);
                arrForCocktailSort[j] = random.nextInt(100000);
            }

            bubbleSort = new BubbleSort(0, 0);
            bubbleSort.sort(arrForBubbleSort);

            cocktailSort = new CocktailSort(0, 0);
            cocktailSort.sort(arrForCocktailSort);

            int value1 = bubbleSort.getCountComparisons() + bubbleSort.getCountExchanges();
            int value2 = cocktailSort.getCountComparisons() + cocktailSort.getCountExchanges();

            result[0][i] = value1;
            result[1][i] = value2;
        }
        return result;
    }

    //Настройка диаграммы с помощью JFreeChart
    //chart - диаграмма
    private void customizeChartDefault(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot(); //Создает новую диаграмму
        
        //Набор данных XYDataset формируется на основе данных типа XYSeries, включающих числовые значения для двух осей.
        XYDataset ds = plot.getDataset(); //Метод getDataSet() определяет, каким должно быть первоначальное состояние базы данных перед выполнением каждого теста

        for (int i = 0; i < ds.getSeriesCount(); i++) {
            chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2)); // Настройка графика (ширина линии)
        }

        Font font = buttonPerformanceTest.getFont();//текст кнопки
        chart.getLegend().setItemFont(font); //устанавливает новый интерфейс параметров шрифта
        plot.setBackgroundPaint(Color.WHITE); //фон диаграммы

        //цвет сетки графика:
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.getRangeAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getDomainAxis().setLabelFont(font);
    }

    //Сохранение диаграммы в файл
    private void saveChartIntoFile(String filename) throws IOException {
        JFreeChart chart = chartPanel.getChart();
        SVGGraphics2D g2 = new SVGGraphics2D(EXPORT_WIDTH, EXPORT_HEIGHT);
        Rectangle r = new Rectangle(0, 0, EXPORT_WIDTH, EXPORT_HEIGHT);
        chart.draw(g2, r);
        SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());
    }

    //вывод результата тестирования эффективности в виде графиков во JFreeChart
    //sortNames - названия методов сортировок
    private void performanceTestDemo(String[] sortNames, Consumer<Integer[]>[] sorts, int[] sizes) {
        double[][] result = performanceTest(sortNames, sizes);

        DefaultXYDataset ds = new DefaultXYDataset();
        double[][] data = new double[2][result.length];
        data[0] = Arrays.stream(sizes).asDoubleStream().toArray();

        for (int i = 0; i < sorts.length; i++) {
            data = data.clone();
            data[1] = result[i];
            ds.addSeries(sortNames[i], data);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Sorting efficiency",
                "Array dimension, number of elements",
                "Number of exchanges and comparisons",
                ds
        );
        customizeChartDefault(chart);

        if (chartPanel == null) {
            chartPanel = new ChartPanel(chart);
            panelPerformance.add(chartPanel, BorderLayout.CENTER);
            panelPerformance.updateUI();
        } else {
            chartPanel.setChart(chart);
        }
        buttonSaveChart.setVisible(true);
    }

    //Демонстрация сортировки
    //sort - сортировка в виде Consumer
    private void sortDemo(Consumer<Integer[]> sort) {
        try {
            Integer[] arr = ArrayUtils.toObject(JTableUtils.readIntArrayFromJTable(tableArr));

            sort.accept(arr);

            int[] primitiveArr = ArrayUtils.toPrimitive(arr);
            JTableUtils.writeArrayToJTable(tableArr, primitiveArr);

            // проверка правильности решения
            assert primitiveArr != null;
            if (!checkSorted(primitiveArr)) {
                SwingUtils.showInfoMessageBox("Упс... А массив-то неправильно отсортирован!");
            }
        } catch (Exception ex) {
            SwingUtils.showErrorMessageBox(ex);
        }
    }
}
