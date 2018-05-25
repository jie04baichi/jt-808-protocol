package cn.hylexus.jt808.service.baidu.trace.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * http工具类
 * 
 * @author baidu
 *
 */
public class HttpUtils {

    /**
     * 编码
     */
    public static final String CHAR_ENC = "UTF-8";

    /**
     * url编码
     * 
     * @param parameter
     * @return
     */
    public static String urlEncode(String parameter) {
        try {
            return URLEncoder.encode(parameter, CHAR_ENC);
        } catch (UnsupportedEncodingException e) {
            return parameter;
        }
    }

}
