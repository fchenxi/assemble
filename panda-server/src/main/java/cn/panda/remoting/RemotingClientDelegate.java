package cn.panda.remoting;

import cn.panda.client.exception.JobTrackerNotFoundException;
import cn.panda.remoting.exception.RemotingException;
import cn.panda.remoting.protocol.RemotingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * @author Robert HG (254963746@qq.com) on 8/1/14.
 */
public class RemotingClientDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotingClientDelegate.class);

    private RemotingClient remotingClient;
//    private AppContext appContext;

    // JobTracker 是否可用
    private volatile boolean serverEnable = false;
//    private List<Node> jobTrackers;

    //    public RemotingClientDelegate(RemotingClient remotingClient, AppContext appContext) {
//        this.remotingClient = remotingClient;
//        this.appContext = appContext;
//        this.jobTrackers = new CopyOnWriteArrayList<Node>();
//    }
    public RemotingClientDelegate(RemotingClient remotingClient) {
        this.remotingClient = remotingClient;
    }

//    private Node getJobTrackerNode() throws JobTrackerNotFoundException {
//        try {
//            if (jobTrackers.size() == 0) {
//                throw new JobTrackerNotFoundException("no available jobTracker!");
//            }
//            // 连JobTracker的负载均衡算法
//            LoadBalance loadBalance = ServiceLoader.load(LoadBalance.class, appContext.getConfig
//                    (), ExtConfig.JOB_TRACKER_SELECT_LOADBALANCE);
//            return loadBalance.select(jobTrackers, appContext.getConfig().getIdentity());
//        } catch (JobTrackerNotFoundException e) {
//            this.serverEnable = false;
//            // publish msg
//            EventInfo eventInfo = new EventInfo(EcTopic.NO_JOB_TRACKER_AVAILABLE);
//            appContext.getEventCenter().publishAsync(eventInfo);
//            throw e;
//        }
//    }

    public void start() {
        try {
            remotingClient.start();
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        }
    }

//    public boolean contains(Node jobTracker) {
//        return jobTrackers.contains(jobTracker);
//    }

//    public void addJobTracker(Node jobTracker) {
//        if (!contains(jobTracker)) {
//            jobTrackers.add(jobTracker);
//        }
//    }
//
//    public boolean removeJobTracker(Node jobTracker) {
//        return jobTrackers.remove(jobTracker);
//    }

    /**
     * 同步调用
     */
    public RemotingCommand invokeSync(RemotingCommand request)
            throws JobTrackerNotFoundException {

//        Node jobTracker = getJobTrackerNode();

        try {
//            RemotingCommand response = remotingClient.invokeSync(jobTracker.getAddress(),
//                    request, appContext.getConfig().getInvokeTimeoutMillis());
            RemotingCommand response = remotingClient.invokeSync("127.0.0.1",
                    request, 60000);

            this.serverEnable = true;
            return response;
        } catch (Exception e) {
            // 将这个JobTracker移除
//            jobTrackers.remove(jobTracker);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            // 只要不是节点 不可用, 轮询所有节点请求
            return invokeSync(request);
        }
    }

    /**
     * 异步调用
     */
    public void invokeAsync(RemotingCommand request, AsyncCallback asyncCallback)
            throws JobTrackerNotFoundException {

//        Node jobTracker = getJobTrackerNode();

        try {
//            remotingClient.invokeAsync(jobTracker.getAddress(), request,
//                    appContext.getConfig().getInvokeTimeoutMillis(), asyncCallback);
            remotingClient.invokeAsync("127.0.0.1",
                    request, 60000L, asyncCallback);

            this.serverEnable = true;
        } catch (Throwable e) {
            // 将这个JobTracker移除
//            jobTrackers.remove(jobTracker);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            // 只要不是节点 不可用, 轮询所有节点请求
            invokeAsync(request, asyncCallback);
        }
    }

    /**
     * 单向调用
     */
    public void invokeOneway(RemotingCommand request)
            throws JobTrackerNotFoundException {

//        Node jobTracker = getJobTrackerNode();

        try {
//            remotingClient.invokeOneway(jobTracker.getAddress(), request,
//                    appContext.getConfig().getInvokeTimeoutMillis());
            RemotingCommand response = remotingClient.invokeSync("127.0.0.1",
                    request, 60000);

            this.serverEnable = true;
        } catch (Throwable e) {
            // 将这个JobTracker移除
//            jobTrackers.remove(jobTracker);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            // 只要不是节点 不可用, 轮询所有节点请求
            invokeOneway(request);
        }
    }

    public void registerProcessor(int requestCode, RemotingProcessor processor,
                                  ExecutorService executor) {
        remotingClient.registerProcessor(requestCode, processor, executor);
    }

    public void registerDefaultProcessor(RemotingProcessor processor, ExecutorService executor) {
        remotingClient.registerDefaultProcessor(processor, executor);
    }

    public boolean isServerEnable() {
        return serverEnable;
    }

    public void setServerEnable(boolean serverEnable) {
        this.serverEnable = serverEnable;
    }

    public void shutdown() {
        remotingClient.shutdown();
    }

    public RemotingClient getRemotingClient() {
        return remotingClient;
    }
}
