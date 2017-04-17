package cn.panda.model;

import cn.panda.excel.ExcelResources;

import java.io.Serializable;

/**
 * @usage       任务完成时间要求
 * @author      tong.cx
 * @version     0.0.1
 * @datetime    2016/3/28 17:05
 * @copyright   wonhigh.cn
 */
public class AbstractJob implements Serializable{

    /**
     * 主键ID
     */
    private String id;
    /**
     * 触发器Name
     */
    private String triggerName;
    /**
     * 触发器分组
     */
    private String triggerGroup;
    /**
     * BeanUtils不支持Date类型,数据库为TimeStamp
     * Job完成时间限制
     */
    private String jobReqTime;
    /**
     * Job优先级
     */
    private String jobPri;
    /**
     * 备注: 1.知会DC当日值班人员进行即时修复操作; 2.知会BI当日值班人员该问题。
     */
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ExcelResources(title="任务分组",order=1)
    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }
    @ExcelResources(title="任务名称",order=2)
    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @ExcelResources(title="优先级",order=3)
    public String getJobPri() {
        return jobPri;
    }

    public void setJobPri(String jobPri) {
        int length = jobPri.length();
        if(jobPri.contains(".") && length > 0){
            length = jobPri.indexOf(".");
            jobPri = jobPri.substring(0, length);
        }
        this.jobPri = jobPri;
    }
    @ExcelResources(title="时限要求",order=4)
    public String getJobReqTime() {
        return jobReqTime;
    }

    public void setJobReqTime(String jobReqTime) {
        int length = jobReqTime.length();
        if(jobReqTime.contains(".") && length > 0){
            length = jobReqTime.indexOf(".");
            jobReqTime = jobReqTime.substring(0, length);
        }
        this.jobReqTime = jobReqTime;
    }

    @ExcelResources(title="备注",order=5)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public AbstractJob() {
    }


    public AbstractJob(String triggerName, String triggerGroup, String jobReqTime, String jobPri, String remark) {
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.jobReqTime = jobReqTime;
        this.jobPri = jobPri;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "AbstractJob{" +
                "id='" + id + '\'' +
                ", triggerName='" + triggerName + '\'' +
                ", triggerGroup='" + triggerGroup + '\'' +
                ", jobReqTime='" + jobReqTime + '\'' +
                ", jobPri=" + jobPri +
                ", remark='" + remark + '\'' +
                '}';
    }
}
