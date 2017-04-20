package cn.panda.common.hbase.server.bo.serializer;

/**
 * @author Woonduk Kang(emeroad)
 */
public interface RowKeyEncoder<V> {

    byte[] encodeRowKey(V value);

}
