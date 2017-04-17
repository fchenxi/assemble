package cn.panda.master;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

public abstract class AbstractMaster<T, V, R> {
    protected volatile Set<? extends SlaveSpec<T, V>> slaves;

    // subtask dispatch strategy
    private volatile SubTaskDispatchStrategy<T, V> dispatchStrategy;

    public AbstractMaster() {
    }
    protected void init() {
        slaves = this.createSlaves();
        dispatchStrategy = newSubTaskDispatchStrategy();
        for (SlaveSpec<T, V> slave : slaves) {
            slave.init();
        }
    }
    protected R service(Object... params) throws Exception {
        final TaskDivideStrategy<T> taskDivideStrategy =
                newTaskDivideStrtegy(params);
        Iterator<Future<V>> subResults = dispatchStrategy.dispatch(slaves, taskDivideStrategy);
        for (SlaveSpec<T, V> slave : slaves) {
            slave.shutdown();
        }
        R result = combineResults(subResults);
        return result;
    }

    /**
     * create task divide strategy, round-robin default.
     * @param params
     */
    protected abstract TaskDivideStrategy<T> newTaskDivideStrtegy(Object... params);

    protected SubTaskDispatchStrategy<T, V> newSubTaskDispatchStrategy() {
        return new RoundRobinSubTaskDispatchStrategy<T, V>();
    }

    protected abstract Set<? extends SlaveSpec<T, V>> createSlaves();

    protected abstract R combineResults(Iterator<Future<V>> subResult);

}
