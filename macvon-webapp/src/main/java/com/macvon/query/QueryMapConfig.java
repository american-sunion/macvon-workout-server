package com.macvon.query;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.macvon.domain.Pair;
import com.macvon.utils.FileUtil;

/**
 * read queryMap and store in sqlMapConfig Object.
 * @author xun wu
 *
 */
public enum QueryMapConfig {
	INSTANCE;
	public final static Logger LOGGER = LoggerFactory.getLogger(QueryMapConfig.class);
	private final QueryMap queryMap = new QueryMap();
	public final Map<String, List<Pair<String, Field>>> nativeSqlOutputEntityMaps = Maps.newHashMap();
	public void loadSqlMaps(String sqlFileName) {
		InputStream inputStream = null;
		try {
			inputStream = FileUtil.getInputStream(sqlFileName);
			if (inputStream != null) {
		        SAXBuilder jdomBuilder = new SAXBuilder();
		        Document document = jdomBuilder.build(inputStream);
				if(document==null) {
					throw new RuntimeException("XML document not created! " + sqlFileName);
				}
				QueryMapConfigParser.INTSANCE.parseConfigurator(document.getRootElement());
			} else {
				throw new RuntimeException("file not found! " + sqlFileName);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (final IOException e) {
				LOGGER.error("Failure..." + e.getMessage());
				// e.printStackTrace();
			}
		}
	}

	public QueryMap getQueryMap() {
		return queryMap;
	}
}