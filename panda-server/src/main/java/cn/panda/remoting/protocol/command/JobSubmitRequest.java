package cn.panda.remoting.protocol.command;

import cn.panda.client.domain.Job;

import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 7/24/14.
 *         任务传递信息
 */
public class JobSubmitRequest extends AbstractRemotingCommandBody {

    private static final long serialVersionUID = 7229438891247265777L;

    private List<Job> jobs;

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

}
