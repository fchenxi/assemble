package cn.panda.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @usage       POI Excel标题annotation
 * @author      tong.cx
 * @version     0.1.0
 * @datetime    2016/3/28 18:42
 * @copyright   wonhigh.cn
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelResources {
    /**
     * 属性的标题名称
     *
     * @return
     */
    String title();

    /**
     * 在excel的顺序
     *
     * @return
     */
    int order() default 9999;
}

