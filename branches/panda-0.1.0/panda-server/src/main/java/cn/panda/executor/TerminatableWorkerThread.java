package cn.panda.executor;

import cn.panda.consumer.AbstractTerminatableThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TerminatableWorkerThread<T, V> extends AbstractTerminatableThread {
    private final BlockingQueue<Runnable> workQueue;
    private final TaskProcessor<T, V> taskProcessor;

    public TerminatableWorkerThread(BlockingQueue<Runnable> workQueue, TaskProcessor<T, V> taskProcessor) {
        this.workQueue = workQueue;
        this.taskProcessor = taskProcessor;
    }

    public Future<V> submit(final T task) throws InterruptedException{
        Callable<V> callable = new Callable<V>() {
            @Override
            public V call() throws Exception {
                return taskProcessor.doProcess(task);
            }
        };
        FutureTask<V> ft = new FutureTask<V>(callable);
        workQueue.put(ft);
        terminationToken.reservations.incrementAndGet();
        return ft;
    }
    @Override
    protected void doRun() throws Exception {
        Runnable ft = workQueue.take();
        try  {
            ft.run();
        } finally {
            terminationToken.reservations.decrementAndGet();
        }
    }
}
