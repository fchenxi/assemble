package cn.panda.web.filter;

/**
 * @author HyunGil Jeong
 */
public interface TimestampFilter {
    boolean filter(long timestamp);
}