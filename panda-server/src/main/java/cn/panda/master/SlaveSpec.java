package cn.panda.master;

import java.util.concurrent.Future;

public interface SlaveSpec<T, V> {
    /**
     * submit the task to master.
     * @param task
     * @return slave`s promise result
     * @throws InterruptedException
     */
    Future<V> submit(final T task) throws InterruptedException;

    /**
     * init the slave service
     */
    void init();

    /**
     * stop slave service
     */
    void shutdown();
}
