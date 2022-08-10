package edu.neu.coe.huskySort.sort.simple;
import edu.neu.coe.huskySort.sort.ComparisonSortHelper;
import edu.neu.coe.huskySort.sort.HelperFactory;
import edu.neu.coe.huskySort.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuickSort_Standard<X extends Comparable<X>> extends QuickSort<X>  {

    public static final String DESCRIPTION = "QuickSort standard";

    public QuickSort_Standard(String description, int N, Config config) {
        super(description, N, config);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public QuickSort_Standard(ComparisonSortHelper<X> helper) {
        super(helper);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Standard
     *
     * @param N the number elements we expect to sort.
     * @param config the configuration.
     */
    public QuickSort_Standard(int N, Config config) {
        this(DESCRIPTION, N, config);
    }

    /**
     * Constructor for QuickSort
     *
     * @param config the configuration.
     */
    public QuickSort_Standard(Config config) {
        this(0, config);
    }

    @Override
    public Partitioner<X> createPartitioner() {
        return new Partitioner_Basic(getHelper());
    }

    public class Partitioner_Basic implements Partitioner<X> {

        public Partitioner_Basic(ComparisonSortHelper<X> helper) {
            this.helper = helper;
        }

        /**
         * Method to partition the given partition into smaller partitions.
         *
         * @param partition the partition to divide up.
         * @return an array of partitions, whose length depends on the sorting
         * method being used.
         */
        public List<Partition<X>> partition(Partition<X> partition) {

            final X[] xs = partition.xs;
            final int from = partition.from;
            final int to = partition.to;
            final int hi = to - 1;
            X v = xs[from];
            int i = from;
            int j = to;

            if (helper.instrumented()) {
                while (true) {
                    while (i < hi && helper.compare(xs[++i], v)<0) {

                    }
                    while (j > from && helper.compare(v, xs[--j])<0) {

                    }
                    if (i >= j) break;
                    helper.swap(xs, i, j);
                }
                helper.swap(xs, from, j);
            } else {
                while (true) {
                    while (i < hi && xs[++i].compareTo(v) < 0) {}
                    while (j > from && xs[--j].compareTo(v) > 0) {}
                    if (i >= j) break;
                    swap(xs, i, j);
                }
                swap(xs, from, j);
            }

            List<Partition<X>> partitions = new ArrayList<>();
            partitions.add(new Partition<>(xs, from, j));
            partitions.add(new Partition<>(xs, j + 1, to));
            return partitions;
        }

        private void swap(X[] ys, int i, int j) {
            X temp = ys[i];
            ys[i] = ys[j];
            ys[j] = temp;
        }

        private final ComparisonSortHelper<X> helper;
    }
    /**
     * Method to run quicksort and get values for compares
     * and swaps for different array sizes using StatPack.
     */
    public static void main(String[] args) throws IOException {
        int k = 13;
        Integer[] sizeOfArray = new Integer[k];
        for(int i=0;i<k;i++){
            sizeOfArray[i] = (int)Math.pow(2, 7 + i);
        }
        int cycles = 1000;
        System.out.println("Averaging across " + cycles + " runs");
        for (Integer n : sizeOfArray) {
            double swaps = 0.0;
            double compares = 0.0;
            for (int t = 0; t < cycles; t++) {
                final Config config = Config.setupConfig("true", "", "", "", "");
                final ComparisonSortHelper<Integer> helper = HelperFactory.create("quick sort", n, config);
                final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n));
                QuickSort_Standard<Integer> s = new QuickSort_Standard(helper);
                s.init(n);
                helper.preProcess(xs);
                Integer[] ys = s.sort(xs);
                helper.postProcess(ys);
                final PrivateMethodInvoker privateMethodTester = new PrivateMethodInvoker(helper);
                final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
                swaps = swaps + statPack.getStatistics(Instrumenter.SWAPS).mean();
                compares = compares + statPack.getStatistics(Instrumenter.COMPARES).mean();
            }
            double averageSwap = swaps / cycles;
            double averageCompare = compares / cycles;
            double ratio =  averageCompare/averageSwap ;
            System.out.println( "-------------------------------------------------------------------------------------------------------");
            System.out.println( "Array Size: " + n + " Avg Swap: " + averageSwap + " Avg Compare: " + averageCompare + " Ratio: " + ratio);
        }
    }
}
