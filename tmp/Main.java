import java.io.*;
import java.util.*;

public class Main {
    static int callCount = 0;
    static int swapCount = 0;
    static int compCount = 0;

    static PrintStream callStream = open("calls.txt");
    static PrintStream swapStream = open("swaps.txt");
    static PrintStream compStream = open("comps.txt");

    static PrintStream open(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) f.createNewFile();
            return new PrintStream(f);
        }
        catch (Exception e) {
            return null;
        }
    }

    static void benchReset() {
        callCount = 0;
        swapCount = 0;
        compCount = 0;
    }

    static final int MAX = 1000;

    public static void main(String[] args) {
        int[] arr = new int[0];
        for (int size = 1; size <= MAX; size++) {
            arr = randArr(size);
            double calls = Double.POSITIVE_INFINITY;
            double swaps = Double.POSITIVE_INFINITY;
            double comps = Double.POSITIVE_INFINITY;
            int total = 100;
            for (int trial = 0; trial < total; trial++) {
                benchReset();
                shuffle(arr);
                heapsort(arr);
                calls = Math.min(calls, callCount);
                swaps = Math.min(swaps, swapCount);
                comps = Math.min(comps, compCount);
            }
            callStream.println(calls);
            swapStream.println(swaps);
            compStream.println(comps);
        }
        callStream.flush();
        swapStream.flush();
        compStream.flush();
    }

    static int[] randArr(int n) {
        int[] arr = new int[n];
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt();
        }
        return arr;
    }

    static void siftDown(int[] arr, int index, int size) {
        callCount++;
        int child1 = index * 2 + 1, child2 = index * 2 + 2;
        int max = index;
        if (child1 < size && less(arr[max], arr[child1])) max = child1;
        if (child2 < size && less(arr[max], arr[child2])) max = child2;
        if (max != index) {
            swap(arr, index, max);
            siftDown(arr, max, size);
        }
    }

    static void heapify(int[] arr) {
        int lastParent = (arr.length - 2) / 2;
        for (int i = lastParent; !less(i, 0); i--) siftDown(arr, i, arr.length);
    }
    
    static void heapsort(int[] arr) {
        if (arr.length < 2) return;
        heapify(arr);
        int size = arr.length;
        while (less(1, size)) {
            swap(arr, 0, size - 1);
            size--;
            siftDown(arr, 0, size);
        }
    }
    
    static void swap(int[] arr, int a, int b) {
        swapCount++;
        int t = arr[a];
        arr[a] = arr[b];
        arr[b] = t;
    }

    static boolean less(int a, int b) {
        compCount++;
        return a < b;
    }

    static void shuffle(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int n : arr) list.add(n);
        Collections.shuffle(list);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
    }
}
