/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.huskySort.sort.simple;
import edu.neu.coe.huskySort.sort.ComparableSortHelper;
import edu.neu.coe.huskySort.sort.ComparisonSortHelper;
import edu.neu.coe.huskySort.sort.HelperFactory;
import edu.neu.coe.huskySort.sort.SortWithHelper;
import edu.neu.coe.huskySort.util.Config;
import edu.neu.coe.huskySort.util.Instrumenter;
import edu.neu.coe.huskySort.util.PrivateMethodInvoker;
import edu.neu.coe.huskySort.util.StatPack;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Class to implement Shell Sort.
 *
 * @param <X> the type of element on which we will be sorting (must implement Comparable).
 */
public class ShellSort<X extends Comparable<X>> extends SortWithHelper<X> {

    /**
     * Constructor for ShellSort.
     * <p>
     * NOTE: not used.
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public ShellSort(final int m, final int N, final Config config) {
        super(DESCRIPTION, N, config);
        this.m = m;
    }

    public ShellSort() throws IOException {
        this(5);
    }

    /**
     * Constructor for ShellSort
     *
     * @param m the "gap" (h) sequence to follow:
     *          1: ordinary insertion sort;
     *          2: use powers of two less one;
     *          3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *          4: Sedgewick's sequence (not implemented).
     */
    public ShellSort(final int m) {
        this(m, new ComparableSortHelper<>(DESCRIPTION));
    }


    /**
     * Method to sort a sub-array of an array of Xs.
     * <p>
     * TODO check that the treatment of from and to is correct. It seems to be according to the unit tests.
     *
     * @param xs an array of Xs to be sorted in place.
     */
    public void sort(final X[] xs, final int from, final int to) {
        final int N = to - from;
        final H hh = new H(N);
        int h = hh.first();
        while (h > 0) {
            hSort(h, xs, from, to);
            h = hh.next();
        }
    }

    public static final String DESCRIPTION = "Shell sort";



    /**
     * Constructor for ShellSort
     *
     * @param m      the "gap" (h) sequence to follow:
     *               1: ordinary insertion sort;
     *               2: use powers of two less one;
     *               3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *               4: Sedgewick's sequence (not implemented).
     * @param helper an explicit instance of ComparisonSortHelper to be used.
     */
    public ShellSort(final int m, final ComparisonSortHelper<X> helper) {
        super(helper);
        this.m = m;
    }

    /**
     * Constructor for ShellSort
     *
     * @param m the "gap" (h) sequence to follow:
     *          1: ordinary insertion sort;
     *          2: use powers of two less one;
     *          3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *          4: Sedgewick's sequence.
     *          5: Pratt Sequence 2^i*3^j with i, j >= 0.
     */
    public ShellSort(int m, Config config) {
        this(m, new ComparableSortHelper<>(DESCRIPTION));
    }



    /**
     * Set the "shell" function which is invoked on the helper after each shell (i.e. each value of h).
     * Yes, I do realize that shell was the name of the inventor, Donald Shell.
     * But it's also a convenient name of a (set of) h-sorts which one particular h-value.
     *
     * @param shellFunction a consumer of Helper of X.
     */
    public void setShellFunction(Consumer<ComparisonSortHelper<X>> shellFunction) {
        this.shellFunction = shellFunction;
    }

    /**
     * Private method to h-sort an array.
     *
     * @param h    the stride (gap) of the h-sort.
     * @param xs   the array to be sorted.
     * @param from the first index to be considered in array xs.
     * @param to   one plus the last index to be considered in array xs.
     */
    private void hSort(final int h, final X[] xs, final int from, final int to) {
        final ComparisonSortHelper<X> helper = getHelper();
        for (int i = h + from; i < to; i++) {
            int j = i;
            while (j >= h + from && helper.swapConditional(xs, j - h, j)) j -= h;
        }
    }

    private final int m;

    private Consumer<ComparisonSortHelper<X>> shellFunction = null;

    /**
     * Private inner class to provide h (gap) values.
     */
    class H {
        @SuppressWarnings("CanBeFinal")
        private int h = 1;
        private int i;
        private boolean started = false;
        final List<Integer> data = new ArrayList<>();

        H(final int N) {
            switch (m) {
                case 1:
                    break;
                case 2:
                    while (h <= N) h = 2 * (h + 1) - 1;
                    break;
                case 3:
                    while (h <= N / 3) h = h * 3 + 1;
                    break;
                case 4:
                    i = 0;
                    while (sedgewick(i) < N) i++;
                    i--;
                    h = (int) sedgewick(i); // Note there will be loss of precision for large i
                    break;
                case 5:
                    //2^i*3^j with i, j >= 0
                    int i;
                    int j = 1;
                    while (j <= N) {
                        i = j;
                        while (i <= N) {
                            data.add(i);
                            i = i * 2;
                        }
                        j = j * 3;
                    }
                    Collections.sort(data);
                    this.i = data.size() - 1;
                    h = data.get(this.i);
                    break;
                default:
                    throw new RuntimeException("invalid m value: " + m);
            }
        }

        /**
         * Method to yield the first h value.
         * NOTE: this may only be called once.
         *
         * @return the first (largest) value of h, given the size of the problem (N)
         */
        int first() {
            if (started) throw new RuntimeException("cannot call first more than once");
            started = true;
            return h;
        }

        /**
         * Method to yield the next h value in the "gap" series.
         * NOTE: first must be called before next.
         *
         * @return the next value of h in the gap series.
         */
        int next() {
            if (started) {
                switch (m) {
                    case 1:
                        return 0;
                    case 2:
                        h = (h + 1) / 2 - 1;
                        return h;
                    case 3:
                        h = h / 3;
                        return h;
                    case 4:
                        i--;
                        return (int) sedgewick(i);
                    case 5:
                        i--;
                        if (i < 0) {
                            return 0;
                        }
                        return data.get(i);
                    default:
                        throw new RuntimeException("invalid m value: " + m);
                }
            } else {
                started = true;
                return h;
            }
        }


        long sedgewick(int k) {
            if (k < 0) return 0;
            if (k % 2 == 0) return 9L * (powerOf2(k) - powerOf2(k / 2)) + 1;
            else return 8L * powerOf2(k) - 6 * powerOf2((k + 1) / 2) + 1;
        }

        private long powerOf2(int k) {
            long value = 1;
            for (int i = 0; i < k; i++) value *= 2;
            return value;
        }
    }

    /**
     * Method to run quicksort and get values for compares
     * and swaps for different array sizes using statpack.
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Please enter a number: ");

        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        if(num==1){
            int k = 10;
            System.out.println("test");
            Integer[] sizeOfArray = new Integer[k];
            for(int i=0;i<k;i++){
                sizeOfArray[i] = (int)Math.pow(2, 7+i);
            }
            Integer cycles = 1;
            System.out.println("Averaging across " + cycles + " runs");
            for (Integer n : sizeOfArray) {
                double swaps = 0.0;
                double compares = 0.0;
                for (int t = 0; t < cycles; t++) {
                    final Config config = Config.setupConfig("true", "", "", "", "");
                    final ComparisonSortHelper<Integer> helper = HelperFactory.create("quick sort", n, config);
                    Integer[] xs = new Integer[n] ;
                    for(int i = 0;i<n;i++){
                        xs[i]=i;
                    }
                    ShellSort<Integer> s = new ShellSort(3,helper);
                    s.init(n);
                    helper.preProcess(xs);
                    Integer[] ys = s.sort(xs);
                    helper.postProcess(ys);
                    final PrivateMethodInvoker privateMethodTester = new PrivateMethodInvoker(helper);
                    final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
                    swaps = swaps + statPack.getStatistics(Instrumenter.SWAPS).mean();
                    compares = compares + statPack.getStatistics(Instrumenter.COMPARES).mean();
                    System.out.println(statPack);
                }

                double averageSwap = swaps / cycles;
                double averageCompare = compares / cycles;
                double ratio =  averageCompare/averageSwap ;
                System.out.println( "-------------------------------------------------------------------------------------------------------");
                System.out.println( "Array Size: " + n + " Avg Swap: " + averageSwap + " Avg Compare: " + averageCompare + " Ratio: " + ratio);
            }
        }
        if(num==2){
            int k = 10;
            Integer[] sizeOfArray = new Integer[k];
            for(int i=0;i<k;i++){
                sizeOfArray[i] = (int)Math.pow(2, 7+i);
            }
            Integer cycles = 1;
            System.out.println("Averaging across " + cycles + " runs");
            for (Integer n : sizeOfArray) {
                double swaps = 0.0;
                double compares = 0.0;
                for (int t = 0; t < cycles; t++) {
                    final Config config = Config.setupConfig("true", "", "", "", "");
                    final ComparisonSortHelper<Integer> helper = HelperFactory.create("quick sort", n, config);
                    final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n));
                    ShellSort<Integer> s = new ShellSort(3,helper);
                    s.init(n);
                    helper.preProcess(xs);
                    Integer[] ys = s.sort(xs);
                    helper.postProcess(ys);
                    final PrivateMethodInvoker privateMethodTester = new PrivateMethodInvoker(helper);
                    final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
                    swaps = swaps + statPack.getStatistics(Instrumenter.SWAPS).mean();
                    compares = compares + statPack.getStatistics(Instrumenter.COMPARES).mean();
                    System.out.println(statPack);
                }

                double averageSwap = swaps / cycles;
                double averageCompare = compares / cycles;
                double ratio =  averageCompare/averageSwap ;
                System.out.println( "-------------------------------------------------------------------------------------------------------");
                System.out.println( "Array Size: " + n + " Avg Swap: " + averageSwap + " Avg Compare: " + averageCompare + " Ratio: " + ratio);
            }
        }
        if(num==3){
            int k = 10;
            Integer[] sizeOfArray = new Integer[k];
            for(int i=0;i<k;i++){
                sizeOfArray[i] = (int)Math.pow(2, 7+i);
            }
            Integer cycles = 1;
            System.out.println("Averaging across " + cycles + " runs");
            for (Integer n : sizeOfArray) {
                double swaps = 0.0;
                double compares = 0.0;
                for (int t = 0; t < cycles; t++) {
                    final Config config = Config.setupConfig("true", "", "", "", "");
                    final ComparisonSortHelper<Integer> helper = HelperFactory.create("quick sort", n, config);
                    Integer[] xs = new Integer[n] ;
                    for(int i = 0;i<n;i++){
                        xs[i]=n-i;
                    }
                    ShellSort<Integer> s = new ShellSort(3,helper);
                    s.init(n);
                    helper.preProcess(xs);
                    Integer[] ys = s.sort(xs);
                    helper.postProcess(ys);
                    final PrivateMethodInvoker privateMethodTester = new PrivateMethodInvoker(helper);
                    final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
                    swaps = swaps + statPack.getStatistics(Instrumenter.SWAPS).mean();
                    compares = compares + statPack.getStatistics(Instrumenter.COMPARES).mean();
                    System.out.println(statPack);
                }

                double averageSwap = swaps / cycles;
                double averageCompare = compares / cycles;
                double ratio =  averageCompare/averageSwap ;
                System.out.println( "-------------------------------------------------------------------------------------------------------");
                System.out.println( "Array Size: " + n + " Avg Swap: " + averageSwap + " Avg Compare: " + averageCompare + " Ratio: " + ratio);
            }
        }
    }
}
