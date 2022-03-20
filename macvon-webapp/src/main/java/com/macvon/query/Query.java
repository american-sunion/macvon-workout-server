package com.macvon.query;

public class Query {
	private String id;
	private String sql;
	private Class<?> entity;
	public Query(final String id, final String sql) {
		this.id = id;
		this.sql = sql;
	}
	public String getId() {
		return id;
	}
	public String getSql() {
		return sql;
	}
	public Class<?> getEntity() {
		return entity;
	}
	public void setEntity(Class<?> entity) {
		this.entity = entity;
	}
	@Override
	public String toString() {
		return "Query [id=" + id + ", sql=" + sql + ", entity=" + entity + "]";
	}
}