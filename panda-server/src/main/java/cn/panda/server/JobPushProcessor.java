package cn.panda.server;

import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingCommand;
import cn.panda.remoting.exception.RemotingCommandException;

public class JobPushProcessor extends AbstractProcessor {
    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        return null;
    }
}
