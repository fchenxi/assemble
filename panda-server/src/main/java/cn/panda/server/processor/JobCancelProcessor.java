package cn.panda.server.processor;


import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.JobProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobCancelProcessor extends AbstractRemotingProcessor {
    private final Logger LOGGER = LoggerFactory.getLogger(JobCancelProcessor.class);

//    public JobCancelProcessor(JobTrackerAppContext appContext) {
//        super(appContext);
//    }

    public JobCancelProcessor() {
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {

//        JobCancelRequest jobCancelRequest = request.getBody();
//
//        String taskId = jobCancelRequest.getTaskId();
//        String taskTrackerNodeGroup = jobCancelRequest.getTaskTrackerNodeGroup();
//        JobPo jobPo = appContext.getCronJobQueue().getJob(taskTrackerNodeGroup, taskId);
//        if (jobPo == null) {
//            jobPo = appContext.getRepeatJobQueue().getJob(taskTrackerNodeGroup, taskId);
//        }
//        if (jobPo == null) {
//            jobPo = appContext.getExecutableJobQueue().getJob(taskTrackerNodeGroup, taskId);
//        }
//        if (jobPo == null) {
//            jobPo = appContext.getSuspendJobQueue().getJob(taskTrackerNodeGroup, taskId);
//        }
//
//        if (jobPo != null) {
//            // 队列都remove下吧
//            appContext.getExecutableJobQueue().removeBatch(jobPo.getRealTaskId(), jobPo.getTaskTrackerNodeGroup());
//            if (jobPo.isCron()) {
//                appContext.getCronJobQueue().remove(jobPo.getJobId());
//            } else if (jobPo.isRepeatable()) {
//                appContext.getRepeatJobQueue().remove(jobPo.getJobId());
//            }
//            appContext.getSuspendJobQueue().remove(jobPo.getJobId());
//
//            // 记录日志
//            JobLogPo jobLogPo = JobDomainConverter.convertJobLog(jobPo);
//            jobLogPo.setSuccess(true);
//            jobLogPo.setLogType(LogType.DEL);
//            jobLogPo.setLogTime(SystemClock.now());
//            jobLogPo.setLevel(Level.INFO);
//            appContext.getJobLogger().log(jobLogPo);

//            LOGGER.info("Cancel Job success , jobId={}, taskId={}, taskTrackerNodeGroup={}", jobPo.getJobId(), taskId, taskTrackerNodeGroup);
            return RemotingCommand.createResponseCommand(JobProtos
                    .ResponseCode.JOB_CANCEL_SUCCESS.code());
        }

        return RemotingCommand.createResponseCommand(JobProtos
                .ResponseCode.JOB_CANCEL_FAILED.code(), "Job maybe running");
    }
}
}
