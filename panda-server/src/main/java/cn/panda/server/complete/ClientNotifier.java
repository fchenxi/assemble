package cn.panda.server.complete;

import cn.panda.remoting.protocol.command.JobFinishedRequest;
import cn.panda.remoting.protocol.command.JobRunResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Robert HG (254963746@qq.com) on 3/2/15.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientNotifier.class
            .getSimpleName());
    private ClientNotifyHandler clientNotifyHandler;
//    private JobTrackerAppContext appContext;

    //    public ClientNotifier(JobTrackerAppContext appContext, ClientNotifyHandler
    // clientNotifyHandler) {
//        this.appContext = appContext;
//        this.clientNotifyHandler = clientNotifyHandler;
//    }
    public ClientNotifier() {
        this.clientNotifyHandler = clientNotifyHandler;
    }

    /**
     * 发送给客户端
     *
     * @return 返回成功的个数
     */
    public <T extends JobRunResult> int send(List<T> jobResults) {
        if (CollectionUtils.isEmpty(jobResults)) {
            return 0;
        }

//        // 单个 就不用 分组了
//        if (jobResults.size() == 1) {
//
//            JobRunResult result = jobResults.get(0);
//            if (!send0(result.getJobMeta().getJob().getSubmitNodeGroup(), Collections
//                    .singletonList(result))) {
//                // 如果没有完成就返回
//                clientNotifyHandler.handleFailed(jobResults);
//                return 0;
//            }
//        } else if (jobResults.size() > 1) {
//
//            List<JobRunResult> failedJobRunResult = new ArrayList<JobRunResult>();
//
//            // 有多个要进行分组 (出现在 失败重发的时候)
//            Map<String/*nodeGroup*/, List<JobRunResult>> groupMap = new HashMap<String,
//                    List<JobRunResult>>();
//
//            for (T jobResult : jobResults) {
//                List<JobRunResult> results = groupMap.get(jobResult.getJobMeta().getJob()
//                        .getSubmitNodeGroup());
//                if (results == null) {
//                    results = new ArrayList<JobRunResult>();
//                    groupMap.put(jobResult.getJobMeta().getJob().getSubmitNodeGroup(), results);
//                }
//                results.add(jobResult);
//            }
//            for (Map.Entry<String, List<JobRunResult>> entry : groupMap.entrySet()) {
//
//                if (!send0(entry.getKey(), entry.getValue())) {
//                    failedJobRunResult.addAll(entry.getValue());
//                }
//            }
//            clientNotifyHandler.handleFailed(failedJobRunResult);
//            return jobResults.size() - failedJobRunResult.size();
//        }
        return jobResults.size();
    }

    /**
     * 发送给客户端
     * 返回是否发送成功还是失败
     */
    private boolean send0(String nodeGroup, final List<JobRunResult> results) {
        // 得到 可用的客户端节点
        JobClientNode jobClientNode = appContext.getJobClientManager().getAvailableJobClient
                (nodeGroup);

        if (jobClientNode == null) {
            return false;
        }
        List<JobResult> jobResults = new ArrayList<JobResult>(results.size());
        for (JobRunResult result : results) {
            JobResult jobResult = new JobResult();

            Job job = JobUtils.copy(result.getJobMeta().getJob());
            job.setTaskId(result.getJobMeta().getRealTaskId());
            jobResult.setJob(job);
            jobResult.setSuccess(Action.EXECUTE_SUCCESS.equals(result.getAction()));
            jobResult.setMsg(result.getMsg());
            jobResult.setTime(result.getTime());
            jobResult.setExeSeqId(result.getJobMeta().getInternalExtParam(Constants.EXE_SEQ_ID));
            jobResults.add(jobResult);
        }

        JobFinishedRequest requestBody = appContext.getCommandBodyWrapper().wrapper(new
                JobFinishedRequest());
        requestBody.setJobResults(jobResults);
        RemotingCommand commandRequest = RemotingCommand.createRequestCommand(JobProtos
                .RequestCode.JOB_COMPLETED.code(), requestBody);

        final Holder<Boolean> result = new Holder<Boolean>();
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            getRemotingServer().invokeAsync(jobClientNode.getChannel().getChannel(),
                    commandRequest, new AsyncCallback() {
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                    try {
                        RemotingCommand commandResponse = responseFuture.getResponseCommand();

                        if (commandResponse != null && commandResponse.getCode() == JobProtos
                                .ResponseCode.JOB_NOTIFY_SUCCESS.code()) {
                            clientNotifyHandler.handleSuccess(results);
                            result.set(true);
                        } else {
                            result.set(false);
                        }
                    } finally {
                        latch.countDown();
                    }
                }
            });

            try {
                latch.await(Constants.LATCH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RequestTimeoutException(e);
            }

        } catch (RemotingSendException e) {
            LOGGER.error("Notify client failed!", e);
        }
        return result.get() == null ? false : result.get();
    }

    private RemotingServerDelegate getRemotingServer() {
        return appContext.getRemotingServer();
    }

}
