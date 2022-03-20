package com.macvon.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * date utils
 * 
 * @author brianwu
 *
 */
public class DateUtils {
	public final static DateFormat DF_MMDDYYHHMMSS = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	public final static DateFormat DF_MMDDYYHHMMSSZ = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss'Z'");
	// public final static DateFormat YYYYMMDDTHHMMSS = new
	// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final TimeZone utc = TimeZone.getTimeZone("UTC");
	private static final SimpleDateFormat YYYYMMDDTHHMMSS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat YYYYMMDDHHMMSSMS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	static {
		YYYYMMDDTHHMMSS.setTimeZone(utc);
		YYYYMMDD.setTimeZone(utc);
	}

	public static Timestamp getFutureDateTs() {
		LocalDateTime datetime = LocalDateTime.of(2029, 12, 31, 0, 0);
		return Timestamp.valueOf(datetime);
	}

	public static Timestamp getTruncatedCurrentDateTime() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime datetime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
		return Timestamp.valueOf(datetime);
	}

	public static Date getTruncatedCurrentDate()  {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			return formatter.parse(formatter.format(new Date()));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	public static Date getStartOfCurrentDate() {
		return Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Timestamp getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	    return new Timestamp(cal.getTimeInMillis());
	}
	public static Timestamp getCurrentDateTS() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	    return new Timestamp(cal.getTimeInMillis());
	}
	public static String now() {
		return getCurrentDate(new Date());
	}
	public static String yestoday() {
		Date date = getDateFromNow(1);
		return getCurrentDate(date);
	}
	public static String getCurrentDate(Date date) {
		SimpleDateFormat sdfAmerica = new SimpleDateFormat("MM/dd/yyyy");
		sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return sdfAmerica.format(date);
	}
	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static String dateToMMDDYYHHMMSSNYC(Date date) {
		SimpleDateFormat sdfAmerica = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return sdfAmerica.format(date);
	}

	public static String dateToMMDDYY(Date date) {
		DateFormat DF_MMDDYY = new SimpleDateFormat("MM/dd/yyyy");
		DF_MMDDYY.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return DF_MMDDYY.format(date);
	}
	public static String dateToTxnMMDDYY(Date date) {
		DateFormat DF_MMDDYY = new SimpleDateFormat("MM/dd/yyyy");
		//DF_MMDDYY.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		return DF_MMDDYY.format(date);
	}
	public static int dateToYYYYMMDDHHMMSSMS(Date date) {
		return new Integer(YYYYMMDDHHMMSSMS.format(date));
	}

	public static String dateToYYYYMMDDHHMMSS(Date date) {
		SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return sdfAmerica.format(date);
	}

	public static String dateToYYYYMMDD(Date date) {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	public static String[] monthStartAndEndDayInLastOneYear(int month) {
		String[] dates = new String[2];
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (month > (currentMonth + 1)) {
			year = year - 1;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1, 0, 0);
		Date firstDateOfMonth = calendar.getTime();
		Calendar endCalendar = calendar;
		endCalendar.add(Calendar.MONTH, 1);
		endCalendar.set(Calendar.DAY_OF_MONTH, 1);
		endCalendar.add(Calendar.DATE, -1);

		Date lastDayOfMonth = endCalendar.getTime();

		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dates[0] = sdf.format(firstDateOfMonth);
		dates[1] = sdf.format(lastDayOfMonth);

		return dates;
	}

	public static boolean isNowActive(Date effDate, Date exp) {
		return isActive(new Date(), effDate, exp);
	}

	public static boolean isActive(Date date, Date effDate, Date exp) {
		return date.getTime() >= effDate.getTime() && date.getTime() <= exp.getTime();
	}
	public static boolean isUnlocked(Date lockeddate) {
		Date unlockedDateTime = addMinutes(lockeddate, 30);
		return new Date().getTime() >= unlockedDateTime.getTime();
	}
	public static String[] getStartAndEndDayFromNow(int days) {
		String[] dates = new String[2];
		Calendar calendar = Calendar.getInstance();
		Date startDay = calendar.getTime();

		Calendar endCalendar = calendar;
		endCalendar.add(Calendar.DATE, -days);
		Date endDay = endCalendar.getTime();
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dates[0] = sdf.format(startDay);
		dates[1] = sdf.format(endDay);

		return dates;
	}

	public static String[] monthStartAndEndDayFromNow(int month) {
		String[] dates = new String[2];
		// int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		// int year = Calendar.getInstance().get(Calendar.YEAR);
		// if(month>(currentMonth+1)) {
		// year = year-1;
		// }

		Calendar calendar = Calendar.getInstance();
		Date firstDateOfMonth = calendar.getTime();

		Calendar endCalendar = calendar;
		endCalendar.add(Calendar.MONTH, -month);
		endCalendar.set(Calendar.DAY_OF_MONTH, 1);
		// endCalendar.add(Calendar.DATE, -1);

		Date lastDayOfMonth = endCalendar.getTime();

		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dates[0] = sdf.format(firstDateOfMonth);
		dates[1] = sdf.format(lastDayOfMonth);

		return dates;
	}

	public static Date yyyymmddToDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static Date expirationDate(String date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			return formatter.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static Date MMddyyyyToDate(String date) {
		try {
			return new SimpleDateFormat("MM/dd/yyyy").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date yyyy_mm_ddToDate(String date) {
		try {
			return new SimpleDateFormat("yyyyMMdd").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long yyyymmddHHmmToDate(String date, String time) {
		try {
			return (new SimpleDateFormat("yyyyMMdd HHmm").parse(date + " " + time)).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertDateTime(int date, int time) {
		try {
			String dateStr = Integer.toString(date);
			String timeTmp = Integer.toString(time);
			String h = timeTmp.substring(0, 2);
			String m = timeTmp.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(dateStr + " " + h + ":" + m);
			return new SimpleDateFormat("MM/dd/yyyy HH:mm").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertFormatedTime(String time) {
		if (StringUtils.isEmpty(time)) {
			return "";
		}
		String h = time.substring(0, 2);
		String m = time.substring(2, 4);
		return h + ":" + m;
	}

	public static String convertDateTime(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("MM/dd/yyyy HH:mm").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertDateTimeYY(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("MM/dd/yy HH:mm").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertDateTimeDateFormat(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yyyy HH:mm").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertDateTimeDateTimeFormat(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yyyy H:m").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertDateTimeAM(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("MM/dd/yyyy hh:mm aa").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertDateTimeAMYY(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("MM/dd/yy hh:mm aa").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static String convertdateFromCSTWebAMDateFormat(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yyyy hh:mm a");
		return output.format(formatter2);
	}
	public static String convertdateFromCSTWebAMDateFormat2(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yy hh:mm a");
		return output.format(formatter2);
	}
	public static String convertdateFromCSTWebAMDateFormat3(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yy h:m a");
		return output.format(formatter2);
	}
	public static String convertdateFromCSTWebAMDateFormat4(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yy h:m");
		return output.format(formatter2);
	}


	public static String convertDateTimeAMDateTimeFormat(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yyyy h:m aa").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long mmddyyyyToDateLong(String date) {
		try {
			return (new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.S").parse(date + " 00:00:00.0")).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String increaseTime(String input, int num) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date d = df.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, num);
			return df.format(cal.getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	public static String covertToyyyy_MM_ddThhmmssZ(Date date) {
		return YYYYMMDDTHHMMSS.format(date);
	}

	public static Date ddMMMyyyyhhmmToDate(String date) {
		try {
			return new SimpleDateFormat("dd MMM yyyy hh:mm").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long convertToNanoSecDate(Date date) {
		return date.getTime() * 1000000;
	}

	/**
	 * check if date is expired.
	 * 
	 * @param expireDate
	 * @return
	 */
	public static boolean isExpiredAsOfToday(Date expirationDate) {
		Date now = Calendar.getInstance().getTime();
		if (expirationDate.before(now)) {
			return true;
		}
		return false;
	}

	public static boolean isBefore(Date thisDate, Date thatDate) {
		if (thisDate.before(thatDate)) {
			return true;
		}
		return false;
	}

	public static boolean isActive(Date eff) {
		Date now = Calendar.getInstance().getTime();
		return eff.compareTo(now) <= 0;
	}

	/**
	 * add minutes
	 * 
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date addSecond(Date date, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, sec);
		return cal.getTime();
	}

	/**
	 * add minutes
	 * 
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * get different duration period from last time.
	 * 
	 * @param past
	 * @return
	 */
	public static String findDuration(Date past) {
		long diff = Calendar.getInstance().getTime().getTime() - past.getTime();
		String duration = "";
		int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
		if (diffDays > 0) {
			duration += diffDays + " day ";
		}
		diff -= diffDays * (24 * 60 * 60 * 1000);

		int diffhours = (int) (diff / (60 * 60 * 1000));
		if (diffhours > 0) {
			duration += diffhours + " hour ";
		}
		diff -= diffhours * (60 * 60 * 1000);

		int diffmin = (int) (diff / (60 * 1000));
		if (diffmin > 0) {
			duration += diffmin + " min ";
		}
		diff -= diffmin * (60 * 1000);

		int diffsec = (int) (diff / (1000));
		if (diffsec > 0) {
			duration += diffsec + " sec";
		}
		return duration;

	}

	/**
	 * find duration in seconds by compare current date.
	 * 
	 * @param past
	 * @return
	 */
	public static int findDiffInSecFromFutureToNow(Date future) {
		return findDurationInSec(future.getTime(), Calendar.getInstance().getTime().getTime());
	}

	/**
	 * find duration in seconds by compare current date.
	 * 
	 * @param past
	 * @return
	 */
	public static int findDiffInSecFromNowToPast(Date past) {
		return findDurationInSec(Calendar.getInstance().getTime().getTime(), past.getTime());
	}

	/**
	 * find duration in seconds by compare two dates.
	 * 
	 * @param from
	 * @param toPast
	 * @return
	 */
	public static int findDurationInSec(Date from, Date toPast) {
		return findDurationInSec(from.getTime(), toPast.getTime());
	}

	private static int findDurationInSec(long from, long toPast) {
		long diff = from - toPast;
		return (int) diff / 1000;
	}

	public static Date getDateFromNow(int day) {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -day);
		return cal.getTime();
	}

	public static String getDateStrFromNow(int day) {
		Date date = getDateFromNow(day);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yy");
		return sdf.format(date);
	}

	public static String getYYMMDDDateStrFromNow(int day) {
		Date date = getDateFromNow(day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}

	public static String getYYYYMMDDNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	public static String getFutureYYYYMMDD() {
		return "20291231";
	}
	public static boolean isBetween(Date d, Date min, Date max) {
		return d.compareTo(min) >= 0 && d.compareTo(max) <= 0;
	}

	public static String convertDateMMDDYYYY(String date) {
		if (date != null) {
			return date.substring(4, 6) + "/" + date.substring(6) + "/" + date.substring(0, 4);
		}
		return date;
	}

	public static String convertdateFromCST(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MMM dd HH:mm");
		return output.format(formatter2);
	}

	public static String convertdateTimeFromCST(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWeb(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZonedDateTime output =null;
		if(targetZoon!=null) {
			ZoneId targetZoneId = ZoneId.of(targetZoon);
			output = zdt.withZoneSameInstant(targetZoneId);	
		} else {
			output = zdt;
		}

		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWebYY(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWebDateFormat(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm a");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWebDateTimeFormat(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yyyy H:m a");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWebAM(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWebAMYY(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a");
		return output.format(formatter2);
	}

	public static String convertdateFromCSTWebAMDateTimeFormat(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId = ZoneId.of(targetZoon);
		ZonedDateTime output = zdt.withZoneSameInstant(targetZoneId);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yyyy h:m a");
		return output.format(formatter2);
	}
	public static String convertDateTimeAMDateFormat(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yyyy hh:mm aa").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static String convertDateTimeAMDateFormat2(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yy hh:mm aa").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static String convertDateTimeAMDateFormat3(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yy h:m aa").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static String convertDateTimeAMDateFormat4(String date, String time) {
		try {
			String h = time.substring(0, 2);
			String m = time.substring(2, 4);
			Date cnvertDate = new SimpleDateFormat("yyyyMMdd hh:mm").parse(date + " " + h + ":" + m);
			return new SimpleDateFormat("M/d/yy h:m").format(cnvertDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static boolean hasTime(String time, String text, int range) {
		Set<String> allTimes = getTimeInRange(time, range);
		for (String ts : allTimes) {
			if (StringUtils.containsIgnoreCase(text, ts)) {
				return true;

			}
		}
		return false;
	}

	public static boolean hasTime(String text, Set<String> allTimes) {
		for (String ts : allTimes) {
			if (StringUtils.containsIgnoreCase(text, ts)) {
				return true;

			}
		}
		return false;
	}

	public static Set<String> getTimeInRange(String input, int range) {
		Set<String> allTimes = new HashSet<>();
		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			SimpleDateFormat df2 = new SimpleDateFormat("H:m");
			SimpleDateFormat df3 = new SimpleDateFormat("h:m");
			Date d = df.parse(input);
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(d);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(d);
			String baseTime = df.format(cal1.getTime());
			allTimes.add(baseTime);
			String baseTime1 = df2.format(cal1.getTime());
			allTimes.add(baseTime1);
			String baseTime2 = df3.format(cal1.getTime());
			allTimes.add(baseTime2);
			// postive and negtive
			for (int i = 0; i < range; i++) {
				cal1.add(Calendar.MINUTE, 1);
				String timeA1 = df.format(cal1.getTime());
				allTimes.add(timeA1);
				String timeA2 = df2.format(cal1.getTime());
				allTimes.add(timeA2);
				String timeA3 = df3.format(cal1.getTime());
				allTimes.add(timeA3);
				cal2.add(Calendar.MINUTE, -1);
				String timeB1 = df.format(cal2.getTime());
				allTimes.add(timeB1);
				String timeB2 = df2.format(cal2.getTime());
				allTimes.add(timeB2);
				String timeB3 = df3.format(cal2.getTime());
				allTimes.add(timeB3);
			}

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return allTimes;
	}

	public static ZonedDateTime convertdateFromCSTToDate(String date, String time, String targetZoon) {
		ZoneId cstZoneId = ZoneId.of("America/Chicago");
		String formatString = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
				+ time.substring(0, 2) + ":" + time.substring(2, 4);
		LocalDateTime ldt = LocalDateTime.parse(formatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		ZonedDateTime zdt = ldt.atZone(cstZoneId);
		ZoneId targetZoneId =null;
		if(targetZoon==null) {
			return zdt;
		} else {
			targetZoneId = ZoneId.of(targetZoon);
		}
		return zdt.withZoneSameInstant(targetZoneId);
	}

	public static Set<String> getAllFormatedDate(ZonedDateTime inputDate) {
		Set<String> allDates = new HashSet<>();
		try {
			DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yy");
			DateTimeFormatter df2 = DateTimeFormatter.ofPattern("M/d/yy");
			DateTimeFormatter df3 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			DateTimeFormatter df4 = DateTimeFormatter.ofPattern("M/d/yyyy");
			String format1 = inputDate.format(df);
			allDates.add(format1);
			String format2 = inputDate.format(df2);
			allDates.add(format2);
			String format3 = inputDate.format(df3);
			allDates.add(format3);
			String format4 = inputDate.format(df4);
			allDates.add(format4);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return allDates;
	}

	public static boolean hasDate(String text, Set<String> allDates) {
		for (String ts : allDates) {
			if (StringUtils.containsIgnoreCase(text, ts)) {
				return true;

			}
		}
		return false;
	}
	// public static void main(String[] args) {
	// ZonedDateTime inputDate = convertdateFromCSTToDate("20191008", "1130",
	// "America/New_York");
	// Set<String> alldates = getAllFormatedDate(inputDate);
	// boolean isMatch = hasDate("10/8/2019", alldates);
	// System.out.println(isMatch);
	// }

}

