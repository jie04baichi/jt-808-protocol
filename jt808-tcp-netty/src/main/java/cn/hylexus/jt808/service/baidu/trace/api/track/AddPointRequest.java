package cn.hylexus.jt808.service.baidu.trace.api.track;

import cn.hylexus.jt808.service.baidu.trace.model.BaseRequest;
import cn.hylexus.jt808.service.baidu.trace.model.TrackPoint;

/**
 * 添加单个轨迹点请求
 * 
 * @author baidu
 *
 */
public class AddPointRequest extends BaseRequest {

    /**
     * entity标识
     */
    private String entityName;

    /**
     * 轨迹点
     */
    private TrackPoint trackPoint;

    /**
     * 获取entity标识
     * 
     * @return
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * 设置entity标识
     * 
     * @param entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * 获取轨迹点
     * 
     * @return
     */
    public TrackPoint getTrackPoint() {
        return trackPoint;
    }

    /**
     * 设置轨迹点
     * 
     * @param trackPoint
     */
    public void setTrackPoint(TrackPoint trackPoint) {
        this.trackPoint = trackPoint;
    }

    public AddPointRequest() {
        super();
    }

    /**
     * 
     * @param requestID 请求ID
     * @param ak 服务端AK
     * @param serviceId 轨迹服务ID
     * @param entityName entity标识
     * @param trackPoint 轨迹点
     */
    public AddPointRequest(long requestID, String ak, long serviceId, String entityName, TrackPoint trackPoint) {
        super(requestID, ak, serviceId);
        this.entityName = entityName;
        this.trackPoint = trackPoint;
    }

    @Override
    public String toString() {
        return "AddPointRequest [entityName=" + entityName + ", trackPoint=" + trackPoint + "]";
    }

}
