package cn.panda.client;

import cn.panda.client.domain.Job;
import cn.panda.client.domain.Response;
import cn.panda.client.domain.ResponseCode;
import cn.panda.client.exception.JobSubmitException;
import cn.panda.client.exception.JobTrackerNotFoundException;
import cn.panda.client.executor.JobCompletedHandler;
import cn.panda.client.executor.JobSubmitExecutor;
import cn.panda.client.executor.SubmitCallback;
import cn.panda.client.processor.RemotingDispatcher;
import cn.panda.constants.Constants;
import cn.panda.remoting.AsyncCallback;
import cn.panda.remoting.RemotingClientDelegate;
import cn.panda.remoting.RemotingProcessor;
import cn.panda.remoting.ResponseFuture;
import cn.panda.remoting.protocol.JobProtos;
import cn.panda.remoting.protocol.RemotingCommand;
import cn.panda.remoting.protocol.command.CommandBodyWrapper;
import cn.panda.remoting.protocol.command.JobSubmitRequest;
import cn.panda.remoting.protocol.command.JobSubmitResponse;
import cn.panda.util.BatchUtils;
import cn.panda.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.panda.remoting.netty.NettyRemotingServer.LOGGER;

public class JobClient {

    private JobSubmitProtector protector;

    protected RemotingClientDelegate remotingClient;

    protected AtomicBoolean started = new AtomicBoolean(false);

    private static final int BATCH_SIZE = 10;


    private Response protectSubmit(List<Job> jobs) throws JobSubmitException {
        return protector.execute(jobs, new JobSubmitExecutor<Response>() {
            @Override
            public Response execute(List<Job> jobs) throws JobSubmitException {
                return submitJob(jobs, SubmitType.ASYNC);
            }
        });
    }

    protected Response submitJob(final List<Job> jobs, SubmitType type) throws JobSubmitException {
        // 检查参数
        checkFields(jobs);

        final Response response = new Response();
        try {
//            JobSubmitRequest jobSubmitRequest = CommandBodyWrapper.wrapper(new JobSubmitRequest());
            JobSubmitRequest jobSubmitRequest = CommandBodyWrapper.wrapper(new JobSubmitRequest());

            jobSubmitRequest.setJobs(jobs);

            RemotingCommand requestCommand = RemotingCommand.createRequestCommand(
                    JobProtos.RequestCode.SUBMIT_JOB.code(), jobSubmitRequest);

            SubmitCallback submitCallback = new SubmitCallback() {
                @Override
                public void call(RemotingCommand responseCommand) {
                    if (responseCommand == null) {
                        response.setFailedJobs(jobs);
                        response.setSuccess(false);
                        response.setMsg("Submit Job failed: JobTracker is broken");
                        LOGGER.warn("Submit Job failed: {}, {}", jobs, "JobTracker is broken");
                        return;
                    }

                    if (JobProtos.ResponseCode.JOB_RECEIVE_SUCCESS.code() == responseCommand
                            .getCode()) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Submit Job success: {}", jobs);
                        }
                        response.setSuccess(true);
                        return;
                    }
                    // 失败的job
                    JobSubmitResponse jobSubmitResponse = responseCommand.getBody();
                    response.setFailedJobs(jobSubmitResponse.getFailedJobs());
                    response.setSuccess(false);
                    response.setCode(JobProtos.ResponseCode.valueOf(responseCommand.getCode())
                            .name());
                    response.setMsg("Submit Job failed: " + responseCommand.getRemark() + " " +
                            jobSubmitResponse.getMsg());
                    LOGGER.warn("Submit Job failed: {}, {}, {}", jobs, responseCommand.getRemark
                            (), jobSubmitResponse.getMsg());
                }
            };

            if (SubmitType.ASYNC.equals(type)) {
                asyncSubmit(requestCommand, submitCallback);
            } else {
                syncSubmit(requestCommand, submitCallback);
            }
        } catch (JobTrackerNotFoundException e) {
            response.setSuccess(false);
            response.setCode(ResponseCode.JOB_TRACKER_NOT_FOUND);
            response.setMsg("Can not found JobTracker node!");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setCode(ResponseCode.SYSTEM_ERROR);
            response.setMsg(StringUtils.toString(e));
        } finally {
            // 统计

        }

        return response;
    }

    private void asyncSubmit(RemotingCommand requestCommand, final SubmitCallback submitCallback)
            throws JobTrackerNotFoundException {
        final CountDownLatch latch = new CountDownLatch(1);
        remotingClient.invokeAsync(requestCommand, new AsyncCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                try {
                    submitCallback.call(responseFuture.getResponseCommand());
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await(Constants.LATCH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new JobSubmitException("Submit job failed, async request timeout!", e);
        }
    }

    /**
     * 同步提交任务
     */
    private void syncSubmit(RemotingCommand requestCommand, final SubmitCallback submitCallback)
            throws JobTrackerNotFoundException {
        submitCallback.call(remotingClient.invokeSync(requestCommand));
    }

    public Response submitJob(List<Job> jobs) throws JobSubmitException {
        checkStart();
        final Response response = new Response();
        response.setSuccess(true);
        int size = jobs.size();

        BatchUtils.batchExecute(size, BATCH_SIZE, jobs, new BatchUtils.Executor<Job>() {
            @Override
            public boolean execute(List<Job> list) {
                Response subResponse = protectSubmit(list);
                if (!subResponse.isSuccess()) {
                    response.setSuccess(false);
                    response.addFailedJobs(list);
                    response.setMsg(subResponse.getMsg());
                }
                return true;
            }
        });
        return response;
    }

    //    @Override
//    protected RemotingProcessor getDefaultProcessor() {
//        return new RemotingDispatcher(appContext);
//    }
//    @Override
    protected RemotingProcessor getDefaultProcessor() {
        return new RemotingDispatcher();
    }

    /**
     * 设置任务完成接收器
     */
//    public void setJobCompletedHandler(JobCompletedHandler jobCompletedHandler) {
//        appContext.setJobCompletedHandler(jobCompletedHandler);
//    }

    enum SubmitType {
        SYNC,   // 同步
        ASYNC   // 异步
    }

    private void checkStart() {
        if (!started.get()) {
            throw new JobSubmitException("JobClient did not started");
        }
    }
    private void checkFields(List<Job> jobs) {
        // 参数验证
        if (CollectionUtils.isEmpty(jobs)) {
            throw new JobSubmitException("Job can not be null!");
        }
        for (Job job : jobs) {
            if (job == null) {
                throw new JobSubmitException("Job can not be null!");
            } else {
                job.checkField();
            }
        }
    }
}
