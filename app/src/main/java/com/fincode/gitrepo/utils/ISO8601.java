package com.fincode.gitrepo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

//  ласс дл¤ работы с датой в формате ISO8601
public final class ISO8601 {
	public static String fromCalendar(final Calendar calendar) {
		Date date = calendar.getTime();
		String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
				Locale.ENGLISH).format(date);
		return formatted.substring(0, 22) + ":" + formatted.substring(22);
	}

	public static String now() {
		return fromCalendar(GregorianCalendar.getInstance());
	}

	public static Calendar toCalendar(final String iso8601string)
			throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance();
		String s = iso8601string.replace("Z", "+00:00");
		try {
			s = s.substring(0, 22) + s.substring(23);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException("Invalid length", 0);
		}
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
				Locale.ENGLISH).parse(s);
		calendar.setTime(date);
		return calendar;
	}
}
