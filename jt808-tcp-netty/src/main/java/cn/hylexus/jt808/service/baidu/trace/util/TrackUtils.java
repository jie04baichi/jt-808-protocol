package cn.hylexus.jt808.service.baidu.trace.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import cn.hylexus.jt808.service.baidu.trace.api.track.IllegalTrackArgumentException;
import cn.hylexus.jt808.service.baidu.trace.model.LatLng;
import cn.hylexus.jt808.service.baidu.trace.model.TrackPoint;


/**
 * track工具类
 * 
 * @author baidu
 *
 */
public class TrackUtils {

    /**
     * 检查经纬度
     * 
     * @param latLng
     * @return
     */
    public static boolean checkLatLng(LatLng latLng) {
        if (null == latLng) {
            throw new IllegalTrackArgumentException("latLng can not be null");
        }

        if (Math.abs(latLng.getLatitude()) > 90 || Math.abs(latLng.getLongitude()) > 180) {
            throw new IllegalTrackArgumentException("latLng is invalid value");
        }

        return true;
    }

    /**
     * 组装单个轨迹点
     * 
     * @param trackPoint
     * @param parameters
     */
    public static void packPoint(TrackPoint trackPoint, StringBuilder parameters) {
        LatLng location = trackPoint.getLocation();
        if (TrackUtils.checkLatLng(location)) {
            parameters.append("&latitude=").append(location.getLatitude());
            parameters.append("&longitude=").append(location.getLongitude());
        }

        parameters.append("&loc_time=").append(trackPoint.getLocTime());

        if (null == trackPoint.getCoordType()) {
            throw new IllegalTrackArgumentException("coordType can not be null.");
        }

        parameters.append("&coord_type_input=").append(trackPoint.getCoordType().name());
        parameters.append("&speed=").append(trackPoint.getSpeed());
        parameters.append("&direction=").append(trackPoint.getDirection());
        parameters.append("&height=").append(trackPoint.getHeight());
        parameters.append("&radius=").append(trackPoint.getRadius());

        if (!CommonUtils.isNullOrEmpty(trackPoint.getObjectKey())) {
            parameters.append("&_obejct_key=").append(HttpUtils.urlEncode(trackPoint.getObjectKey()));
        }

        if (null != trackPoint.getColumns()) {
            packColumns(trackPoint.getColumns(), parameters);
        }
    }

    /**
     * 组装轨迹点列表
     *
     * @param points 轨迹点列表，Map中key为entityName， value为轨迹点集
     * @param parameters json格式轨迹点信息
     */
    public static int packPoints(Map<String, List<TrackPoint>> points, StringBuilder parameters) {
        if (null == points) {
            throw new IllegalTrackArgumentException("track points can not be null.");
        }
        JSONArray jsonArray = new JSONArray();
        int pointsCount = 0;
        try {
            Set<Map.Entry<String, List<TrackPoint>>> entries = points.entrySet();
            for (Map.Entry<String, List<TrackPoint>> entry : entries) {
                String entityName = entry.getKey();
                List<TrackPoint> trackPoints = entry.getValue();
                if (null == trackPoints) {
                    continue;
                }
                pointsCount += trackPoints.size();
                for (TrackPoint trackPoint : trackPoints) {
                    if (null == trackPoint) {
                        continue;
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("entity_name", entityName);
                    jsonObject.put("loc_time", trackPoint.getLocTime());

                    LatLng location = trackPoint.getLocation();
                    if (TrackUtils.checkLatLng(location)) {
                        jsonObject.put("latitude", location.getLatitude());
                        jsonObject.put("longitude", location.getLongitude());
                    }

                    if (null == trackPoint.getCoordType()) {
                        throw new IllegalTrackArgumentException("coordType can not be null.");
                    }
                    jsonObject.put("coord_type_input", trackPoint.getCoordType().name());
                    jsonObject.put("speed", trackPoint.getSpeed());
                    jsonObject.put("direction", trackPoint.getDirection());
                    jsonObject.put("height", trackPoint.getHeight());
                    jsonObject.put("radius", trackPoint.getRadius());

                    Map<String, String> columns = trackPoint.getColumns();
                    if (null != columns) {
                        packColumns(columns, jsonObject);
                    }
                    jsonArray.add(jsonObject);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        parameters.append("&point_list=").append(jsonArray.toString());
        return pointsCount;
    }

    /**
     * 组装自定义属性（单个轨迹点）
     * 
     * @param columns
     * @param parameters
     */
    public static void packColumns(Map<String, String> columns, StringBuilder parameters) {
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            parameters.append("&").append(entry.getKey()).append("=").append(HttpUtils.urlEncode(entry.getValue()));
        }
    }

    /**
     * 组装自定义属性（批量轨迹点）
     *
     * @param columns 自定义属性
     * @param paramsMap 参数集
     * @param jsonObject json对象
     *
     * @return
     */
    private static void packColumns(Map<String, String> columns, JSONObject jsonObject) {
        for (Map.Entry<String, String> column : columns.entrySet()) {
            if (StringUtils.isNullOrEmpty(column.getKey()) || StringUtils.isNullOrEmpty(column.getValue())) {
                continue;
            }
            try {
                jsonObject.put(column.getKey(), HttpUtils.urlEncode(column.getValue()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
