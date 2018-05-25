package cn.hylexus.jt808.service.baidu.trace.model;

/**
 * 基础响应信息类
 * 
 * @author baidu
 *
 */
public abstract class BaseResponse {

    /**
     * 响应ID，与请求ID对应
     */
    private long responseID;

    /**
     * 响应状态码
     * 
     * 参考{@link StatusCodes}
     * 
     * <a href="http://lbsyun.baidu.com/index.php?title=webapi/errorcode" target =_blank>控制服务返回码</a>
     * 
     * <a href="http://lbsyun.baidu.com/index.php?title=yingyan/api/v3/appendix" target=_blank>鹰眼服务错误码</a>
     * 
     * 
     */
    private int status;

    /**
     * 响应状态消息
     */
    private String message;

    /**
     * 获取响应状态码
     * 
     * @return
     */
    public long getResponseID() {
        return responseID;
    }

    /**
     * 设置响应状态码
     * 
     * @param responseID
     */
    public void setResponseID(long responseID) {
        this.responseID = responseID;
    }

    /**
     * 获取响应状态消息
     * 
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置响应状态码
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取响应消息
     * 
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @param responseID 响应ID（与请求ID对应）
     * @param status 响应状态
     * @param message 响应消息
     */
    public BaseResponse(long responseID, int status, String message) {
        super();
        this.responseID = responseID;
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseResponse [responseID=" + responseID + ", status=" + status + ", message=" + message + "]";
    }

}
