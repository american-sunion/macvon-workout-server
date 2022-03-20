package com.macvon.query;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum QueryLoader {
	INSTANCE;
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryLoader.class);
	static {
		LOGGER.info("Start to Initialing application...");
		try {
			LOGGER.info("initializing sqlMap config...");
			QueryMapConfig.INSTANCE.loadSqlMaps("queryMap.xml");
			QueryMap queryMap = QueryMapConfig.INSTANCE.getQueryMap();
			LOGGER.info("load sqlMap config...sqlMap: {}", queryMap.info());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getSql(String daoClassName, String methodName) {
		QueryMap sqlMap = QueryMapConfig.INSTANCE.getQueryMap();
		Map<String, DAO> daoMap = sqlMap.getDaoMaps();
		DAO dao = daoMap.get(daoClassName);
		Query query = dao.getQueryMaps().get(methodName);
		return query.getSql();
	}
}
