package com.rayeye.repo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Repository<T> {

	Serializable insert(T entity);
	
	void insertAttrs(T entity , String ... includeAttrs);
	
	void insertExcludeAttrs(T entity , String ... excludeAttrs);
	
	void update(T entity);
	
	void updateAttrs(T entity , String ... includeAttrs);
	
	void updateExcludeAttrs(T entity , String ... excludeAttrs);

	void remove(T entity);
	
	void removeById(java.io.Serializable pk);
	
	void removeByAttrs(T entity , String ... attrs );
	
	T findById(java.io.Serializable pk);
	
	T findByIdIncludeAttrs(java.io.Serializable pk , String ... includeAttrs);
	
	T findByIdExcludeAttrs(java.io.Serializable pk , String ... excludeAttrs);
	
	List<T> find(Map<String , Object> params);
	
	List<T> findIncludeAttrs(Map<String , Object> params , String ... includeAttrs);
	
	List<T> findExcludeAttrs(Map<String , Object> params , String ... excludeAttrs);
	
	List<T> findAll();
	
	List<T> findAllincludeAttrs(String ... includeAttrs);
	
	List<T> findAllExcludeAttrs(String ... excludeAttrs);
	
	List<T> findSql(String sql , Object ...params);
	
	List<T> findSqlForMap(String sql , Map<String , Object> params);
	
	T findUniqueResult(String sql , Object ...params);
	
	T findUniqueResult(String sql , Map<String , Object> params);
}