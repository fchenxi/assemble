package cn.panda.client.executor;


import cn.panda.client.domain.Job;
import cn.panda.client.exception.JobSubmitException;

import java.util.List;

public interface JobSubmitExecutor<T> {

    T execute(List<Job> jobs) throws JobSubmitException;

}
