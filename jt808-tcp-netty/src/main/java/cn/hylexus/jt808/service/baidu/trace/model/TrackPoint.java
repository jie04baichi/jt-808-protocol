package cn.hylexus.jt808.service.baidu.trace.model;

import java.util.Map;

/**
 * 轨迹点信息
 * 
 * @author baidu
 *
 */
public class TrackPoint extends Point {

    /**
     * 对象数据名称
     */
    private String objectKey;

    /**
     * track自定义字段
     */
    private Map<String, String> columns;

    /**
     * 获取对象数据名称
     * 
     * @return
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象数据名称
     * 
     * @param objectKey
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * 获取自定义属性
     * 
     * @return
     */
    public Map<String, String> getColumns() {
        return columns;
    }

    /**
     * 设置自定义属性
     * 
     * @param columns
     */
    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public TrackPoint() {
        super();
    }

    /**
     * 
     * @param location 经纬度坐标
     * @param coordType 坐标类型
     * @param radius 定位精度
     * @param locTime 定位时间
     * @param direction 方向
     * @param speed 速度
     * @param height 高度
     * @param objectKey 对象数据名称
     * @param columns 自定义属性
     */
    public TrackPoint(LatLng location, CoordType coordType, double radius, long locTime, int direction, double speed,
            double height, String objectKey, Map<String, String> columns) {
        super(location, coordType, radius, locTime, direction, speed, height);
        this.objectKey = objectKey;
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "TrackPoint [radius=" + radius + ", locTime=" + locTime + ", direction=" + direction + ", speed=" + speed
                + ", height=" + height + ", objectKey=" + objectKey + ", columns=" + columns + "]";
    }

}
