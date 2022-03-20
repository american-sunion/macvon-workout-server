package com.macvon.utils;

import java.util.Random;

public class RandomUtils {
	public static String randomStr(int length) {
		String numbers = "0123456789";
		String capitalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String smallChars = "abcdefghijklmnopqrstuvwxyz";
		//String symbols = "!@#$%^&*_=+-.?<>)";
		String symbols = "!&*_.@$)";
		StringBuilder sb = new StringBuilder();
		String values = capitalChars + smallChars + numbers + symbols + smallChars;
		Random rndm_method = new Random();
		for (int i = 0; i < length; i++) {
			if(i==0) {
				sb.append(capitalChars.charAt(rndm_method.nextInt(capitalChars.length())));
			} else {
				sb.append(values.charAt(rndm_method.nextInt(values.length())));
			}
			
		}
		return sb.toString();
	}
	public static long getId() {
		return getRandomNumber(10000000, 1000000000);
	}
	public static long getRdmLongId() {
		return getRandomLongNumber(10000000, 999999999);
	}
	public static int getRandomNumber(int min, int max) {
	    return (int) Math.floor(Math.random() * (max - min + 1)) + min;
	}
	public static long getRandomLongNumber(int min, int max) {
	    return (long) Math.floor(Math.random() * (max - min + 1)) + min;
	}
	public static int getRandomSixNumber() {
		return getRandomNumber(100000, 999999);
	}
	public static int getRandom4Number() {
		return getRandomNumber(1000, 9999);
	}
	public static String generateRandom(int length) {
	    Random random = new Random();
	    char[] digits = new char[length];
	    digits[0] = (char) (random.nextInt(9) + '1');
	    for (int i = 1; i < length; i++) {
	        digits[i] = (char) (random.nextInt(10) + '0');
	    }
	    return new String(digits);
	}
	public static String generateUid() {
	    return generateRandom(10);
	}
}
