package com.macvon.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.macvon.domain.auth.AuthUser;

public class AppUtils {
	private static final Pattern specialChar = Pattern.compile("[$@#&%~!_%(=*]");
	private static final Pattern digit = Pattern.compile("[0-9]");

	/**
	 * get request IP address
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String DetermineCompName(HttpServletRequest request) {
		return request.getRemoteHost();
	}

	public static boolean hasContentMatch(Set<String> texts, String search) {
		for (String text : texts) {
			if (StringUtils.containsIgnoreCase(text, search)) {
				return true;
			}
		}
		return false;
	}

	public static String maskEmail(String email) {
		StringBuilder sb = new StringBuilder();
		boolean isAfterAt = false;
		boolean showSuffix = false;
		int startMask = 2;
		for (int i = 0; i < email.length(); ++i) {
			if (i < startMask) {
				sb.append(email.charAt(i));
			} else if (email.charAt(i) == '@') {
				int addInt = generateRamdomInt(1, 5);
				for (int j = 0; j < addInt; j++) {
					sb.append("*");
				}
				isAfterAt = true;
				sb.append(email.charAt(i));

			} else if (isAfterAt && email.charAt(i) == '.') {
				sb.append(email.charAt(i));
				showSuffix = true;
			} else if (showSuffix) {
				sb.append(email.charAt(i));
			} else {
				sb.append("*");
			}

		}
		return sb.toString();
	}

	public static int generateRamdomInt(int low, int high) {
		Random r = new Random();
		return r.nextInt(high - low) + low;
	}

	public static String getTransactionFieldID() {
		return "" + System.currentTimeMillis();
	}

	public static String maskString(String acct, int showLength) {
		int maskedLength = acct.length() - showLength;
		StringBuilder masked = new StringBuilder();
		for (int i = 0; i < acct.length(); i++) {
			if (i < maskedLength) {
				masked.append("*");
			} else {
				masked.append(acct.charAt(i));
			}
		}
		return masked.toString();
	}

	public static String SQLReplace(String sql, int repeat, String symbol, String replace) {
		for (int i = 0; i < repeat; i++) {
			sql = sql.replace(symbol, replace);
		}
		return sql;
	}

	public static String convertToJson(Object object) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getLast4digitPhoneNumber(String phone) {
		if (phone.length() > 4) {
			return phone.substring(phone.length() - 4);
		}
		return phone;
	}

	public static boolean isNumeric(String strNum) {
		return strNum.matches("-?\\d+(\\.\\d+)?");
	}

	public static String getConfigVal(String key, Environment env) {
		String sysVal = System.getProperty(key);
		if (sysVal != null) {
			return sysVal;
		} else {
			return env.getProperty(key);
		}
	}

	public static String getAuthApiUrl(String path, Environment env) {
		return getConfigVal("auth.url.base", env) + "/auth/" + path;
	}

	public static String getApiUrl(String path, Environment env) {
		return getConfigVal("auth.url.base", env) + "/api/" + path;
	}

	public static long getConfigLongVal(String key, Environment env) {
		String val = getConfigVal(key, env);
		if (val != null) {
			return Long.parseLong(val);
		}
		return -1L;
	}

	public static <T> Collection<T> getIntersection(Collection<T> coll1, Collection<T> coll2) {
		return Stream.concat(coll1.stream(), coll2.stream()).filter(coll1::contains).filter(coll2::contains)
				.collect(Collectors.toSet());
	}

	public static <T> boolean hasIntersect(Collection<T> coll1, Collection<T> coll2) {
		Collection<T> result = getIntersection(coll1, coll2);
		return !result.isEmpty();

	}

	public static String getPrefixUid(long userId) {
		return String.valueOf(userId).substring(0, 4);
	}

	public static BigDecimal doubleToBigDecimalCcy(double amount) {
		DecimalFormat df = new DecimalFormat("#.00");
		String amountStr = df.format(amount);
		return new BigDecimal(amountStr);
	}

	public static String doubleToStringCcy(double amount) {
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(amount);
	}

	public static BigDecimal intToBigDecimalCcy(int amount) {
		return new BigDecimal(amount);
	}

	public static Float stringToDecimal(String amount) {
		DecimalFormat df = new DecimalFormat("#.00");
		return Float.parseFloat(df.format(amount));
	}

	public static AuthUser getUser(Authentication auth) {
		return auth != null ? (AuthUser) auth.getPrincipal() : null;
	}

	public static String getUserName(Authentication auth) {
		return getUser(auth).getUsername();
	}

	public static boolean metPasswordRules(String pass) {
		String password = pass.replaceAll("\\s", "");

		if (password.isEmpty() || pass.length() != password.length() || password.length() < 8) {
			return false;
		}

		if (password.equals(password.toLowerCase()) || password.equals(password.toUpperCase())) {
			return false;
		}

		if (!specialChar.matcher(password).find() || !digit.matcher(password).find()) {
			return false;
		}

		return Character.isLetter(password.charAt(0));
	}

	public static boolean isValidPhone(String phoneNumber) {
		// System.out.println(phoneNumber);
		Pattern pattern = Pattern.compile("^\\+\\d{1}\\d{3}\\d{7}");
		Matcher matcher = pattern.matcher(phoneNumber);
		return matcher.matches() ? true : false;

	}

	public static boolean isValidPhoneFormat2(String phoneNumber) {
		// System.out.println(phoneNumber);
		Pattern pattern = Pattern.compile("\\d{3}\\d{7}");
		Matcher matcher = pattern.matcher(phoneNumber);
		return matcher.matches() ? true : false;

	}

	public static String convertPhone(String phoneNumber) {
		return "+1" + phoneNumber;
	}

	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
}
