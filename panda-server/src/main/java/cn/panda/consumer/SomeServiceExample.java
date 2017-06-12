package cn.panda.consumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SomeServiceExample {

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(
            100);

    private final Server server = new Server();
    private final Client client = new Client();

    public void init() {
        server.start();
        client.start();
    }

    public void shutdown() {
        server.terminate(true);
        client.terminate();
    }

    public static void main(String[] args) {
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
