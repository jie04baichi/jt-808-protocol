package cn.hylexus.jt808.service.baidu.trace.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 通用工具类
 * 
 * @author baidu
 *
 */
public class CommonUtils {

    /**
     * 将异常信息转成字符串
     * 
     * @param ex
     * @return
     */
    public static String transExceptionToString(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * 字符串是否为null或空串
     * 
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        if (null == str || "".equals(str)) {
            return true;
        }
        return false;
    }

}
