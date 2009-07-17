package edu.ou.utils;

public final class SortUtils {
	/**
     * A generics-friendly sort method for symmetrically sorting a key array and a values array in place.
     *
     * @param keys The key array to sort.
     * @param values the values array.  Should be the same size as keys.
     */
    public static <K extends Comparable<K>, V> void symmetricSort(K[] keys, V[] values) {
        assert(keys.length == values.length);
        symmetricQuickSort(keys, values, 0, keys.length - 1);
    }

    /**
     * A generics-friendly quick sort for symmetrically sorting a key array and a values array in place.
     *
     * This code is adapted from James Gosling and Kevin A. Smith's quicksort code available at
     * http://java.sun.com/applets/jdk/1.0/demo/SortDemo/QSortAlgorithm.java
     *
     * @param keys The key array to sort.
     * @param values The values array.  Should be the same size as keys.
     * @param lo0 The current low index of the keys array.
     * @param hi0 The current hi index of the keys array.
     */
    private static <K extends Comparable<K>, V> void symmetricQuickSort(K[] keys, V[] values, int lo0, int hi0) {
        assert(keys.length == values.length);
        int lo = lo0;
        int hi = hi0;
        K mid;

        if (hi0 > lo0) {
            //pick pivot element
            mid = keys[(lo0 + hi0) / 2];

            //loop through the array until indices cross
            while (lo <= hi) {
                //find the first element that is greater than or equal to
                //the partition element starting from the left Index.
                while ((lo < hi0) && (keys[lo].compareTo(mid) < 0))
                    ++lo;

                //find an element that is smaller than or equal to
                //the partition element starting from the right Index.

                while ((hi > lo0) && (keys[hi].compareTo(mid) > 0))
                    --hi;

                //if the indexes have not crossed, swap
                if (lo <= hi) {
                    K ktmp = keys[lo];
                    V vtmp = values[lo];

                    keys[lo] = keys[hi];
                    values[lo] = values[hi];

                    keys[hi] = ktmp;
                    values[hi] = vtmp;

                    ++lo;
                    --hi;
                }
            }

            //If the right index has not reached the left side of array,sort the left partition.
            if (lo0 < hi)
                symmetricQuickSort(keys, values, lo0, hi);

            //If the left index has not reached the right side of array, sort the right partition.
            if (lo < hi0)
                symmetricQuickSort(keys, values, lo, hi0);

        }
    }
}
