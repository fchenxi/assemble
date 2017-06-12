package cn.panda.consumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server extends AbstractTerminatableThread {

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(
            100);

    private int i = 0;

    @Override
    protected void doRun() throws Exception {
        String product = String.valueOf(i++);
        queue.put(product);
        System.out.println("produce product: " + product);
    }
}