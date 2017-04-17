package cn.panda.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public abstract class AbstractSerializer<T, V> {
    private final TerminatableWorkerThread<T, V> workerThread;

    public AbstractSerializer(BlockingQueue<Runnable> workQueue, TaskProcessor<T, V> taskProcessor) {
        workerThread = new TerminatableWorkerThread<T, V>(workQueue, taskProcessor);
    }
    protected abstract T makeTask(Object... params);
    protected Future<V> service(Object... params) throws InterruptedException {
        T task = makeTask(params);
        Future<V> resultPromise = workerThread.submit(task);
        return resultPromise;
    }
    public void init() {
        workerThread.start();
    }
    public void shutdown() {
        workerThread.terminate();
    }
}
