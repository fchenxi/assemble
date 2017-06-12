package cn.panda.consumer;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client extends AbstractTerminatableThread {

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(
            100);

    @Override
    protected void doRun() throws Exception {
        String product = queue.take();
        System.out.println("consume product: " + product);
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } finally {
            terminationToken.reservations.decrementAndGet();
        }
    }
}