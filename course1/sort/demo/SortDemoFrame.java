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
        //Создает новый экземпляр XYPlot без набора данных, без осей и без средства визуализации.
        XYPlot plot = chart.getXYPlot(); //Создает новую диаграмму на основе предоставленного графика.
        //Набор данных XYDataset формируется на основе данных типа XYSeries, включающих числовые значения для двух осей.
        XYDataset ds = plot.getDataset(); //Метод getDataSet() определяет, каким должно быть первоначальное состояние базы данных перед выполнением каждого теста

        //Для определения цвета и ширины линии графика используются методы setSeriesPaint и setSeriesStroke.
        //Для изменения типа линии графиков используется метод setSeriesStroke
        for (int i = 0; i < ds.getSeriesCount(); i++) {
            chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2)); // Настройка графика (ширина линии)
        }

        Font font = buttonPerformanceTest.getFont();//текст кнопки
        chart.getLegend().setItemFont(font); //устанавливает новый интерфейс параметров шрифта. Устанавливаемый интерфейс передается в качестве значения единственного параметра
        plot.setBackgroundPaint(Color.WHITE); //фон диаграммы

        //цвет сетки графика:
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        //Базовый класс для всех осей в JavaFX, представляющий ось, нарисованную на области диаграммы. Он содержит свойства для автоматического ранжирования оси, тиков и меток вдоль оси.
        plot.getRangeAxis().setTickLabelFont(font); //Шрифт для всех меток тиков
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), 10, 10));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panel1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonSample = new JButton();
        buttonSample.setText("Example");
        panel1.add(buttonSample, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonRandom = new JButton();
        buttonRandom.setText("Random (size: 10)");
        panel1.add(buttonRandom, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonBubbleSort = new JButton();
        buttonBubbleSort.setText("Сортировка пузырьком (Bubble Sort)");
        panel2.add(buttonBubbleSort, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCocktailSort = new JButton();
        buttonCocktailSort.setText("Сортировка перемешиванием (CocktailSort)");
        panel2.add(buttonCocktailSort, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panel3, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonWarmup = new JButton();
        buttonWarmup.setText("Warm up");
        panel3.add(buttonWarmup, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonPerformanceTest = new JButton();
        buttonPerformanceTest.setText("Sorting efficiency");
        panel3.add(buttonPerformanceTest, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonSaveChart = new JButton();
        buttonSaveChart.setText("Save");
        panel3.add(buttonSaveChart, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelPerformance = new JPanel();
        panelPerformance.setLayout(new BorderLayout(0, 0));
        panelMain.add(panelPerformance, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 300), null, 0, false));
        final Spacer spacer3 = new Spacer();
        panelPerformance.add(spacer3, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 120), new Dimension(-1, 120), new Dimension(-1, 120), 0, false));
        tableArr = new JTable();
        scrollPane1.setViewportView(tableArr);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
