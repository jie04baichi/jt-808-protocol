package cn.hylexus.jt808.service.baidu.trace.util;

/**
 * 时间工具类
 * 
 * @author baidu
 *
 */
public class TimeUtils {

    public static final int MILLIS_TO_SECONDS = 1000;

    public static final int MILLIS_TO_MINUTES = 60 * 1000;
    
    public static final int MILLIS_TO_DAY = 24 * 60 * 60 * 1000;

    /**
     * 获取当前分钟数
     * 
     * @return
     */
    public static long getCurrentTimeOfMinutes() {
        return System.currentTimeMillis() / MILLIS_TO_MINUTES;
    }

    /**
     * 获取当前天数
     * 
     * @return
     */
    public static long getCurrentTimeOfDay() {
        return System.currentTimeMillis() / MILLIS_TO_DAY;
    }
}
