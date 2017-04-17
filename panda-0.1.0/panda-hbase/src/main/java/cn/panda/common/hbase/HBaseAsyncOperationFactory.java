/*
 * Copyright 2016 NAVER Corp.
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
 *
 */

package cn.panda.common.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author Taejin Koo
 */
public class HBaseAsyncOperationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseAsyncOperationFactory.class);

    public static final String ENABLE_ASYNC_METHOD = "hbase.client.async.enable";
    public static final boolean DEFAULT_ENABLE_ASYNC_METHOD = false;

    public static final String ASYNC_IN_QUEUE_SIZE = "hbase.client.async.in.queuesize";
    public static final int DEFAULT_ASYNC_IN_QUEUE_SIZE = 10000;

    public static final String ASYNC_PERIODIC_FLUSH_TIME = "hbase.tablemultiplexer.flush.period.ms";
    public static final int DEFAULT_ASYNC_PERIODIC_FLUSH_TIME = 100;

    public static final String ASYNC_RETRY_COUNT = "hbase.client.max.retries.in.queue";
    public static final int DEFAULT_ASYNC_RETRY_COUNT = 10;

    public static HBaseAsyncOperation create(Configuration configuration) throws IOException {
        boolean enableAsyncMethod = configuration.getBoolean(ENABLE_ASYNC_METHOD, DEFAULT_ENABLE_ASYNC_METHOD);
        if (!enableAsyncMethod) {
            return DisabledHBaseAsyncOperation.INSTANCE;
        }

        int queueSize = configuration.getInt(ASYNC_IN_QUEUE_SIZE, DEFAULT_ASYNC_IN_QUEUE_SIZE);

        if (configuration.get(ASYNC_PERIODIC_FLUSH_TIME, null) == null) {
            configuration.setInt(ASYNC_PERIODIC_FLUSH_TIME, DEFAULT_ASYNC_PERIODIC_FLUSH_TIME);
        }

        if (configuration.get(ASYNC_RETRY_COUNT, null) == null) {
            configuration.setInt(ASYNC_RETRY_COUNT, DEFAULT_ASYNC_RETRY_COUNT);
        }

        return new HBaseAsyncTemplate(configuration, queueSize);
    }

    public static HBaseAsyncOperation create(Connection connection, Configuration configuration) throws IOException {
        boolean enableAsyncMethod = configuration.getBoolean(ENABLE_ASYNC_METHOD, DEFAULT_ENABLE_ASYNC_METHOD);
        if (!enableAsyncMethod) {
            return DisabledHBaseAsyncOperation.INSTANCE;
        }

        int queueSize = configuration.getInt(ASYNC_IN_QUEUE_SIZE, DEFAULT_ASYNC_IN_QUEUE_SIZE);

        if (configuration.get(ASYNC_PERIODIC_FLUSH_TIME, null) == null) {
            configuration.setInt(ASYNC_PERIODIC_FLUSH_TIME, DEFAULT_ASYNC_PERIODIC_FLUSH_TIME);
        }

        if (configuration.get(ASYNC_RETRY_COUNT, null) == null) {
            configuration.setInt(ASYNC_RETRY_COUNT, DEFAULT_ASYNC_RETRY_COUNT);
        }

        return new HBaseAsyncTemplate(connection, configuration, queueSize);
    }

}