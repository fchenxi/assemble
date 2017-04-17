package cn.panda;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {
    private static ThreadLocal<SimpleDateFormat> sdfWrapper = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }

    };

    public static void info(String message) {
        SimpleDateFormat sdf = sdfWrapper.get();
        System.out.println('[' + sdf.format(new Date()) + "][INFO][" + Thread.currentThread().getName() + "]:" + message);
    }
}