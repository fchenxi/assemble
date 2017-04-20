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

package cn.panda.common.hbase.server.bo.serializer.stat;

import cn.panda.common.hbase.server.bo.serializer.RowKeyEncoder;
import cn.panda.common.server.bo.stat.AgentStatRowKeyComponent;
import cn.panda.common.utils.BytesUtils;
import cn.panda.common.utils.TimeUtils;
import org.springframework.stereotype.Component;

import static cn.panda.common.hbase.HBaseTables.AGENT_NAME_MAX_LEN;

/**
 * @author HyunGil Jeong
 */
@Component
public class AgentStatRowKeyEncoder implements RowKeyEncoder<AgentStatRowKeyComponent> {

    @Override
    public byte[] encodeRowKey(AgentStatRowKeyComponent component) {
        if (component == null) {
            throw new NullPointerException("component must not be null");
        }
        byte[] bAgentId = BytesUtils.toBytes(component.getAgentId());
        byte[] bStatType = new byte[]{component.getAgentStatType().getRawTypeCode()};
        byte[] rowKey = new byte[AGENT_NAME_MAX_LEN + bStatType.length + BytesUtils.LONG_BYTE_LENGTH];

        BytesUtils.writeBytes(rowKey, 0, bAgentId);
        BytesUtils.writeBytes(rowKey, AGENT_NAME_MAX_LEN, bStatType);
        BytesUtils.writeLong(TimeUtils.reverseTimeMillis(component.getBaseTimestamp()), rowKey, AGENT_NAME_MAX_LEN + bStatType.length);

        return rowKey;
    }
}
