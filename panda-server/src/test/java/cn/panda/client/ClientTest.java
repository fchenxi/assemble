package cn.panda.client;


import cn.panda.client.domain.Job;
import cn.panda.client.domain.Response;

public class ClientTest {
    public static void main(String[] args) {
        JobClient jobClient = new JobClient();
//        jobClient.setNodeGroup("test_jobClient");
//        jobClient.setClusterName("test_cluster");
//        jobClient.setRegistryAddress("zookeeper://127.0.0.1:2181");
//        jobClient.setJobCompletedHandler(new JobCompletedHandlerImpl());
//        jobClient.addConfig("job.fail.store", "mapdb");
//        jobClient.start();
        submitCronJob(jobClient);
    }
    private static void submitCronJob(JobClient jobClient) {
        Job job = new Job();
        job.setTaskId("t_cron_555");
//        job.setParam("shopId", "1122222221");
//        job.setTaskTrackerNodeGroup("test_trade_TaskTracker");      // 执行要执行该任务的taskTracker的节点组名称
//        job.setNeedFeedback(true);
//        job.setReplaceOnExist(true);        // 当任务队列中存在这个任务的时候，是否替换更新
//        job.setCronExpression("0 0/1 * * * ?");
        Response response = jobClient.submitJob(job);
        System.out.println(response);
    }
}

