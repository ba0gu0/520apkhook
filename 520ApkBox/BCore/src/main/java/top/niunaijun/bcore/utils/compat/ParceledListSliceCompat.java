package top.niunaijun.bcore.utils.compat;

import java.util.List;

import black.android.content.pm.ParceledListSlice;

public class ParceledListSliceCompat {
	public static Object create(List<?> list) {
		Object slice = ParceledListSlice._new1.newInstance(list);
		if (slice != null) {
			return slice;
		} else {
			slice = ParceledListSlice._new0.newInstance();
		}

		for (Object item : list) {
			ParceledListSlice.append.call(slice, item);
		}
		ParceledListSlice.setLastSlice.call(slice, true);
		return slice;
	}
}
