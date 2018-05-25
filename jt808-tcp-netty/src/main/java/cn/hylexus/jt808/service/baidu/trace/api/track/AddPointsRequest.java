package cn.hylexus.jt808.service.baidu.trace.api.track;

import java.util.List;
import java.util.Map;

import cn.hylexus.jt808.service.baidu.trace.model.BaseRequest;
import cn.hylexus.jt808.service.baidu.trace.model.TrackPoint;


/**
 * 批量添加轨迹点请求
 * 
 * @author baidu
 *
 */
public final class AddPointsRequest extends BaseRequest {

    /**
     * 轨迹点集
     * 
     * key为entityName，value为该entityName的轨迹点集
     */
    private Map<String, List<TrackPoint>> trackPoints;

    /**
     * 获取轨迹点集
     * 
     * @return
     */
    public Map<String, List<TrackPoint>> getTrackPoints() {
        return trackPoints;
    }

    /**
     * 设置轨迹点集
     * 
     * @param trackPoints
     */
    public void setTrackPoints(Map<String, List<TrackPoint>> trackPoints) {
        this.trackPoints = trackPoints;
    }

    public AddPointsRequest() {
        super();
    }

    /**
     * 
     * @param requestID 请求ID
     * @param ak 服务端AK
     * @param serviceId 轨迹服务ID
     * @param trackPoints 轨迹点集
     */
    public AddPointsRequest(long requestID, String ak, long serviceId, Map<String, List<TrackPoint>> trackPoints) {
        super(requestID, ak, serviceId);
        this.trackPoints = trackPoints;
    }
    @Override
    public String toString() {
        return "AddPointsRequest [trackPoints=" + trackPoints + "]";
    }

}
