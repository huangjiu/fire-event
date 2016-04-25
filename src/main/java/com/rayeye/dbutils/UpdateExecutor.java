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
 * Fluent class for executing updates.
 *
 * @since 2.0
 */
public class UpdateExecutor extends AbstractExecutor<UpdateExecutor> {

    private final boolean closeConn;

    /**
     * Constructs an UpdateExecutor given a connection and SQL statement.
     *
     * @param conn The connection to use during execution.
     * @param sql The SQL statement.
     * @param closeConnection If the connection should be closed or not.
     * @throws SQLException thrown if there is an error during execution.
     */
    UpdateExecutor(final Connection conn, final String sql, final boolean closeConnection) throws SQLException {
        super(conn, sql);
        this.closeConn = closeConnection;
    }

    /**
     * Calls update after checking the parameters to ensure nothing is null.
     *
     * @return The number of rows updated.
     * @throws SQLException If there are database or parameter errors.
     */
    public int execute() throws SQLException {
        // throw an exception if there are unmapped parameters
        this.throwIfUnmappedParams();

        try {
            return getStatement().executeUpdate();
        } catch (SQLException e) {
            this.rethrow(e);

        } finally {
            close(getStatement());
            if (closeConn) {
                close(getConnection());
            }
        }

        // we get here only if something is thrown
        return 0;
    }

}
