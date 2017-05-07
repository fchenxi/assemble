/*
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.panda.collector.service;

import cn.panda.web.dao.hbase.stat.collector.AgentStatDaoV2;
import cn.panda.common.server.bo.stat.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author minwoo.jung
 */
@Service("hBaseAgentStatService")
public class HBaseAgentStatService implements AgentStatService {

    private final Logger logger = LoggerFactory.getLogger(HBaseAgentStatService.class.getName());

    @Autowired
    private AgentStatDaoV2<JvmGcBo> jvmGcDao;

    @Autowired
    private AgentStatDaoV2<JvmGcDetailedBo> jvmGcDetailedDao;

    @Autowired
    private AgentStatDaoV2<CpuLoadBo> cpuLoadDao;

    @Autowired
    private AgentStatDaoV2<TransactionBo> transactionDao;

    @Autowired
    private AgentStatDaoV2<ActiveTraceBo> activeTraceDao;


    @Override
    public void save(AgentStatBo agentStatBo) {
        final String agentId = agentStatBo.getAgentId();
        try {
            this.jvmGcDao.insert(agentId, agentStatBo.getJvmGcBos());
            this.jvmGcDetailedDao.insert(agentId, agentStatBo.getJvmGcDetailedBos());
            this.cpuLoadDao.insert(agentId, agentStatBo.getCpuLoadBos());
            this.transactionDao.insert(agentId, agentStatBo.getTransactionBos());
            this.activeTraceDao.insert(agentId, agentStatBo.getActiveTraceBos());
        } catch (Exception e) {
            logger.warn("Error inserting AgentStatBo. Caused:{}", e.getMessage(), e);
        }

    }
}
