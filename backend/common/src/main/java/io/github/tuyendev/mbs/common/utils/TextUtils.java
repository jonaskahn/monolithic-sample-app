package io.github.tuyendev.mbs.common.utils;

import org.apache.commons.lang3.StringUtils;

public abstract class TextUtils {

	public static final String leftPad(String str, int size) {
		return StringUtils.leftPad(str, size, '0');
	}
}
