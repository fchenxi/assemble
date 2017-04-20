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

package cn.panda.common.hbase.server.bo.codec.stat.v1;

import cn.panda.common.buffer.Buffer;
import cn.panda.common.hbase.server.bo.codec.stat.AgentStatCodec;
import cn.panda.common.hbase.server.bo.codec.stat.AgentStatDataPointCodec;
import cn.panda.common.hbase.server.bo.serializer.stat.AgentStatDecodingContext;
import cn.panda.common.server.bo.stat.ActiveTraceBo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HyunGil Jeong
 */
@Component
public class ActiveTraceCodecV1 implements AgentStatCodec<ActiveTraceBo> {

    private static final byte VERSION = 1;

    private final AgentStatDataPointCodec codec;

    @Autowired
    public ActiveTraceCodecV1(AgentStatDataPointCodec codec) {
        Assert.notNull(codec, "agentStatDataPointCodec must not be null");
        this.codec = codec;
    }

    @Override
    public byte getVersion() {
        return VERSION;
    }

    @Override
    public void encodeValues(Buffer valueBuffer, List<ActiveTraceBo> activeTraceBos) {
        if (CollectionUtils.isEmpty(activeTraceBos)) {
            throw new IllegalArgumentException("activeTraceBos must not be empty");
        }
        final int numValues = activeTraceBos.size();
        valueBuffer.putVInt(numValues);

    }


    @Override
    public List<ActiveTraceBo> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {
        final String agentId = decodingContext.getAgentId();
        final long baseTimestamp = decodingContext.getBaseTimestamp();
        final long timestampDelta = decodingContext.getTimestampDelta();
        final long initialTimestamp = baseTimestamp + timestampDelta;

        int numValues = valueBuffer.readVInt();

        List<ActiveTraceBo> activeTraceBos = new ArrayList<>(numValues);

        return activeTraceBos;
    }
}
