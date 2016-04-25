/**
 *
 */
package com.rayeye.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rayeye.dbutils.handlers.BeanHandler;
import com.rayeye.dbutils.handlers.BeanListHandler;

/**
 * An EntityExecutor that is used to update entities.
 */
public class QueryEntityExecutor<T> extends
		AbstractEntityExecutor<QueryEntityExecutor<T>> {

	private static final Logger LOG = LoggerFactory
			.getLogger(QueryEntityExecutor.class);
	private Set<String> excludeColumns = null;
	private Set<String> includeColumns = null;

	/**
	 * Constructor.
	 * 
	 * @param entityInstance
	 *            the entity instance to update.
	 * @param conn
	 *            the connection to use.
	 */
	QueryEntityExecutor(Class<T> entityName, final Connection conn) {
		super(entityName, conn);
	}

	public QueryEntityExecutor<T> exclude(String... excludeColumns) {
		this.exclude(new HashSet<String>(Arrays.asList(excludeColumns)));
		return this;
	}

	public QueryEntityExecutor<T> exclude(Set<String> excludeColumns) {
		this.excludeColumns = excludeColumns;
		return this;
	}

	public QueryEntityExecutor<T> include(String... includeColumns) {
		this.include(new HashSet<String>(Arrays.asList(includeColumns)));
		return this;
	}

	public QueryEntityExecutor<T> include(Set<String> includeColumns) {
		this.includeColumns = includeColumns;
		return this;
	}

	/**
	 * 相等 =
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryEntityExecutor<T> eq(String key, Object value) {
		this.bind(key, value);
		return this;
	}

	public QueryEntityExecutor<T> bindMap(Map<String, Object> params)
			throws SQLException {
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				this.bind(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public List<T> list() throws SQLException {
		// execute using the BeanHandler
		QueryExecutor exec = buildQueryExecutor();
		return (List<T>) exec.execute(new BeanListHandler((Class<T>) entity));
	}

	public T uniqueResult() throws SQLException {
		QueryExecutor exec = buildQueryExecutor();
		return exec.execute(new BeanHandler<T>((Class<T>) entity));
	}

	private QueryExecutor buildQueryExecutor() throws SQLException {

		Map<String, String> columns = getColumns();

		StringBuilder sb = new StringBuilder("select ");

		sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), null));

		sb.append(" from ").append(tableName);

		if (!params.isEmpty()) {
			sb.append(" where ");
			sb.append(EntityUtils.joinColumnsEquals(params.keySet(), " and "));
		}

		LOG.debug("SELECT: {}", sb.toString());

		// setup the QueryExecutor
		final QueryExecutor exec = new QueryExecutor(conn, sb.toString(), true);

		// go through and bind all the params
		for (Map.Entry<String, Object> entity : params.entrySet()) {

			exec.bind(entity.getKey(), entity.getValue());
		}

		return exec;

	}

	private Map<String, String> getColumns() {

		Map<String, String> columns = EntityUtils.getAllColumnNames(entity);
		if (includeColumns != null) {
			final Map<String, String> newColumns = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : columns.entrySet()) {
				if (includeColumns.contains(entry.getValue())) {
					newColumns.put(entry.getKey(), entry.getValue());
				}
			}
			return newColumns;
		} else if (excludeColumns != null) {
			Map<String, String> newColumns = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : columns.entrySet()) {
				if (!excludeColumns.contains(entry.getValue())) {
					newColumns.put(entry.getKey(), entry.getValue());
				}
			}
			return newColumns;
		}

		return columns;
	}
}
