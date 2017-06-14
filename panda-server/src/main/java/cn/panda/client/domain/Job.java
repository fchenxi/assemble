package cn.panda.client.domain;

import cn.panda.client.exception.JobSubmitException;
import cn.panda.util.StringUtils;

public class Job {

    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void checkField() throws JobSubmitException {
        if (StringUtils.isEmpty(taskId)) {
            throw new JobSubmitException("taskId can not be empty! job is " + toString());
        }
        if (taskId.length() > 64) {
            throw new JobSubmitException("taskId length should not great than 64! job is " +
                    toString());
        }

    }
}
