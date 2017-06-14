package cn.panda.remoting;

import cn.panda.remoting.protocol.RemotingCommandFieldCheckException;

import java.io.Serializable;

/**
 * RemotingCommand中自定义字段反射对象的公共接口
 */
public interface RemotingCommandBody extends Serializable{

    public void checkFields() throws RemotingCommandFieldCheckException;
}
