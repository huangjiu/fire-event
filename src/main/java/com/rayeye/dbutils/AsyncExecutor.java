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

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Convenience class for executing QueryExecutor, InsertExecutor, or UpdateExecutors asynchronously.
 *
 * @since 2.0
 */
public class AsyncExecutor {

    private final ExecutorService executorService;

    /**
     * Constructor for AsyncQueryRunner which uses a provided ExecutorService and underlying QueryRunner.
     *
     * @param executorService the {@code ExecutorService} instance used to run JDBC invocations concurrently.
     */
    public AsyncExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Execute a {@link BatchExecutor}.
     *
     * @param executor The executor for this SQL statement.
     * @return A <code>Future</code> which returns the result of the batch call.
     * @throws SQLException if a database access error occurs
     */
    public Future<int[]> execute(final BatchExecutor executor) throws SQLException {
        return executorService.submit(new Callable<int[]>() {

            @Override
            public int[] call() throws Exception {
                return executor.execute();
            }

        });
    }

    /**
     * Execute a {@link QueryExecutor} given a handler.
     *
     * @param <T> The type of object that the handler returns
     * @param executor The executor for this SQL statement.
     * @param handler The handler that converts the results into an object.
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public <T> Future<T> execute(final QueryExecutor executor, final ResultSetHandler<T> handler) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return executor.execute(handler);
            }

        });
    }

    /**
     * Execute an {@link UpdateExecutor}.
     *
     * @param executor The executor for this SQL statement.
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public Future<Integer> execute(final UpdateExecutor executor) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return Integer.valueOf(executor.execute());
            }

        });
    }

    /**
     * Execute an {@link InsertExecutor} given a handler.
     *
     * @param <T> The type of object that the handler returns
     * @param executor The executor for this SQL statement.
     * @param handler The handler that converts the results into an object.
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public <T> Future<T> execute(final InsertExecutor executor, final ResultSetHandler<T> handler) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return executor.execute(handler);
            }

        });
    }

    /**
     * Execute an {@link InsertExecutor} given a handler.
     *
     * @param executor The executor for this SQL statement.
     * @return A <code>Future</code> which returns the number of rows inserted.
     * @throws SQLException if a database access error occurs
     */
    public Future<Integer> execute(final InsertExecutor executor) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return Integer.valueOf(executor.execute());
            }

        });
    }

}
