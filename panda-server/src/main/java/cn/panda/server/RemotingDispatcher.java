package cn.panda.server;

import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.RemotingProcessor;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.JobProtos;
import cn.panda.remoting.protocol.RemotingProtos;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Robert HG (254963746@qq.com) on 8/14/14.
 *         task tracker 总的处理器, 每一种命令对应不同的处理器
 */
public class RemotingDispatcher extends AbstractProcessor {

    private final Map<JobProtos.RequestCode, RemotingProcessor> processors = new
            HashMap<JobProtos.RequestCode, RemotingProcessor>();

//    public RemotingDispatcher(TaskTrackerAppContext appContext) {
//        super(appContext);
//        processors.put(JobProtos.RequestCode.PUSH_JOB, new JobPushProcessor(appContext));
//        processors.put(JobProtos.RequestCode.JOB_ASK, new JobAskProcessor(appContext));
//    }

    public RemotingDispatcher() {
        processors.put(JobProtos.RequestCode.PUSH_JOB, new JobPushProcessor());
        processors.put(JobProtos.RequestCode.JOB_ASK, new JobAskProcessor());

    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws
            RemotingCommandException {
        JobProtos.RequestCode code = JobProtos.RequestCode.valueOf(request.getCode());
        RemotingProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode
                            .REQUEST_CODE_NOT_SUPPORTED.code(),
                    "request code not supported!");
        }
        return processor.processRequest(channel, request);
    }
}
