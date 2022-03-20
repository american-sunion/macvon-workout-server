package com.macvon.query;

import java.util.Map;

import com.google.common.collect.Maps;

public class DAO {
	private final String className;
	private final Map<String, Query> queryMaps = Maps.newHashMap();
	public DAO(final String className) {
		this.className = className;
	}
	public String getClassName() {
		return className;
	}
	public Map<String, Query> getQueryMaps() {
		return queryMaps;
	}
	public void addQuery(Query query) {
		queryMaps.put(query.getId(), query);
	}
	public int queryNumber() {
		return queryMaps.size();
	}
}
