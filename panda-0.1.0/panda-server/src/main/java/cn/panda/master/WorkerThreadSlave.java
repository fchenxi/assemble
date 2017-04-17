package cn.panda.master;

import cn.panda.consumer.AbstractTerminatableThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class WorkerThreadSlave<T, V> extends AbstractTerminatableThread
        implements SlaveSpec<T, V> {
    private final BlockingQueue<Runnable> taskQueue;
    public WorkerThreadSlave(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public Future<V> submit(final T task) throws InterruptedException {
        FutureTask<V> ft = new FutureTask<V>(new Callable<V>() {
            @Override
            public V call() throws Exception {
                V result = null;
                try {
                    result = doProcess(task);
                } catch (Exception e){
                }
                return result;
            }
        });
        taskQueue.put(ft);
        terminationToken.reservations.incrementAndGet();
        return ft;
    }
    private SubTaskFailureException newSubTaskFailureException(final T subTask, Exception cause) {
        RetryInfo<T, V> retryInfo = new RetryInfo<T, V>(subTask, new Callable<V>() {
            @Override
            public V call() throws Exception {
                V result;
                result = doProcess(subTask);
                return result;
            }
        });
        return new SubTaskFailureException(retryInfo, cause);
    }
    protected abstract V doProcess(T task) throws Exception;

    @Override
    protected void doRun() throws Exception {
        try {
            Runnable task = taskQueue.take();
            task.run();
        } finally {
            terminationToken.reservations.decrementAndGet();
        }
    }
    @Override
    public void init() {
        start();
    }

    @Override
    public void shutdown() {
        terminate(true);
    }
}
