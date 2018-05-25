package cn.hylexus.jt808.service.baidu.trace.model;

/**
 * 基础请求信息类
 * 
 * @author baidu
 *
 */
public abstract class BaseRequest {

    /**
     * 请求ID
     */
    private long requestID;

    /**
     * 服务端ak
     */
    private String ak;
    /**
     * 轨迹服务ID
     */
    private long serviceId;

    /**
     * 获取请求ID
     * 
     * @return
     */
    public long getRequestID() {
        return requestID;
    }

    /**
     * 设置请求ID
     * 
     * @param requestID
     */
    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    /**
     * 获取服务端AK
     * 
     * @return
     */
    public String getAk() {
        return ak;
    }

    /**
     * 设置服务端AK
     * 
     * @param ak
     */
    public void setAk(String ak) {
        this.ak = ak;
    }

	/**
     * 获取轨迹服务ID
     * 
     * @return
     */
    public long getServiceId() {
        return serviceId;
    }

    /**
     * 设置轨迹服务ID
     * 
     * @param serviceId
     */
    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public BaseRequest() {
        super();
    }

    /**
     * 
     * @param requestID 请求ID
     * @param ak 服务端AK
     * @param serviceId 轨迹服务ID
     */
    public BaseRequest(long requestID, String ak, long serviceId) {
        super();
        this.requestID = requestID;
        this.ak = ak;
        this.serviceId = serviceId;
    }
    @Override
    public String toString() {
        return "BaseRequest [requestID=" + requestID + ", ak=" + ak + ", serviceId=" + serviceId + "]";
    }

}
