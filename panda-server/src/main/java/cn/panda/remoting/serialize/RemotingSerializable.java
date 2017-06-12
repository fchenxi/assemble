package cn.panda.remoting.serialize;

/**
 * @author Robert HG (254963746@qq.com) on 11/6/15.
 */
public interface RemotingSerializable {

    int getId();

    byte[] serialize(final Object obj) throws Exception;

    <T> T deserialize(final byte[] data, Class<T> clazz) throws Exception;
}
