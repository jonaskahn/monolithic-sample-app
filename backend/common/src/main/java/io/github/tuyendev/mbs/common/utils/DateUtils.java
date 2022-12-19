package io.github.tuyendev.mbs.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public abstract class DateUtils {

	public static LocalDateTime dateToLocalDateTime(Date date) {
		if (date == null) return null;
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		if (localDateTime == null) return null;
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}
