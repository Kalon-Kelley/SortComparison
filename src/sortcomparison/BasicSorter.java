package sortcomparison;

import static sbcc.Core.*;

import java.util.*;

import static java.lang.System.*;
import static org.apache.commons.lang3.StringUtils.*;

public class BasicSorter implements Sorter {

	@Override
	// method to use the insertion sort algorithm on a specific array given an index
	// and number of elements
	public void insertionSort(String[] data, int fi, int n) {
		for (int i = 1; i < n; i++) {
			// findSpot(data, data[i], i);
			int count = 0;
			String key = data[i + fi];
			int j = i - 1;
			while (j >= 0 && data[j + fi].compareTo(key) > 0) {
				count++;
				j--;
			}

			arraycopy(data, (i - count) + fi, data, ((i - count) + 1) + fi, count);
			data[(j + 1) + fi] = key;
		}

	}


	@Override
	// method to use the quicksort algorithm calling the partition function on
	// segments of a size greater than 15 and calling insertionSort on segments with
	// a size of 15 or smaller
	public void quickSort(String[] data, int fi, int n) {
		if (data.length <= 15) {
			insertionSort(data, fi, n);
		} else {
			int li = (fi + (n - 1));
			if (fi < li) {
				int pi = partition(data, fi, n);
				int nLeft = pi - fi;
				int nRight = li - pi;
				quickSort(data, fi, nLeft);
				quickSort(data, pi + 1, nRight);
			}
		}
	}


	@Override
	// method to partition an array given a starting index and number of elements
	public int partition(String[] data, int fi, int n) {
		int pivotIndex = getPivotIndex(data, fi, n);
		String pivot = data[pivotIndex];
		data[pivotIndex] = data[fi];
		data[fi] = pivot;
		int tbi = fi + 1;
		int tsi = fi + (n - 1);
		while (tbi < tsi) {
			while ((tbi < tsi) && (data[tbi].compareTo(pivot) <= 0)) {
				tbi++;
			}
			while ((tsi > fi) && (data[tsi].compareTo(pivot) > 0)) {
				tsi--;
			}
			if (tbi < tsi) {
				String temp = data[tbi];
				data[tbi] = data[tsi];
				data[tsi] = temp;
			}
		}
		if (pivot.compareTo(data[tsi]) >= 0) {
			String temp = data[fi];
			data[fi] = data[tsi];
			data[tsi] = temp;
			return tsi;
		} else {
			return fi;
		}

	}


	// method that gets the index of the pivot using the median of three method
	// unless there are fewer than three elements in the array then the pivot is the
	// first index
	private int getPivotIndex(String[] data, int fi, int n) {
		if (n >= 3) {
			int low = fi;
			int high = fi + (n - 1);
			int mid = (int) Math.ceil((low + high) / 2);
			int pivot;
			if (((data[low].compareTo(data[mid]) < 0) && (data[low].compareTo(data[high]) > 0))
					|| ((data[low].compareTo(data[mid]) > 0) && (data[low].compareTo(data[high]) < 0))) {
				pivot = low;
			} else if (((data[mid].compareTo(data[low]) < 0) && (data[mid].compareTo(data[high]) > 0))
					|| ((data[mid].compareTo(data[low]) > 0) && (data[mid].compareTo(data[high]) < 0))) {
				pivot = mid;
			} else {
				pivot = high;
			}
			return pivot;
		} else {
			return fi;
		}
	}


	@Override
	// method to use the merge sort algorithm recursively calling itself on segments
	// with a size greater than 15 otherwise the insertionSort method is called
	public void mergeSort(String[] data, int fi, int n) {
		if (n <= 15) {
			insertionSort(data, fi, n);
		} else {
			int middle = (int) Math.ceil((fi + (n + fi)) / 2);
			mergeSort(data, fi, middle - fi);
			mergeSort(data, middle, n - (middle - fi));
			merge(data, fi, middle - fi, n - (middle - fi));
		}
	}


	@Override
	// method to compare segments and merge them in lexicographical order
	public void merge(String[] data, int fi, int nl, int nr) {
		String[] temp = new String[nl + nr];
		int count = 0;
		int li = fi;
		int ri = fi + nl;
		while ((li < fi + nl) && (ri < ((fi + nl) + nr))) {
			if (data[li].compareTo(data[ri]) <= 0) {
				temp[count] = data[li];
				li++;
				count++;
			} else {
				temp[count] = data[ri];
				ri++;
				count++;
			}
		}
		if (li < fi + nl) {
			arraycopy(data, li, temp, count, (fi + nl) - li);
		} else if (ri < fi + nl + nr) {
			arraycopy(data, ri, temp, count, (fi + nl + nr) - ri);
		}
		arraycopy(temp, 0, data, fi, nl + nr);

	}


	@Override
	// method to call heapify method
	public void heapSort(String[] data) {
		int n = data.length;
		heapify(data);

		for (int i = n - 1; i > 0; i--) {
			String temp = data[0];
			data[0] = data[i];
			data[i] = temp;
			heapify(data, i, 0);
		}

	}


	@Override
	// creates more specific inputs to pass through to the other heapify method
	public void heapify(String[] data) {
		int n = data.length;
		for (int i = n / 2 - 1; i >= 0; i--)
			heapify(data, n, i);

	}


	// heapifys the array given the number of elements and starting index
	private void heapify(String[] data, int n, int i) {
		int largest = i;
		int left = 2 * i + 1;
		int right = 2 * i + 2;
		if (left < n && data[left].compareTo(data[largest]) > 0) {
			largest = left;
		}
		if (right < n && data[right].compareTo(data[largest]) > 0) {
			largest = right;
		}

		if (largest != i) {
			String swap = data[i];
			data[i] = data[largest];
			data[largest] = swap;
			heapify(data, n, largest);
		}

	}

}
