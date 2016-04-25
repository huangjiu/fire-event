/**
 *
 */
package com.rayeye.dbutils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * An EntityExecutor that is used to update entities.
 */
public class InsertEntityExecutor<T> extends
		AbstractEntityExecutor<InsertEntityExecutor<T>> {
	private static final Logger LOG = LoggerFactory
			.getLogger(InsertEntityExecutor.class);

	private final T entityInstance;
	private Set<String> excludeColumns;
	private Set<String> includeColumns;

	/**
	 * Constructor.
	 * 
	 * @param entityInstance
	 *            the entity instance to update.
	 * @param conn
	 *            the connection to use.
	 */
	InsertEntityExecutor(final T entityInstance, final Class<T> entityClass, final Connection conn) {
		this(entityInstance, entityClass , conn, null);
	}

	/**
	 * Constructor that takes a list of columns to exclude during the bind.
	 * 
	 * @param entityInstance
	 *            the entity instance to update.
	 * @param conn
	 *            the connection to use.
	 * @param excludeColumns
	 *            the columns to exclude during the bind.
	 */
	InsertEntityExecutor(T entityInstance , Class<T> entityClass, Connection conn, Set<String> excludeColumns) {
		super(entityClass, conn);
		this.entityInstance = entityInstance;
		this.excludeColumns = excludeColumns;
	}

	@Override
	protected Object getEntity() {
		return entityInstance;
	}
	
	public InsertEntityExecutor<T> setIncludeColumns(String... includeColumns) {
		this.setIncludeColumns(new HashSet<String>(Arrays.asList(includeColumns)));
		return this;
	}

	public InsertEntityExecutor<T> setIncludeColumns(Set<String> includeColumns) {
		this.includeColumns = includeColumns;
		return this;
	}

	/**
	 * Updates an entity(ies) in the database.
	 * 
	 * @return the number of rows updated.
	 * @throws SQLException
	 *             thrown if any errors occur during updating.
	 */
	public int insert() throws SQLException {
		Map<String, String> columns = EntityUtils.getColumnNames(entity);
		
		if( includeColumns != null ) {
			Map<String, String> newColumns = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : columns.entrySet()) {
				if (includeColumns.contains(entry.getValue())) {
					newColumns.put(entry.getKey(), entry.getValue());
				}
			}
			return insert(newColumns);
		} else if( excludeColumns != null ) {
			Map<String, String> newColumns = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : columns.entrySet()) {
				if (!excludeColumns.contains(entry.getValue())) {
					newColumns.put(entry.getKey(), entry.getValue());
				}
			}
			return insert(newColumns);
		}
		
		return insert(columns);
	}
	
	private int insert(Map<String, String> columns) throws SQLException{
		
		final String tableName = EntityUtils.getTableName(entity);
		
		final StringBuilder sb = new StringBuilder("insert into ");

		// create the SQL command
		sb.append(tableName);
		sb.append(" (");
		sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), null));
		sb.append(") values(");
		sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), ":"));
		sb.append(")");

		LOG.debug("INSERT: {}", sb.toString());

		// create the executor
		final InsertExecutor exec = new InsertExecutor(conn, sb.toString(), true);

		for (String column : columns.keySet()) {
			// don't bind the exclude columns
			if (excludeColumns != null && excludeColumns.contains(column)) {
				continue;
			}

			try {
				// bind all of the values
				final Object value = PropertyUtils.getSimpleProperty(
						entityInstance, columns.get(column));

				if (value == null) {
					exec.bindNull(column);
				} else {
					exec.bind(column, value);
				}
			} catch (final IllegalAccessException e) {
				throw new SQLException(e);
			} catch (final InvocationTargetException e) {
				throw new SQLException(e);
			} catch (final NoSuchMethodException e) {
				throw new SQLException(e);
			}
		}

		return exec.execute();
	}

//	private int includeColumnsExecute() throws SQLException {
//
//		final Map<String, String> columns = EntityUtils.getColumnNames(entity);
//		final Map<String, String> newColumns = new HashMap<String, String>();
//		for (Map.Entry<String, String> entry : columns.entrySet()) {
//			if (includeColumns.contains(entry.getValue())) {
//				newColumns.put(entry.getKey(), entry.getValue());
//			}
//		}
//
//		final StringBuilder sb = new StringBuilder("insert into ");
//
//		// create the SQL command
//		sb.append(tableName);
//		sb.append(" (");
//		sb.append(EntityUtils.joinColumnsWithComma(newColumns.keySet(), null));
//		sb.append(") values(");
//		sb.append(EntityUtils.joinColumnsWithComma(newColumns.keySet(), ":"));
//		sb.append(")");
//
//		LOG.debug("INSERT: {}", sb.toString());
//
//		// create the executor
//		final InsertExecutor exec = new InsertExecutor(conn, sb.toString() , true);
//
//		for (String column : newColumns.keySet()) {
//			// don't bind the exclude columns
//			if (excludeColumns.contains(column)) {
//				continue;
//			}
//
//			try {
//				// bind all of the values
//				final Object value = PropertyUtils.getSimpleProperty( entityInstance, newColumns.get(column));
//
//				if (value == null) {
//					exec.bindNull(column);
//				} else {
//					exec.bind(column, value);
//				}
//			} catch (final IllegalAccessException e) {
//				throw new SQLException(e);
//			} catch (final InvocationTargetException e) {
//				throw new SQLException(e);
//			} catch (final NoSuchMethodException e) {
//				throw new SQLException(e);
//			}
//		}
//
//		return exec.execute();
//	}
//
//	private int excludeColumnsExecute() throws SQLException {
//
//		final String tableName = EntityUtils.getTableName(entity);
//		final Map<String, String> columns = EntityUtils.getColumnNames(entity);
//
//		final StringBuilder sb = new StringBuilder("insert into ");
//
//		// create the SQL command
//		sb.append(tableName);
//		sb.append(" (");
//		sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), null));
//		sb.append(") values(");
//		sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), ":"));
//		sb.append(")");
//
//		LOG.debug("INSERT: {}", sb.toString());
//
//		// create the executor
//		final InsertExecutor exec = new InsertExecutor(conn, sb.toString(),
//				true);
//
//		for (String column : columns.keySet()) {
//			// don't bind the exclude columns
//			if (excludeColumns.contains(column)) {
//				continue;
//			}
//
//			try {
//				// bind all of the values
//				final Object value = PropertyUtils.getSimpleProperty(
//						entityInstance, columns.get(column));
//
//				if (value == null) {
//					exec.bindNull(column);
//				} else {
//					exec.bind(column, value);
//				}
//			} catch (final IllegalAccessException e) {
//				throw new SQLException(e);
//			} catch (final InvocationTargetException e) {
//				throw new SQLException(e);
//			} catch (final NoSuchMethodException e) {
//				throw new SQLException(e);
//			}
//		}
//
//		return exec.execute();
//	}
}
