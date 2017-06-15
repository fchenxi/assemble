package cn.panda.server.complete.biz;

import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.protocol.command.JobCompletedRequest;

/**
 * @author Robert HG (254963746@qq.com) on 11/11/15.
 */
public interface JobCompletedBiz {

    /**
     * 如果返回空表示继续执行
     */
    RemotingCommand doBiz(JobCompletedRequest request);

}
