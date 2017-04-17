package cn.panda.consumer;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SomeServiceExample {
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(
            100);

    private final Producer producer = new Producer();
    private final Consumer consumer = new Consumer();
    private class Producer extends AbstractTerminatableThread{
        private int i = 0;

        @Override
        protected void doRun() throws Exception {
            String product = String.valueOf(i++);
            queue.put(product);
            System.out.println("produce product: " + product);
        }
    }
    private class Consumer extends AbstractTerminatableThread{
        @Override
        protected void doRun() throws Exception {
            String product = queue.take();
            System.out.println("consume product: " + product);
            try{
                Thread.sleep(new Random().nextInt(100));
            }catch (InterruptedException e){
                System.err.println(e.getMessage());
            }finally {
                terminationToken.reservations.decrementAndGet();
            }
        }
    }
    public void init(){
        producer.start();
        consumer.start();
    }
    public void shutdown(){
        producer.terminate(true);
        consumer.terminate();
    }
    public static void main(String[] args){
        SomeServiceExample ss = new SomeServiceExample();
        ss.init();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        ss.shutdown();
    }
}
