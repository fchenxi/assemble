package cn.panda.remoting.netty;


import cn.panda.constants.RemotingClientConfig;
import cn.panda.constants.RemotingServerConfig;
import cn.panda.remoting.RemotingClient;
import cn.panda.remoting.RemotingServer;
import cn.panda.remoting.RemotingTransporter;

/**
 * @author Robert HG (254963746@qq.com) on 11/6/15.
 */
public class NettyRemotingTransporter implements RemotingTransporter {

//    @Override
//    public RemotingServer getRemotingServer(AppContext appContext, RemotingServerConfig remotingServerConfig) {
//        return new NettyRemotingServer(appContext, remotingServerConfig);
//    }
//
//    @Override
//    public RemotingClient getRemotingClient(AppContext appContext, RemotingClientConfig remotingClientConfig) {
//        return new NettyRemotingClient(appContext, remotingClientConfig);
//    }
    @Override
    public RemotingServer getRemotingServer(RemotingServerConfig remotingServerConfig) {
        return new NettyRemotingServer(remotingServerConfig);
    }

    @Override
    public RemotingClient getRemotingClient(RemotingClientConfig remotingClientConfig) {
        return new NettyRemotingClient(remotingClientConfig);
    }
}
