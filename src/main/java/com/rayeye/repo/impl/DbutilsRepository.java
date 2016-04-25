package com.rayeye.repo.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.rayeye.dbutils.EntityUtils;
import com.rayeye.dbutils.QueryExecutor;
import com.rayeye.dbutils.QueryRunner;
import com.rayeye.dbutils.handlers.BeanHandler;
import com.rayeye.dbutils.handlers.BeanListHandler;
import com.rayeye.repo.Repository;
import com.rayeye.repo.RepositoryException;

public abstract class DbutilsRepository<T> implements Repository<T> {

	private Class<T> entityClass;
	
	private QueryRunner db;
	
	private RepositoryException newException(String message){
		return new RepositoryException(message);
	}
	
	private Object getPropertyValue(T entity , String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		return PropertyUtils.getProperty(entity , name);
	}
	
	public QueryRunner getDb() {
		return db;
	}

	public void setDb(QueryRunner db) {
		this.db = db;
	}

	@Override
	public Serializable insert(T entity) {
		try {
			getDb().insert(getEntityClass(), entity).insert();
			return null;
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}
	
	@Override
	public void insertAttrs(T entity, String... includeAttrs) {
		try {
			getDb().insert(getEntityClass(), entity).setIncludeColumns(includeAttrs).insert();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}
	
	@Override
	public void insertExcludeAttrs(T entity, String... excludeAttrs) {
		try {
			getDb().insert(getEntityClass(), entity , Arrays.asList(excludeAttrs)).insert();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public void update(T entity) {
		try {
			getDb().update(entity).bindId().update(getEntityClass());
		} catch (Exception e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public void updateAttrs(T entity, String... includeAttrs) {
		try {
			getDb().update(entity)
				.setIncludeColumns(includeAttrs)
				.bindId()
				.update( getEntityClass());
		} catch (Exception e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public void updateExcludeAttrs(T entity, String... excludeAttrs) {
		try {
			getDb().update(entity, Arrays.asList(excludeAttrs))
				.bindId()
				.update( getEntityClass());
		} catch (Exception e) {
			throw newException(e.getMessage());
		}
	}
	

	@Override
	public void remove(T entity) {
		try {
			getDb().delete(getEntityClass()).bindEntity(entity).bindId().delete();
		} catch (Exception e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public void removeById(Serializable pk) {
		try {
			String id = EntityUtils.getIdColumnName(getEntityClass());
			getDb().delete(getEntityClass()).bind(id , pk ).delete();
		} catch (Exception e) {
			throw newException(e.getMessage());
		}
	}
	
	@Override
	public void removeByAttrs(T entity, String... attrs) {
		try {
			getDb().delete(getEntityClass()).bindEntity(entity).bind(Arrays.asList(attrs)).delete();
		} catch (Exception e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public T findById(Serializable pk) {
		try {
			String id = EntityUtils.getIdColumnName(getEntityClass());
			return getDb().query(getEntityClass()).bind(id , pk).uniqueResult();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}
	
	@Override
	public T findByIdIncludeAttrs(Serializable pk, String... includeAttrs) {
		try {
			String id = EntityUtils.getIdColumnName(getEntityClass());
			return getDb().query(getEntityClass()).include(includeAttrs).bind(id , pk).uniqueResult();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public T findByIdExcludeAttrs(Serializable pk, String... excludeAttrs) {
		try {
			String id = EntityUtils.getIdColumnName(getEntityClass());
			return getDb().query(getEntityClass())
				.exclude(excludeAttrs)
				.bind(id , pk)
				.uniqueResult();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> find(Map<String, Object> params) {
		try {
			return getDb().query(getEntityClass()).bindMap(params).list();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> findIncludeAttrs(Map<String, Object> params,
			String... includeAttrs) {
		try {
			return getDb().query(getEntityClass()).bindMap(params).include(includeAttrs).list();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> findExcludeAttrs(Map<String, Object> params,
			String... excludeAttrs) {
		try {
			return getDb().query(getEntityClass()).bindMap(params).exclude(excludeAttrs).list();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> findAll() {
		try {
			return getDb().query(getEntityClass()).list();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}
	
	@Override
	public List<T> findAllincludeAttrs(String... includeAttrs) {
		try {
			return getDb().query(getEntityClass()).include(includeAttrs).list();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> findAllExcludeAttrs(String... excludeAttrs) {
		try {
			return getDb().query(getEntityClass()).exclude(excludeAttrs).list();
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> findSql(String sql, Object... params) {
		try {
			return getDb().query(sql)
				.bindArray(params)
				.execute(new BeanListHandler<T>( getEntityClass()));
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public List<T> findSqlForMap(String sql, Map<String, Object> params) {
		try {
			return getDb().query(sql)
				.bindMap(params)
				.execute(new BeanListHandler<T>( getEntityClass()));
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public T findUniqueResult(String sql, Object... params) {
		try {
			return getDb().query(sql)
				.bindArray(params)
				.execute(new BeanHandler<T>( getEntityClass()));
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	@Override
	public T findUniqueResult(String sql, Map<String, Object> params) {
		try {
			return getDb().query(sql)
				.bindMap(params)
				.execute(new BeanHandler<T>( getEntityClass()));
		} catch (SQLException e) {
			throw newException(e.getMessage());
		}
	}

	public Class<T> getEntityClass() {
		if (entityClass == null) {
			Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
			entityClass = (Class<T>) types[0];
		}
		return entityClass;
	}

}
