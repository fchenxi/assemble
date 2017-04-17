package cn.panda.master;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

public interface SubTaskDispatchStrategy<T, V> {
    Iterator<Future<V>> dispatch(Set<? extends SlaveSpec<T, V>> slaves,
                                 TaskDivideStrategy<T> taskDivideStrategy) throws InterruptedException;
}
