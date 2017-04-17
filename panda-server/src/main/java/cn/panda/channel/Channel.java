package cn.panda.channel;

public interface Channel<P> {

    P take() throws InterruptedException;

    void put(P product) throws InterruptedException;
}
