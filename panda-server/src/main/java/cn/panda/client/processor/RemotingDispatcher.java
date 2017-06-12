package cn.panda.client.processor;


import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingProcessor;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.JobProtos;
import cn.panda.remoting.protocol.RemotingCommand;
import cn.panda.remoting.protocol.RemotingProtos;

import java.util.HashMap;
import java.util.Map;

import static cn.panda.remoting.protocol.JobProtos.RequestCode.JOB_COMPLETED;
import static cn.panda.remoting.protocol.JobProtos.RequestCode.valueOf;

/**
 * @author Robert HG (254963746@qq.com) on 7/25/14.
 *         客户端默认通信处理器
 */
public class RemotingDispatcher implements RemotingProcessor {

    private final Map<JobProtos.RequestCode, RemotingProcessor> processors = new
            HashMap<JobProtos.RequestCode, RemotingProcessor>();

    //    public RemotingDispatcher(JobClientAppContext appContext) {
//        processors.put(JOB_COMPLETED, new JobFinishedProcessor(appContext));
//    }
    public RemotingDispatcher() {
        processors.put(JOB_COMPLETED, new JobFinishedProcessor());
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws
            RemotingCommandException {
        JobProtos.RequestCode code = valueOf(request.getCode());
        RemotingProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode
                    .REQUEST_CODE_NOT_SUPPORTED.code(), "request code not supported!");
        }
        return processor.processRequest(channel, request);
    }
}
