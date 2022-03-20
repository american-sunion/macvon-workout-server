package com.macvon.query;

import java.util.Map;

import com.google.common.collect.Maps;

public class QueryMap {
	private final Map<String, DAO> daoMaps = Maps.newHashMap();

	public Map<String, DAO> getDaoMaps() {
		return daoMaps;
	}
	public void addDao(DAO dao) {
		daoMaps.put(dao.getClassName(), dao);
	}
	public String info() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, DAO> entry : daoMaps.entrySet()) {
			String key = entry.getKey();
			DAO dao = entry.getValue();
			sb.append(key).append(" : querys:(").append(dao!=null? dao.queryNumber() : 0).append("); ");
		}
		return sb.toString();
	}
}