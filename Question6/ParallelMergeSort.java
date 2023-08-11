import java.util.Arrays;

public class ParallelMergeSort {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); // Use all available cores

    public static void parallelMergeSort(int[] arr) {
        parallelMergeSort(arr, 0, arr.length - 1, NUM_THREADS);
    }

    private static void parallelMergeSort(int[] arr, int left, int right, int numThreads) {
        if (numThreads <= 1) {
            // If only one thread, perform sequential merge sort
            mergeSort(arr, left, right);
            return;
        }

        // Divide the array into two halves and sort them in parallel using multiple threads
        int mid = left + (right - left) / 2;
        Thread leftThread = new Thread(() -> parallelMergeSort(arr, left, mid, numThreads / 2));
        Thread rightThread = new Thread(() -> parallelMergeSort(arr, mid + 1, right, numThreads / 2));
        leftThread.start();
        rightThread.start();

        // Wait for both threads to complete
        try {
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Merge the sorted halves
        merge(arr, left, mid, right);
    }

    private static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        System.arraycopy(arr, left, leftArray, 0, n1);
        System.arraycopy(arr, mid + 1, rightArray, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                arr[k++] = leftArray[i++];
            } else {
                arr[k++] = rightArray[j++];
            }
        }

        while (i < n1) {
            arr[k++] = leftArray[i++];
        }

        while (j < n2) {
            arr[k++] = rightArray[j++];
        }
    }

    public static void main(String[] args) {
        int[] arr = {6, 4, 3, 2, 8, 5, 1, 7};

        System.out.println("Original Array: " + Arrays.toString(arr));
        parallelMergeSort(arr);
        System.out.println("Sorted Array: " + Arrays.toString(arr));
    }
}
