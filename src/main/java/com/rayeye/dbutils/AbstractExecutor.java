/*
 * Copyright (C) 2014 SOP4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rayeye.dbutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class for executing a query, insert, update, or batch.
 *
 * @since 2.0
 */
abstract class AbstractExecutor<T extends AbstractExecutor<T>> {

    private static final String COLON = ":";  // TODO: change this to any character
    private static final Pattern PARAM_PATTERN = Pattern.compile("(:\\w+)");

    private final Connection conn;
    private final String sql;
    private final PreparedStatement stmt;

    private final Map<String, List<Integer>> paramPosMap;
    private final Map<String, Object> paramValueMap;
    private int currentPosition = 0;

    public AbstractExecutor(final Connection conn, final String sql) throws SQLException {
        this.conn = conn;
        this.sql = sql;
        this.paramPosMap = new HashMap<String, List<Integer>>();
        this.paramValueMap = new HashMap<String, Object>();

        final Matcher matcher = PARAM_PATTERN.matcher(sql);

        // go through finding params
        while (matcher.find()) {
            insertParamPosition(matcher.group().replace(COLON, ""));
        }

        // replace all of the :names with ?, and create a prepared statement
        stmt = conn.prepareStatement(sql.replaceAll(":\\w+", "\\?"));
    }

    /**
     * Helper method to insert params and the current position into the map.
     * @param param the SQL param.
     */
    private void insertParamPosition(final String param) {
        List<Integer> posList = paramPosMap.get(param);

        // create a new list if we need to
        if (posList == null) {
            posList = new ArrayList<Integer>();
            paramPosMap.put(param, posList);
        }

        // increment first, so we match SQL numbering
        posList.add(Integer.valueOf(++currentPosition));
    }

    /**
     * Gets the SQL statement that was passed into the constructor.
     *
     * @return the SQL statement passed into the constructor.
     */
    String getSql() {
        return sql;
    }

    /**
     * Returns the underlying prepared statement.
     *
     * @return the underlying prepared statement.
     */
    PreparedStatement getStatement() {
        return stmt;
    }

    /**
     * Returns the underlying connection.
     *
     * @return the underlying connection.
     */
    Connection getConnection() {
        return conn;
    }

    /**
     * Throws an exception if there are unmapped params.
     *
     * @throws SQLException if there are unmapped params.
     */
    void throwIfUnmappedParams() throws SQLException {
        // if the sizes are the same, then we've filled all the parameters
        if(paramValueMap.size() == paramPosMap.size()) {
            return;
        }

        final StringBuilder sb = new StringBuilder("There are unbound parameters: ");
        final Set<String> unboundParams = paramPosMap.keySet();

        // compute the set difference
        unboundParams.removeAll(paramValueMap.keySet());

        for (String param:unboundParams) {
            sb.append(param);
            sb.append(", ");
        }

        // remove the last comma
        sb.delete(sb.length() - 2, sb.length());

        // throw our exception
        throw new SQLException(sb.toString());
    }

    /**
     * Binds a named parameter to a value.
     *
     * @param name the name of the parameter in the SQL statement.
     * @param value the value of the parameter in the SQL statement.
     * @return this execution object to provide the fluent style.
     * @throws SQLException thrown if the parameter is not found, already bound, or there is an issue binding it.
     */
    public T bind(String name, final Object value) throws SQLException {
        name = name.replace(COLON, ""); // so we can take ":name" or "name"

        final List<Integer> pos = paramPosMap.get(name);

        if (pos == null) {
            throw new SQLException(name + " is not found in the SQL statement: " + getSql());
        }

        // make sure it isn't already bound
        if(paramValueMap.containsKey(name)) {
            throw new SQLException("You are attempting to bind the parameter " + name + " twice. It already has the value " + paramValueMap.get(name));
        }

        // go through and bind all of the positions for this name
        for (Integer p:pos) {
            stmt.setObject(p.intValue(), value);
        }

        // add the param and value to our map
        paramValueMap.put(name, value);

        // suppressed because the casting will always work here
        @SuppressWarnings("unchecked")
        final T ret = (T) this;

        return ret;
    }
    
    public T bind(int pos , Object value) throws SQLException {
       
        stmt.setObject( pos, value);
        
        // suppressed because the casting will always work here
        @SuppressWarnings("unchecked")
        final T ret = (T) this;

        return ret;
    }

    /**
     * Binds null to a parameter.
     * Types.VARCHAR is used as the type's parameter.
     * This usually works, but fails with some Oracle and MS SQL drivers.
     *
     * @param name the name of the parameter.
     * @return this execution object to provide the fluent style.
     * @throws SQLException throw if the parameter is not found, already bound, or there is an issue binding null.
     */
    public T bindNull(final String name) throws SQLException {
        return bindNull(name, Types.VARCHAR);
    }

    /**
     * Binds null to a parameter, specifying the parameter's type.
     *
     * @param name the name of the parameter.
     * @param sqlType the type of the parameter.
     * @return this execution object to provide the fluent style.
     * @throws SQLException throw if the parameter is not found, already bound, or there is an issue binding null.
     */
    public T bindNull(String name, final int sqlType) throws SQLException {
        name = name.replace(COLON, ""); // so we can take ":name" or "name"

        final List<Integer> pos = paramPosMap.get(name);

        if (pos == null) {
            throw new SQLException(name + " is not found in the SQL statement");
        }

        // go through and bind all of the positions for this name
        for (Integer p:pos) {
            stmt.setNull(p.intValue(), sqlType);
        }

        // add the param and value to our map
        paramValueMap.put(name, null);

        // suppressed because the casting will always work here
        @SuppressWarnings("unchecked")
        final T ret = (T) this;

        return ret;
    }

    /**
     * Used for batch calls so we can clear the map after the addBatch call.
     */
    void clearValueMap() {
        paramValueMap.clear();
    }

    /**
     * Throws a new exception with a more informative error message.
     *
     * @param cause The original exception that will be chained to the new
     *              exception when it's rethrown.
     *
     * @throws SQLException if a database access error occurs
     */
    void rethrow(SQLException cause) throws SQLException {
        String causeMessage = cause.getMessage();

        if (causeMessage == null) {
            causeMessage = "";
        }

        final StringBuilder msg = new StringBuilder(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        // loop through adding the parameter to value mappings
        for (Map.Entry<String, Object> param:paramValueMap.entrySet()) {
            msg.append(param.getKey());
            msg.append("=");
            msg.append(param.getValue());
            msg.append(" ");
        }

        final SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    /**
     * Wrap the <code>ResultSet</code> in a decorator before processing it. This
     * implementation returns the <code>ResultSet</code> it is given without any
     * decoration.
     *
     * @param rs The <code>ResultSet</code> to decorate; never <code>null</code>.
     * @return The <code>ResultSet</code> wrapped in some decorator.
     */
    ResultSet wrap(ResultSet rs) {
        return rs;
    }

    /**
     * Close a <code>Connection</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     */
    void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Close a <code>Statement</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param stmt Statement to close
     * @throws SQLException if a database access error occurs
     */
    void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Close a <code>ResultSet</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param rs ResultSet to close
     * @throws SQLException if a database access error occurs
     */
    void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }

}
