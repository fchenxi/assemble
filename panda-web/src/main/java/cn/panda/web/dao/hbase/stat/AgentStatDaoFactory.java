/*
 * Copyright 2016 Naver Corp.
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

package cn.panda.web.dao.hbase.stat;

import cn.panda.common.hbase.HBaseAdminTemplate;
import cn.panda.common.hbase.HBaseTables;
import cn.panda.common.server.bo.stat.ActiveTraceBo;
import cn.panda.common.server.bo.stat.AgentStatDataPoint;
import cn.panda.web.dao.hbase.stat.compatibility.HbaseAgentStatDualReadDao;
import org.apache.hadoop.hbase.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * @author HyunGil Jeong
 */
abstract class AgentStatDaoFactory<T extends AgentStatDataPoint, D extends AgentStatDao<T>> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected D v1;
    protected D v2;

    @Autowired
    private HBaseAdminTemplate adminTemplate;

//    @Value("#{pinpointWebProps['web.stat.format.compatibility.version'] ?: 'v2'}")
    private String mode = "v2";

    D getDao() throws Exception {
        logger.info("AgentStatDao Compatibility {}", mode);

        final TableName v1TableName = HBaseTables.AGENT_STAT;
        final TableName v2TableName = HBaseTables.AGENT_STAT_VER2;

        if (mode.equalsIgnoreCase("v1")) {
            if (this.adminTemplate.tableExists(v1TableName)) {
                return v1;
            } else {
                logger.error("AgentStatDao configured for v1, but {} table does not exist", v1TableName);
                throw new IllegalStateException(v1TableName + " table does not exist");
            }
        } else if (mode.equalsIgnoreCase("v2")) {
            if (this.adminTemplate.tableExists(v2TableName)) {
                return v2;
            } else {
                logger.error("AgentStatDao configured for v2, but {} table does not exist", v2TableName);
                throw new IllegalStateException(v2TableName + " table does not exist");
            }
        } else if (mode.equalsIgnoreCase("compatibilityMode")) {
            boolean v1TableExists = this.adminTemplate.tableExists(v1TableName);
            boolean v2TableExists = this.adminTemplate.tableExists(v2TableName);
            if (v1TableExists && v2TableExists) {
                return getCompatibilityDao(this.v1, this.v2);
            } else {
                logger.error("AgentStatDao configured for compatibilityMode, but {} and {} tables do not exist", v1TableName, v2TableName);
                throw new IllegalStateException(v1TableName + ", " + v2TableName + " tables do not exist");
            }
        } else {
            throw new IllegalStateException("Unknown AgentStatDao configuration : " + mode);
        }
    }

    abstract D getCompatibilityDao(D v1, D v2);

    @Repository("activeTraceDaoFactory")
    public static class ActiveTraceDaoFactory extends AgentStatDaoFactory<ActiveTraceBo, ActiveTraceDao> implements FactoryBean<ActiveTraceDao> {

        @Autowired
        public void setV1(@Qualifier("activeTraceDaoV1") ActiveTraceDao v1) {
            this.v1 = v1;
        }

        @Autowired
        public void setV2(@Qualifier("activeTraceDaoV2") ActiveTraceDao v2) {
            this.v2 = v2;
        }

        @Override
        public ActiveTraceDao getObject() throws Exception {
            return super.getDao();
        }

        @Override
        public Class<?> getObjectType() {
            return ActiveTraceDao.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        ActiveTraceDao getCompatibilityDao(ActiveTraceDao v1, ActiveTraceDao v2) {
            return new HbaseAgentStatDualReadDao.ActiveTraceDualReadDao(v2, v1);
        }
    }
}
