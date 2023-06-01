package top.niunaijun.bcore.utils;

import java.util.Arrays;

public class ArrayUtils {
	public static<T> T[] trimToSize(T[] array, int size) {
		if (array == null || size == 0) {
			return null;
		} else if (array.length == size) {
			return array;
		}
		return Arrays.copyOf(array, size);
	}

	public static int indexOfFirst(Object[] array, Class<?> type) {
		if (!isEmpty(array)) {
			int N = -1;
			for (Object one : array) {
				N++;
				if (one != null && type == one.getClass()) {
					return N;
				}
			}
		}
		return -1;
	}

	public static int indexOfObject(Object[] array, Class<?> type, int sequence) {
		if (array == null) {
			return -1;
		}

		while (sequence < array.length) {
			if (type.isInstance(array[sequence])) {
				return sequence;
			}
			sequence++;
		}
		return -1;
	}

	public static int indexOfLast(Object[] array, Class<?> type) {
		if (!isEmpty(array)) {
			for (int N = array.length; N > 0; N--) {
				Object one = array[N - 1];
				if (one != null && one.getClass() == type) {
					return N - 1;
				}
			}
		}
		return -1;
	}

	public static int[] toInt(Integer[] array) {
		int[] newArray = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}
}
