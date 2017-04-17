package cn.panda.channel;

import java.util.concurrent.BlockingQueue;

public interface WorkStealingEnabledChannel<P> extends Channel<P>{

    P take(BlockingQueue<P> preferredQueue) throws InterruptedException;

}
