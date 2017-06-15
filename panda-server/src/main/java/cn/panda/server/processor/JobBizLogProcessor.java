package cn.panda.server.processor;


import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.JobProtos;

import java.util.List;

public class JobBizLogProcessor extends AbstractRemotingProcessor {

//    private JobBizLogProcessor(JobTrackerAppContext appContext) {
//        super(appContext);
//    }

    private JobBizLogProcessor() {
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws
            RemotingCommandException {

//        BizLogSendRequest requestBody = request.getBody();
//
//        List<BizLog> bizLogs = requestBody.getBizLogs();
//        if (CollectionUtils.isNotEmpty(bizLogs)) {
//            for (BizLog bizLog : bizLogs) {
//                JobLogPo jobLogPo = new JobLogPo();
//                jobLogPo.setGmtCreated(SystemClock.now());
//                jobLogPo.setLogTime(bizLog.getLogTime());
//                jobLogPo.setTaskTrackerNodeGroup(bizLog.getTaskTrackerNodeGroup());
//                jobLogPo.setTaskTrackerIdentity(bizLog.getTaskTrackerIdentity());
//                jobLogPo.setJobId(bizLog.getJobId());
//                jobLogPo.setTaskId(bizLog.getTaskId());
//                jobLogPo.setRealTaskId(bizLog.getRealTaskId());
//                jobLogPo.setJobType(bizLog.getJobType());
//                jobLogPo.setMsg(bizLog.getMsg());
//                jobLogPo.setSuccess(true);
//                jobLogPo.setLevel(bizLog.getLevel());
//                jobLogPo.setLogType(LogType.BIZ);
//                appContext.getJobLogger().log(jobLogPo);
//            }
//        }

        return RemotingCommand.createResponseCommand(JobProtos.ResponseCode.BIZ_LOG_SEND_SUCCESS
                .code(), "");
    }
}
