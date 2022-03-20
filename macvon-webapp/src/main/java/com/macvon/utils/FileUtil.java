package com.macvon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * generate file name per timestamp
	 * 
	 * @param multiPart
	 * @return
	 */
	public final static String generateFileName(MultipartFile multiPart) {
		return DateUtils.covertToyyyy_MM_ddThhmmssZ(new Date()) + "-"
				+ multiPart.getOriginalFilename().replace(" ", "_");
	}
	/**
	 * convert MultipartFile as local file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public final static File convertMultiPartToFile(MultipartFile file, String fileDir) throws IOException {
	    File directory = new File(fileDir);
	    if (!directory.exists()){
	        directory.mkdir();
	    }
		File convFile = new File(fileDir + file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	public static Properties loadPropertiesFromArgs(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			String errorMsg = "main args should not be empty, excepted format: file, file1.. fileN k1=v1 k2=v2 .. kN=vN";
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
		Properties props = new Properties();
		for (String arg : args) {
			if (arg.endsWith(".properties")) {
				loadPropertiesFromFile(props, arg);
			}
		}
		return props;

	}

	public static void loadPropertiesFromFile(Properties props, String fname) throws Exception {
		logger.info("loadPropertiesFromFile: {}", fname);
		try (FileInputStream inputStream = new FileInputStream(fname)) {
			props.load(inputStream);
		}
	}
	/**
	 * load file
	 * @param filename
	 * @return
	 */
	public static InputStream getInputStream(String filename) {
		InputStream inStream = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl == null) {
				cl = FileUtil.class.getClassLoader();
			}
			logger.debug("LOADING {}. ClassLoader ===> {}.", filename, cl.getClass().getSimpleName());

			inStream = cl.getResourceAsStream(filename);
			if (inStream == null) {
				inStream = getResource(filename);
				if (inStream == null) {
					throw new NullPointerException("UNABLE_TO_LOAD " + filename + ". InputStream is null.");
				}
			}
			return inStream;
		} catch (final Exception e) {
			logger.error("Failed to load {} file: {}", filename, e);
			return null;
		}
	}
	/**
	 * Load data in stream from URI.
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public final static InputStream getResource(final String uri) throws IOException {
		final File file = new File(uri);
		final URI fileURI = file.toURI();
		try {
			return fileURI.toURL().openStream();
		}
		catch (final MalformedURLException e) {
			return new FileInputStream(uri);
		}
	}
}
