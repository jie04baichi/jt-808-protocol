package cn.hylexus.jt808.service.baidu.trace.model;

/**
 * 状态码
 * 
 * @author baidu
 *
 */
public class StatusCodes {

    /**
     * 解析失败
     */
    public static final int PARSE_FAILED = -2;

    /**
     * IO异常
     */
    public static final int IO_EXCEPTION = -1;

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    public static final String MSG_PARSE_FAILED = "解析失败";

    public static final String MSG_SUCCESS = "success";

}
