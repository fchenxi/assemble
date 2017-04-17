package cn.panda.channel;

import java.util.concurrent.BlockingQueue;

public class WorkStealingChannel<T> implements WorkStealingEnabledChannel<T>{
    // The managed queue
    private final BlockingQueue<T>[] managedQueues;

    public WorkStealingChannel(BlockingQueue<T>[] managedQueues) {
        this.managedQueues = managedQueues;
    }

    @Override
    public T take(BlockingQueue<T> preferredQueue) throws InterruptedException {
        BlockingQueue<T> targetQueue = preferredQueue;
        T product = null;
        if(targetQueue != null){
            product = targetQueue.poll();
        }
        int queueIndex = -1;
        while(product == null){
            queueIndex = (queueIndex + 1) % managedQueues.length;
            targetQueue = managedQueues[queueIndex];
            product = targetQueue.poll();
            if(targetQueue == preferredQueue){
                break;
            }
        }
        if(product == null){
            queueIndex = (int)(System.currentTimeMillis() % managedQueues.length);
            targetQueue = managedQueues[queueIndex];
            product = targetQueue.take();
            System.err.println("stealed from " + queueIndex + " : " + product);
        }
        return product;
    }

    @Override
    public T take() throws InterruptedException {
        return take(null);
    }

    @Override
    public void put(T product) throws InterruptedException {
        int targetIndex = (product.hashCode()) % managedQueues.length;
        BlockingQueue<T> targetQueue = managedQueues[targetIndex];
        targetQueue.put(product);
    }
}
