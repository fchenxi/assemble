package cn.panda.remoting.protocol.command;


import cn.panda.client.domain.JobResult;

import java.util.ArrayList;
import java.util.List;

public class JobFinishedRequest extends AbstractRemotingCommandBody {

    private static final long serialVersionUID = -5455937821973991888L;

    /**
     * 是否接受新任务
     */
    private boolean receiveNewJob = false;

    private List<JobResult> jobResults;

    // 是否是重发(重发是批量发)
    private boolean reSend = false;

    public boolean isReSend() {
        return reSend;
    }

    public void setReSend(boolean reSend) {
        this.reSend = reSend;
    }

    public boolean isReceiveNewJob() {
        return receiveNewJob;
    }

    public void setReceiveNewJob(boolean receiveNewJob) {
        this.receiveNewJob = receiveNewJob;
    }

    public List<JobResult> getJobResults() {
        return jobResults;
    }

    public void setJobResults(List<JobResult> jobResults) {
        this.jobResults = jobResults;
    }

    public void addJobResult(JobResult jobResult) {
        if (jobResults == null) {
            jobResults = new ArrayList<JobResult>();
        }
        jobResults.add(jobResult);
    }

    public void addJobResults(List<JobResult> jobResults) {
        if (this.jobResults == null) {
            this.jobResults = new ArrayList<JobResult>();
        }
        this.jobResults.addAll(jobResults);
    }
}
