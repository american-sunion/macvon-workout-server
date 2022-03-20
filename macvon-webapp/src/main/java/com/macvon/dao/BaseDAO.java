package com.macvon.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.macvon.domain.BaseDomain;
import com.macvon.utils.GlobalConstants;

@SuppressWarnings("unchecked")
public class BaseDAO {
	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class);
	@PersistenceContext
	protected EntityManager em;
	public Query createQuery(final String sql) {
		return em.createQuery(sql);
	}
	public <T> Query createQuery(final String sql, Class<T> domain) {
		return em.createQuery(sql, domain);
	}
	public Object findEntity(final String sql) {
		return em.createQuery(sql).getSingleResult();
	}
	public <T> T findEntityByKey(final Object primaryKey, Class<T> domain) {
		return em.find(domain, primaryKey);
	}
	public Object findEntity(final String sql, final Object... params) {
		Query query = createQuery(sql);
		setPatamsByPosition(query, params);
		return query.getSingleResult();
	}
	
	public <T> Object findEntityObj(final String sql, final Object... params) {
		Query query = createQuery(sql);
		setPatamsByPosition(query, params);
		List<T> list = query.getResultList();
		if (list == null || list.isEmpty()) {
	        return null;
	    }
	    return list.get(0);
	}
	
	public <T> Object findMaxEntity(final String sql, Class<T> domain, final Object... params) {
		Query query = createQuery(sql, domain);
		setPatamsByPosition(query, params);
		query.setMaxResults(1);
		List<T> list = query.getResultList();
		if (list == null || list.isEmpty()) {
			return null;
	    }
	    return list.get(0);
	}
	public <T> List<T> findMaxNEntity(final String sql, Class<T> domain, final int top, final Object... params) {
		Query query = createQuery(sql, domain);
		setPatamsByPosition(query, params);
		query.setMaxResults(top);
		List<T> list = query.getResultList();
		List<T> resultList = Lists.newArrayList();
		if (list == null || list.isEmpty()) {
	        return resultList;
	    }
		for(int i=0; i<list.size() && i< top; i++) {
			resultList.add(list.get(i));
		}
	    return resultList;
	}
	public <T> List<T> findAll(final String sql) {
		return em.createQuery(sql).getResultList();
	}
	public <T> List<T> findAll(final String sql, Class<T> domain) {
		return em.createQuery(sql, domain).getResultList();
	}
	public <T> List<T> findAll(final String sql, Class<T> domain, final Object... params) {
		Query query = createQuery(sql, domain);
		setPatamsByPosition(query, params);
		return query.getResultList();
	}
	
	public long findAllCount(final String sql, final Object... params) {
		Query query = createQuery(sql);
		setPatamsByPosition(query, params);
		return (long) query.getFirstResult();
	}
	
	public long deleteAllCount(final String sql, final Object... params) {
		Query query = createQuery(sql);
		setPatamsByPosition(query, params);
		return (long) query.executeUpdate();
	}
	
	public void save(BaseDomain domain) {
		em.persist(domain);
	}
	public void saveWithFlush(BaseDomain domain) {
		em.persist(domain);
		em.flush();
	}
	public void merge(BaseDomain domain) {
		em.merge(domain);
	}
	
	/**
	 * all ? param should be replaced with real value before call this method.
	 * @param updateSql
	 */
	public void update(final String updateSql) {
		 em.createQuery(updateSql).executeUpdate();
	}
	/**
	 * update with params.
	 * @param updateSql
	 * @param params
	 */
	public void update(final String updateSql, final Object... params) {
		Query query = em.createQuery(updateSql);
		setPatamsByPosition(query, params);
		query.executeUpdate();
	}
	
	public int updateRecord(final String updateSql, final Object... params) {
		Query query = em.createQuery(updateSql);
		setPatamsByPosition(query, params);
		int result = query.executeUpdate();
		return result;
	}
	public void delete(final String deleteSql) {
		 em.createQuery(deleteSql).executeUpdate();
	}
	/**
	 * output is single row, custom output.
	 * don't explore this api internal use only.
	 * select t1.a from table t1, table t2, table t3 where ....
	 * @param customSql
	 * @return
	 */
	private Object getSingleCustomRawResult(final String customSql) {
		return em.createQuery(customSql).getSingleResult();
	}
	/**
	 * output is single row, custom multiple column output.
	 * select t1.a, t1.b, t2.c, t2,d, t3.z from table t1, table t2, table t3 where ....
	 * @param customSql
	 * @return
	 */
	public Object[] getSingleRowResult(final String customSql) {
		Object result = getSingleCustomRawResult(customSql);
		if(result == null) {
			return null;
		} else if (result instanceof Object[]) {
			return (Object[]) result;
		} else if(result instanceof Long ||
				result instanceof Double ||
				result instanceof String) {
			Object[] obj = new Object[1];
			obj[0] = result;
			return obj;
		} else {
			throw new RuntimeException(GlobalConstants.EXCEPTION.DATA_NOT_FOUND_ERROR);
		}
	}
	/**
	 * output is single row and single column output.
	 * select t1.a from table t1, table t2, table t3 where ....
	 * @param customSql
	 * @return
	 */
	public Object getSingleRowAndColumnResult(final String customSql) {
		Object[] result = getSingleRowResult(customSql);
		if(result == null) {
			return 0;
		} else if (result.length ==1) {
			return result[0];
		} else {
			throw new RuntimeException(GlobalConstants.EXCEPTION.DATA_NOT_FOUND_ERROR);
		}
	}
	public long count(final String customSql) {
		Object result = getSingleRowAndColumnResult(customSql);
		return (long) result;
	}
	/**
	 * internal use only.
	 * @param nativeSql
	 * @param params
	 * @return
	 */
	private <T> Query buildNativeQuery(final String nativeSql, Class<T> domain, final Object... params) {
		Query query = em.createNativeQuery(nativeSql, domain);
		if(params!=null) {
			int i = 1;
			for(Object param: params) {
				query.setParameter(i, param);
				i++;
			}
		}
		return query;
	}
	/**
	 * internal use only.
	 * @param nativeSql
	 * @param params
	 * @return
	 */
	private <T> Query buildNativeQuery(final String nativeSql, final Object... params) {
		Query query = em.createNativeQuery(nativeSql);
		if(params!=null) {
			int i = 1;
			for(Object param: params) {
				query.setParameter(i, param);
				i++;
			}
		}
		return query;
	}
	/**
	 * execute native query.
	 * select t1.a, t1.b, t2.c, t2,d, t3.z etc from table t1, table t2, table t3 where ....
	 * queryMapKey is DAO:sqlId in sqlMap.xml
	 * @param customSql
	 * @return
	 */
	public <T> List<T> nativeQueryForResultList(final String nativeSql, Class<T> domain, final Object... params) {
		Query query = buildNativeQuery(nativeSql, domain, params);
		List<T> result = query.getResultList();
		return result;
	}
	public <T> List<T> nativeQueryForNTopResultList(final String nativeSql, Class<T> domain, final int maxResult, final Object... params) {
		Query query = buildNativeQuery(nativeSql, domain, params);
		query.setMaxResults(maxResult);
		List<T> result = query.getResultList();
		return result;
	}	
	public <T> List<T> nativeQueryForResultList(final String nativeSql, final Object... params) {
		Query query = buildNativeQuery(nativeSql, params);
		List<T> result = query.getResultList();
		return result;
	}
	 
	/**
	 * execute native query for single result.
	 * @param nativeSql
	 * @param domain
	 * @param params
	 * @return
	 */
	public <T> T nativeQueryForSingleResult(final String nativeSql, Class<T> domain, final Object... params) {
		Query query = buildNativeQuery(nativeSql, domain, params);
		T result = (T) query.getSingleResult();
		return result;
	}
	
	
	/**
	 * execute native query for single result.
	 * @param nativeSql
	 * @param domain
	 * @param params
	 * @return
	 */
	public <T> T nativeQueryForEntity(final String nativeSql, Class<T> domain, final Object... params) {
		Query query = buildNativeQuery(nativeSql, domain, params);
		List<T> results = query.getResultList();
		if(results!=null) {
			if(results.size() == 1) {
				return  (T)results.get(0);
			} else if (results.size() > 1) {
				LOGGER.warn("More than one results: {}.", results.size());
				return  (T)results.get(0);
			}
		}
		return null;
	}
	/**
	 * execute native query for single column.
	 * @param nativeSql
	 * @param domain
	 * @param params
	 * @return
	 */
	public Object nativeQueryForSingleColumn(final String nativeSql, final Object... params) {
		Query query = buildNativeQuery(nativeSql, params);
		Object result = query.getSingleResult();
		return result;
	}	
	
	
	public Object nativeQueryForUpdate(final String nativeSql, final Object... params) {
		Query query = buildNativeQuery(nativeSql, params);
		Object result = query.executeUpdate();
		return result;
	}	
	
	public long count(final String nativeSql, final Object... params) {
		Object result = nativeQueryForSingleColumn(nativeSql, params);
		return Long.parseLong(result.toString());
	}
	public long navtivesqlCount(final String nativeSql) {
		Object result = nativeQueryForSingleColumn(nativeSql);
		return Long.parseLong(result.toString());
	}
	/**
	 * execute native query for single column.
	 * @param nativeSql
	 * @param domain
	 * @param params
	 * @return
	 */
	public Object nativeQueryForSingleColumnEntity(final String nativeSql, final Object... params) {
		Query query = buildNativeQuery(nativeSql, params);
		List<Object> results = query.getResultList();
		if(results!=null) {
			if(results.size() == 1) {
				return  (Object)results.get(0);
			} else if (results.size() > 1){
				LOGGER.warn("More than one results: {}.", results.size());
				return  (Object)results.get(0);
			}
		}
		return null;
	}
	public void flush() {
		em.flush();
	}
	protected void setPatamsByPosition(final Query query, final Object... params) {
		if(params!=null) {
			int posistion =1;
			for(Object param: params) {
				query.setParameter(posistion, param);
				posistion++;
			}
		}
	}
	
}

