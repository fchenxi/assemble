package cn.panda.remoting;

import cn.panda.constants.RemotingClientConfig;
import cn.panda.constants.RemotingServerConfig;

/**
 * @author Robert HG (254963746@qq.com) on 11/6/15.
 */
public interface RemotingTransporter {

//    RemotingServer getRemotingServer(AppContext appContext, RemotingServerConfig remotingServerConfig);
//
//    RemotingClient getRemotingClient(AppContext appContext, RemotingClientConfig remotingClientConfig);

    RemotingServer getRemotingServer(RemotingServerConfig remotingServerConfig);

    RemotingClient getRemotingClient(RemotingClientConfig remotingClientConfig);


}