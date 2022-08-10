/*
 * Copyright (c) 2017. Phasmid Software
 */

package edu.neu.coe.huskySort.sort.simple;
import edu.neu.coe.huskySort.sort.ComparableSortHelper;
import edu.neu.coe.huskySort.sort.ComparisonSortHelper;
import edu.neu.coe.huskySort.sort.HelperFactory;
import edu.neu.coe.huskySort.sort.Sort;
import edu.neu.coe.huskySort.util.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("ALL")
public class ShellSortTest {

    @Test
    public void testSedgewick0() throws IOException {
        ShellSort<Integer> shellSort = new ShellSort<>(4);
        ShellSort<Integer>.H h = shellSort.new H(0);
        assertEquals(1, h.sedgewick(0));
        assertEquals(5, h.sedgewick(1));
        assertEquals(19, h.sedgewick(2));
        assertEquals(41, h.sedgewick(3));
    }

    @Test
    public void testSedgewick50() throws IOException {
        ShellSort<Integer> shellSort = new ShellSort<>(4);
        ShellSort<Integer>.H h = shellSort.new H(50);
        assertEquals(41, h.first());
        assertEquals(19, h.next());
        assertEquals(5, h.next());
        assertEquals(1, h.next());
        assertEquals(0, h.next());
    }

    @BeforeClass
    public static void setupClass() {
        try {
            config = Config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void hSort3() {
        ShellSort<Integer> sorter = new ShellSort<>(3,config);
        PrivateMethodInvoker t = new PrivateMethodInvoker(sorter);
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {4, 1, -1, 2, 8, 3, 0, 5, 15, 6, 1, 9, 17, 7, 11};
        Class[] classes = {int.class, Comparable[].class, int.class, int.class};
        t.invokePrivateExplicit("hSort", classes, 4, xs, 0, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sort1() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(3,config).sort(xs));
    }

    @Test
    public void sort1a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(5,config).sort(xs));
    }

    @Test
    public void sort2() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        Sort ss = new ShellSort<Integer>(3,config);
        ss.sort(xs, 0, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sort2a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        Sort ss = new ShellSort<Integer>(5,config);
        ss.sort(xs, 0, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sort3() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, -1, 3, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Sort ss = new ShellSort<Integer>(3,config);
        ss.sort(xs, 1, 3);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sort3a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, -1, 3, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Sort ss = new ShellSort<Integer>(5, config);
        ss.sort(xs, 1, 3);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sort4() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 7, 9, 11, 17};
        Sort ss = new ShellSort<Integer>(3,config);
        ss.sort(xs, 11, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sort4a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 7, 9, 11, 17};
        Sort ss = new ShellSort<Integer>(5, config);
        ss.sort(xs, 11, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Ignore // TEST
    public void sort5() throws Exception {
        ComparableSortHelper<Integer> helper = new ComparableSortHelper<>("ShellSort", 1000);
        Integer[] xs = (Integer[]) helper.random(Integer.class, random -> random.nextInt());
        SelectionSort<Integer> sorter = new SelectionSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.sorted(ys));
        System.out.println(helper);

    }

    @Test
    public void sort7() throws Exception {
        doShellSortTest(1000, 3);
    }

    @Test
    public void sort7a() throws Exception {
        doShellSortTest(1000, 5);
    }

    @Test
    public void sort5a() throws Exception {
        doShellSortTest(10, 3);
    }

    @Test
    public void sort5b() throws Exception {
        doShellSortTest(10, 5);
    }

    @Test
    public void sort6a() throws Exception {
        doShellSortTest(100, 3);
    }

    @Test
    public void sort6b() throws Exception {
        doShellSortTest(100, 5);
    }

    private void doShellSortTest(int N, final int gapSequence) throws IOException {
        final Config config = ConfigTest.setupConfig("true", "", "1", "", "");
        ComparisonSortHelper<Integer> helper = (ComparisonSortHelper<Integer>) HelperFactory.create("Shell sort", N, config);
        System.out.println(helper);
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(N * 2));
        helper.init(N);
        helper.preProcess(xs);
        ShellSort<Integer> sorter = new ShellSort<>(gapSequence, helper);
        sorter.mutatingSort(xs);
        helper.postProcess(xs);
        assertTrue(helper.sorted(xs));
        showStatistics(helper);
    }

    private void showStatistics(ComparisonSortHelper<Integer> helper) {
        if (ComparisonSortHelper.class.isAssignableFrom(helper.getClass())) {
            final PrivateMethodInvoker privateMethodTester = new PrivateMethodInvoker(helper);
            final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
            double inversions = statPack.getStatistics(Instrumenter.INVERSIONS).mean();
            double compares = statPack.getStatistics(Instrumenter.COMPARES).mean();
            double swaps = statPack.getStatistics(Instrumenter.SWAPS).mean();
            double fixes = statPack.getStatistics(Instrumenter.FIXES).mean();
            System.out.println(inversions + ", " + compares + ", " + swaps + " " + fixes);
            System.out.println("inversions: " + inversions);
            System.out.println("compares: " + swaps);
            System.out.println("swaps: " + fixes);
        }
    }

    @Test
    public void sortH1() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(1,config).sort(xs));
    }

    @Test
    public void sortH2() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(2,config).sort(xs));
    }

    @Test
    public void sortH3() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(5, config).sort(xs));
    }


    final static LazyLogger logger = new LazyLogger(ShellSort.class);

    private static Config config;
}