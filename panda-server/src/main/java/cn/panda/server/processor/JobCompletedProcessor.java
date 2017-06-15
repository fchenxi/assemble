package cn.panda.server.processor;


import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.RemotingProtos;
import cn.panda.remoting.protocol.command.JobCompletedRequest;
import cn.panda.server.complete.biz.JobCompletedBiz;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JobCompletedProcessor extends AbstractRemotingProcessor {

    private List<JobCompletedBiz> bizChain;

    //    public JobCompletedProcessor(final JobTrackerAppContext appContext) {
//        super(appContext);
//
//        this.bizChain = new CopyOnWriteArrayList<JobCompletedBiz>();
//        this.bizChain.add(new JobStatBiz(appContext));        // 统计
//        this.bizChain.add(new JobProcBiz(appContext));          // 完成处理
//        this.bizChain.add(new PushNewJobBiz(appContext));           // 获取新任务
//
//    }
    public JobCompletedProcessor() {
        this.bizChain = new CopyOnWriteArrayList<JobCompletedBiz>();
        this.bizChain.add(new JobStatBiz(appContext));        // 统计
        this.bizChain.add(new JobProcBiz(appContext));          // 完成处理
        this.bizChain.add(new PushNewJobBiz(appContext));           // 获取新任务

    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {

        JobCompletedRequest requestBody = request.getBody();

        for (JobCompletedBiz biz : bizChain) {
            RemotingCommand remotingCommand = biz.doBiz(requestBody);
            if (remotingCommand != null) {
                return remotingCommand;
            }
        }
        return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.SUCCESS.code());
    }

}
