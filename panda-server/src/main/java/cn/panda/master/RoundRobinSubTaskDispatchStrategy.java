package cn.panda.master;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class RoundRobinSubTaskDispatchStrategy<T, V> implements
        SubTaskDispatchStrategy<T, V> {
    @Override
    public Iterator<Future<V>> dispatch(Set<? extends SlaveSpec<T, V>> slaves, TaskDivideStrategy<T> taskDivideStrategy)
            throws InterruptedException {
        final List<Future<V>> subResults = new LinkedList<Future<V>>();
        T subTask;
        Object[] arrSlaves = slaves.toArray();
        int i = -1;
        final int slaveCount = arrSlaves.length;
        Future<V> subTaskResultPromise;

        while ((subTask = taskDivideStrategy.nextChunk()) != null) {
            i = (i + 1) % slaveCount;
            subTaskResultPromise = ((WorkerThreadSlave<T, V>)arrSlaves[i]).submit(subTask);
            subResults.add(subTaskResultPromise);
        }
        return subResults.iterator();

    }
}
