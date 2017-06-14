package cn.panda.remoting.protocol.command;

import cn.panda.remoting.RemotingCommandBody;
import cn.panda.remoting.protocol.RemotingCommandFieldCheckException;

public class AbstractRemotingCommandBody implements RemotingCommandBody{

    private static final long serialVersionUID = -8184979792935677160L;

    private String identity;

    @Override
    public void checkFields() throws RemotingCommandFieldCheckException {

    }
}
