package cn.panda.client.executor;

import cn.panda.client.domain.JobResult;

import java.util.List;

public interface JobCompletedHandler {

    void onComplete(List<JobResult> jObResults);

}
