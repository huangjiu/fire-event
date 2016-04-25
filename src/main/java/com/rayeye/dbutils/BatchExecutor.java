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
import java.sql.SQLException;

/**
 * This class provides the ability to execute a batch of statements.
 *
 * It is really just a facade to an array of UpdateExecutors.
 *
 * @since 2.0
 */
public class BatchExecutor extends AbstractExecutor<BatchExecutor> {

    private final boolean closeConn;
    private boolean addBatchCalled = false;

    /**
     * Constructs a BatchExecutor given a connection and SQL statement.
     *
     * @param conn The connection to use during execution.
     * @param sql The SQL statement.
     * @param closeConnection If the connection should be closed or not.
     * @throws SQLException thrown if there is an error during execution.
     */
    BatchExecutor(final Connection conn, final String sql, final boolean closeConnection) throws SQLException {
        super(conn, sql);
        this.closeConn = closeConnection;
    }

    /**
     * Returns the close connection flag.
     * @return close connection flag.
     */
    boolean getCloseConn() {
        return closeConn;
    }

    /**
     * Adds the statement to the batch after binding all of the parameters.
     *
     * @return this object.
     * @throws SQLException if a SQLException is thrown during the addBatch() call.
     * @see java.sql.PreparedStatement#addBatch()
     */
    public BatchExecutor addBatch() throws SQLException {
        // throw an exception if there are unmapped parameters
        this.throwIfUnmappedParams();

        addBatchCalled = true;
        
        try {
            getStatement().addBatch();
            clearValueMap();
        } catch (SQLException e) {
            rethrow(e);
        }

        return this;
    }

    /**
     * Calls batch after checking the parameters to ensure nothing is null.
     *
     * @return an array containing the number of rows updated for each statement.
     * @throws SQLException If there are database or parameter errors.
     * @see com.rayeye.dbutils.UpdateExecutor#execute()
     */
    public int[] execute() throws SQLException {
        if(!addBatchCalled) {
            throw new SQLException("addBatch must be called before execute.");
        }

        try {
            return getStatement().executeBatch();
        } catch (SQLException e) {
            rethrow(e);
        } finally {
            close(getStatement());
            if (closeConn) {
                close(getConnection());
            }
        }

        // we get here only if something is thrown
        return null;
    }

}
