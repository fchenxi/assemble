package cn.panda.executor;

public interface TaskProcessor<T, V> {

    V doProcess(T task) throws Exception;

}
