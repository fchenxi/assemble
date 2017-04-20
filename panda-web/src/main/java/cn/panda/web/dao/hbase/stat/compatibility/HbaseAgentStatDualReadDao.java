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

package cn.panda.web.dao.hbase.stat.compatibility;
import cn.panda.common.server.bo.stat.*;
import cn.panda.web.dao.hbase.stat.ActiveTraceDao;
import cn.panda.web.dao.hbase.stat.AgentStatDao;
import cn.panda.web.vo.Range;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author HyunGil Jeong
 */
public abstract class HbaseAgentStatDualReadDao<T extends AgentStatDataPoint> implements AgentStatDao<T> {

    private final AgentStatDao<T> master;
    private final AgentStatDao<T> slave;

    protected HbaseAgentStatDualReadDao(AgentStatDao<T> master, AgentStatDao<T> slave) {
        this.master = master;
        this.slave = slave;
    }

    @Override
    public List<T> getAgentStatList(String agentId, Range range) {
        List<T> agentStats = this.master.getAgentStatList(agentId, range);
        if (CollectionUtils.isNotEmpty(agentStats)) {
            return agentStats;
        } else {
            return this.slave.getAgentStatList(agentId, range);
        }
    }

    @Override
    public boolean agentStatExists(String agentId, Range range) {
        boolean exists = this.master.agentStatExists(agentId, range);
        if (exists) {
            return true;
        } else {
            return this.slave.agentStatExists(agentId, range);
        }
    }

    public static class ActiveTraceDualReadDao extends HbaseAgentStatDualReadDao<ActiveTraceBo> implements ActiveTraceDao {
        public ActiveTraceDualReadDao(AgentStatDao<ActiveTraceBo> master, AgentStatDao<ActiveTraceBo> slave) {
            super(master, slave);
        }
    }

}
