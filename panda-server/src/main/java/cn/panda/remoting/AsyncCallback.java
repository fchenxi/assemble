package cn.panda.remoting;


/**
 * 异步调用应答回调接口
 */
public interface AsyncCallback {

    void operationComplete(final ResponseFuture responseFuture);

}
