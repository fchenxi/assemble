package cn.panda.web.dao.hbase.stat;

import cn.panda.common.server.bo.stat.AgentStatDataPoint;
import cn.panda.web.vo.Range;

import java.util.List;


/**
 * @author HyunGil Jeong
 */
public interface AgentStatDao<T extends AgentStatDataPoint> {

    List<T> getAgentStatList(String agentId, Range range);

    boolean agentStatExists(String agentId, Range range);

    void insert(String agentId, List<T> agentStatDataPoints);
}
