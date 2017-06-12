package cn.panda.remoting.codec;


import cn.panda.remoting.serialize.JavaSerializable;
import cn.panda.remoting.serialize.RemotingSerializable;

/**
 * @author Robert HG (254963746@qq.com) on 11/6/15.
 */
public abstract class AbstractCodec implements Codec {

    protected RemotingSerializable getRemotingSerializable(int serializableTypeId) {
        return new JavaSerializable();
    }

}
