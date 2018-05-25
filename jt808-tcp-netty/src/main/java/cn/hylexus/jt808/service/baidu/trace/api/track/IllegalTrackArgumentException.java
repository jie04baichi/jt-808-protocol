package cn.hylexus.jt808.service.baidu.trace.api.track;

/**
 * 非法的track参数异常
 * 
 * @author baidu
 *
 */
public class IllegalTrackArgumentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalTrackArgumentException() {
        super();
    }

    public IllegalTrackArgumentException(String message) {
        super(message);
    }

}
