package cn.panda.channel;

import cn.panda.consumer.AbstractTerminatableThread;
import cn.panda.consumer.TerminationToken;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class WorkStealingExample {
    private final WorkStealingEnabledChannel<String> channel;
    private final TerminationToken token = new TerminationToken();

    public WorkStealingExample(){
        int nCPU = Runtime.getRuntime().availableProcessors();
        int consumerCount = nCPU / 2 + 1;

        BlockingDeque<String>[] managedQueues = new LinkedBlockingDeque[consumerCount];
        channel = new WorkStealingChannel<String>(managedQueues);

        Consumer[] consumers = new Consumer[consumerCount];
        for(int i = 0; i < consumerCount; i++){
            managedQueues[i] = new LinkedBlockingDeque<String>();
            consumers[i] = new Consumer(token, managedQueues[i]);
        }
        for(int i = 0; i < nCPU; i++){
            new Producer().start();
        }
        for(int i = 0; i < consumerCount; i++){
            consumers[i].start();
        }
    }

    private class Producer extends AbstractTerminatableThread{
        private int i = 0;

        @Override
        protected void doRun() throws Exception {
            String product = String.valueOf(i++);
            channel.put(product);
            System.out.println("produce product: " + product);
            token.reservations.incrementAndGet();
        }
    }

    private class Consumer extends AbstractTerminatableThread{
        private final BlockingDeque<String> workQueue;

        public Consumer(TerminationToken token, BlockingDeque<String> workQueue) {
            super(token);
            this.workQueue = workQueue;
        }

        @Override
        protected void doRun() throws Exception {
            /**
             * WorkStealingEnabledChannel#take(BlockingDequepreferedQueue)method
             * archive work-steal algorithm
             */
            String product = channel.take(workQueue);
            System.out.println("consume product: " + product);
            // simulate long-time job
            try{
                Thread.sleep(new Random().nextInt(50));
            }finally {
                token.reservations.decrementAndGet();
            }
        }
    }
    public void doSomething(){
    }
    public static void main(String[] args) throws InterruptedException {
        WorkStealingExample ws = new WorkStealingExample();
        ws.doSomething();
        Thread.sleep(3500);
    }
}
