package cn.hylexus.jt808.service.baidu.trace.model;

/**
 * 经纬度坐标
 * 
 * @author baidu
 *
 */
public class LatLng {

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 获取纬度
     * 
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 设置纬度
     * 
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取经度
     * 
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 设置经度
     * 
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng() {
        super();
    }

    /**
     * 
     * @param latitude 纬度
     * @param longitude 经度
     */
    public LatLng(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LatLng [latitude=" + latitude + ", longitude=" + longitude + "]";
    }

}
