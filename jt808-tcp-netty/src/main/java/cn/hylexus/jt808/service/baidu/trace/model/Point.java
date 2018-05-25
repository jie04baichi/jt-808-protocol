package cn.hylexus.jt808.service.baidu.trace.model;

/**
 * 坐标点信息
 * 
 * @author baidu
 *
 */
public class Point {

    /**
     * 位置坐标
     */
    private LatLng location;

    /**
     * 坐标类型
     */
    private CoordType coordType;

    /**
     * 定位精度（单位：m）
     */
    protected double radius;

    /**
     * 定位时间
     */
    protected long locTime;

    /**
     * 方向（范围为[0,359]，0度为正北方向，顺时针）
     */
    protected int direction;

    /**
     * 速度（单位：km/h）
     */
    protected double speed;

    /**
     * 高度（单位：m）
     */
    protected double height;

    /**
     * 获取位置坐标
     * 
     * @return
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * 设置位置坐标
     * 
     * @param location
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }

    /**
     * 获取坐标类型
     * 
     * @return
     */
    public CoordType getCoordType() {
        return coordType;
    }

    /**
     * 设置坐标类型
     * 
     * @param coordType
     */
    public void setCoordType(CoordType coordType) {
        this.coordType = coordType;
    }

    /**
     * 获取定位精度
     * 
     * @return
     */
    public double getRadius() {
        return radius;
    }

    /**
     * 设置定位精度
     * 
     * @param radius
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * 获取定位时间
     * 
     * @return
     */
    public long getLocTime() {
        return locTime;
    }

    /**
     * 设置定位时间
     * 
     * @param locTime
     */
    public void setLocTime(long locTime) {
        this.locTime = locTime;
    }

    /**
     * 获取方向
     * 
     * @return
     */
    public int getDirection() {
        return direction;
    }

    /**
     * 设置方向
     * 
     * @param direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * 获取速度
     * 
     * @return
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * 设置速度
     * 
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * 获取高度
     * 
     * @return
     */
    public double getHeight() {
        return height;
    }

    /**
     * 获取高度
     * 
     * @param height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    public Point() {
        super();
    }

    public Point(LatLng location, CoordType coordType, double radius, long locTime, int direction, double speed,
            double height) {
        super();
        this.location = location;
        this.coordType = coordType;
        this.radius = radius;
        this.locTime = locTime;
        this.direction = direction;
        this.speed = speed;
        this.height = height;
    }

    @Override
    public String toString() {
        return "Point [location=" + location + ", coordType=" + coordType + ", radius=" + radius + ", locTime="
                + locTime + ", direction=" + direction + ", speed=" + speed + ", height=" + height + "]";
    }

}
