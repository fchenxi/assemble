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

package cn.panda.web.service.stat;

import cn.panda.common.server.bo.stat.ActiveTraceBo;
import cn.panda.web.dao.hbase.stat.ActiveTraceDao;
import cn.panda.web.vo.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HyunGil Jeong
 */
@Service("agentStatService")
public class ActiveTraceService implements AgentStatService<ActiveTraceBo> {

    private final ActiveTraceDao activeTraceDao;

    @Autowired
    public ActiveTraceService(@Qualifier("activeTraceDaoFactory") ActiveTraceDao activeTraceDao) {
        this.activeTraceDao = activeTraceDao;
    }

    @Override
    public List<ActiveTraceBo> selectAgentStatList(String agentId, Range range) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (range == null) {
            throw new NullPointerException("range must not be null");
        }
        return this.activeTraceDao.getAgentStatList(agentId, range);
    }
}
