package cn.panda.master;

public interface TaskDivideStrategy<T> {
    /**
     * Return the next task, it pressent no task if return null.
     * @return next task
     */
    T nextChunk();
}
