package cn.panda.server.processor;

import cn.panda.constants.ExtConfig;
import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.RemotingProcessor;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.JobProtos;
import cn.panda.remoting.protocol.RemotingProtos;
import cn.panda.remoting.protocol.command.AbstractRemotingCommandBody;
import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RemotingDispatcher extends AbstractRemotingProcessor {

    private final Map<JobProtos.RequestCode, RemotingProcessor> processors = new
            HashMap<JobProtos.RequestCode, RemotingProcessor>();
    private RateLimiter rateLimiter;
    private int reqLimitAcquireTimeout = 50;
    private boolean reqLimitEnable = false;

    //    public RemotingDispatcher(JobTrackerAppContext appContext) {
//        super(appContext);
//        processors.put(RequestCode.SUBMIT_JOB, new JobSubmitProcessor(appContext));
//        processors.put(RequestCode.JOB_COMPLETED, new JobCompletedProcessor(appContext));
//        processors.put(RequestCode.JOB_PULL, new JobPullProcessor(appContext));
//        processors.put(RequestCode.BIZ_LOG_SEND, new JobBizLogProcessor(appContext));
//        processors.put(RequestCode.CANCEL_JOB, new JobCancelProcessor(appContext));
//
//        this.reqLimitEnable = appContext.getConfig().getParameter(ExtConfig
// .JOB_TRACKER_REMOTING_REQ_LIMIT_ENABLE, false);
//        Integer maxQPS = appContext.getConfig().getParameter(ExtConfig
// .JOB_TRACKER_REMOTING_REQ_LIMIT_MAX_QPS, 5000);
//        this.rateLimiter = RateLimiter.create(maxQPS);
//        this.reqLimitAcquireTimeout = appContext.getConfig().getParameter(ExtConfig
// .JOB_TRACKER_REMOTING_REQ_LIMIT_ACQUIRE_TIMEOUT, 50);
//    }
    public RemotingDispatcher() {
//        processors.put(JobProtos.RequestCode.SUBMIT_JOB, new JobSubmitProcessor(appContext));
//        processors.put(JobProtos.RequestCode.JOB_COMPLETED, new JobCompletedProcessor(appContext));
//        processors.put(JobProtos.RequestCode.JOB_PULL, new JobPullProcessor(appContext));
//        processors.put(JobProtos.RequestCode.BIZ_LOG_SEND, new JobBizLogProcessor(appContext));
//        processors.put(JobProtos.RequestCode.CANCEL_JOB, new JobCancelProcessor(appContext));

        processors.put(JobProtos.RequestCode.SUBMIT_JOB, new JobSubmitProcessor());
        processors.put(JobProtos.RequestCode.JOB_COMPLETED, new JobCompletedProcessor());
        processors.put(JobProtos.RequestCode.JOB_PULL, new JobPullProcessor());
        processors.put(JobProtos.RequestCode.BIZ_LOG_SEND, new JobBizLogProcessor());
        processors.put(JobProtos.RequestCode.CANCEL_JOB, new JobCancelProcessor());

//        this.reqLimitEnable = appContext.getConfig().getParameter(ExtConfig
//                .JOB_TRACKER_REMOTING_REQ_LIMIT_ENABLE, false);
        this.reqLimitEnable = false;
//        Integer maxQPS = appContext.getConfig().getParameter(ExtConfig
//                .JOB_TRACKER_REMOTING_REQ_LIMIT_MAX_QPS, 5000);
        Integer maxQPS = 5000;
        this.rateLimiter = RateLimiter.create(maxQPS);
//        this.reqLimitAcquireTimeout = appContext.getConfig().getParameter(ExtConfig
//                .JOB_TRACKER_REMOTING_REQ_LIMIT_ACQUIRE_TIMEOUT, 50);

        this.reqLimitAcquireTimeout = 50;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws
            RemotingCommandException {
        // 心跳
        if (request.getCode() == JobProtos.RequestCode.HEART_BEAT.code()) {
            offerHandler(channel, request);
            return RemotingCommand.createResponseCommand(JobProtos.ResponseCode
                    .HEART_BEAT_SUCCESS.code(), "");
        }
        if (reqLimitEnable) {
            return doBizWithReqLimit(channel, request);
        } else {
            return doBiz(channel, request);
        }
    }

    /**
     * 限流处理
     */
    private RemotingCommand doBizWithReqLimit(Channel channel, RemotingCommand request) throws
            RemotingCommandException {

        if (rateLimiter.tryAcquire(reqLimitAcquireTimeout, TimeUnit.MILLISECONDS)) {
            return doBiz(channel, request);
        }
        return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.SYSTEM_BUSY.code
                (), "remoting server is busy!");
    }

    private RemotingCommand doBiz(Channel channel, RemotingCommand request) throws
            RemotingCommandException {
        // 其他的请求code
        JobProtos.RequestCode code = JobProtos.RequestCode.valueOf(request.getCode());
        RemotingProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode
                    .REQUEST_CODE_NOT_SUPPORTED.code(), "request code not supported!");
        }
        offerHandler(channel, request);
        return processor.processRequest(channel, request);
    }

    /**
     * 1. 将 channel 纳入管理中(不存在就加入)
     * 2. 更新 TaskTracker 节点信息(可用线程数)
     */
    private void offerHandler(Channel channel, RemotingCommand request) {
        AbstractRemotingCommandBody commandBody = request.getBody();
//        String nodeGroup = commandBody.getNodeGroup();
//        String identity = commandBody.getIdentity();
//        NodeType nodeType = NodeType.valueOf(commandBody.getNodeType());

        // 1. 将 channel 纳入管理中(不存在就加入)
//        appContext.getChannelManager().offerChannel(new ChannelWrapper(channel, nodeType,
// nodeGroup, identity));
    }
}
