/*
 * Copyright 2014 NAVER Corp.
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

package cn.panda.web.controller;


import cn.panda.common.server.bo.stat.ActiveTraceBo;
import cn.panda.common.server.bo.stat.AgentStatDataPoint;
import cn.panda.web.service.stat.ActiveTraceService;
import cn.panda.web.service.stat.AgentStatService;
import cn.panda.web.vo.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * @author emeroad
 * @author minwoo.jung
 * @author HyunGil Jeong
 */
public abstract class AgentStatController<T extends AgentStatDataPoint> {

    private final AgentStatService<T> agentStatService;

    public AgentStatController(AgentStatService<T> agentStatService) {
        this.agentStatService = agentStatService;
    }
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<T> getAgentStat(
            @RequestParam("agentId") String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to) {
        Range rangeToScan = new Range(from, to);
        return this.agentStatService.selectAgentStatList(agentId, rangeToScan);
    }
    @Controller
    @RequestMapping("/getAgentStat/activeTrace")
    public static class ActiveTraceController extends AgentStatController<ActiveTraceBo> {
        @Autowired
        public ActiveTraceController(ActiveTraceService activeTraceService) {
            super(activeTraceService);
        }
    }
}
