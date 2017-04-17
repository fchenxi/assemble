package cn.panda.common.hbase;

public interface RowReducer<T> {

    
    T reduce(T map) throws Exception;
}
