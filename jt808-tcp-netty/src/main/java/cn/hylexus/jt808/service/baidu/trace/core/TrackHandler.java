package cn.hylexus.jt808.service.baidu.trace.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.hylexus.jt808.service.baidu.trace.LBSTraceClient;
import cn.hylexus.jt808.service.baidu.trace.api.track.AddPointRequest;
import cn.hylexus.jt808.service.baidu.trace.api.track.AddPointsRequest;
import cn.hylexus.jt808.service.baidu.trace.api.track.IllegalTrackArgumentException;
import cn.hylexus.jt808.service.baidu.trace.api.track.UploadResponse;
import cn.hylexus.jt808.service.baidu.trace.model.BaseRequest;
import cn.hylexus.jt808.service.baidu.trace.model.BaseResponse;
import cn.hylexus.jt808.service.baidu.trace.model.OnUploadListener;
import cn.hylexus.jt808.service.baidu.trace.model.StatusCodes;
import cn.hylexus.jt808.service.baidu.trace.model.TrackPoint;
import cn.hylexus.jt808.service.baidu.trace.util.CommonUtils;
import cn.hylexus.jt808.service.baidu.trace.util.HttpUtils;
import cn.hylexus.jt808.service.baidu.trace.util.TrackUtils;

/**
 * Track处理器
 * 
 * @author baidu
 *
 */
public class TrackHandler {
	private static Logger logger = LoggerFactory.getLogger(TrackHandler.class);

    /**
     * 添加单个轨迹点
     * 
     * @param request
     */
    public static void addPoint(AddPointRequest request) {
        StringBuilder parameters = new StringBuilder();
        packRequest(UrlDomain.ACTION_ADD_POINT, request, parameters);
        AsyncRequestClient.getInstance().submitTask(request.getRequestID(), UrlDomain.ACTION_ADD_POINT,
                parameters.toString(), 1, HttpClient.METHOD_POST);
    }

    /**
     * 批量添加轨迹点
     * 
     * @param request
     */
    public static void addPoints(AddPointsRequest request) {
        StringBuilder parameters = new StringBuilder();
        int size = packRequest(UrlDomain.ACTION_ADD_POINTS, request, parameters);
        AsyncRequestClient.getInstance().submitTask(request.getRequestID(), UrlDomain.ACTION_ADD_POINTS,
                parameters.toString(), size, HttpClient.METHOD_POST);
    }

    /**
     * 组装请求
     * 
     * @return
     */
    public static int packRequest(String action, BaseRequest request,StringBuilder parameters) {
        if (null == request) {
            throw new IllegalTrackArgumentException("request can not be null.");
        }

        packCommonRequest(request, parameters);

        int pointCount = 1;
        
        if (request instanceof AddPointRequest) {
            AddPointRequest addPointRequest = (AddPointRequest) request;
            parameters.append("&entity_name=").append(HttpUtils.urlEncode(addPointRequest.getEntityName()));
            TrackPoint trackPoint = addPointRequest.getTrackPoint();
            if (null == trackPoint) {
                throw new IllegalTrackArgumentException("trackPoint can not be null.");
            }
            TrackUtils.packPoint(trackPoint, parameters);
        } else {
            AddPointsRequest addPointsRequest = (AddPointsRequest) request;
            pointCount = TrackUtils.packPoints(addPointsRequest.getTrackPoints(), parameters);
        }
        logger.debug("parameters = "+ parameters.toString());
        return pointCount;
    }

    /**
     * 组装公共请求
     * 
     * @param request
     * @param parameters
     */
    private static void packCommonRequest(BaseRequest request, StringBuilder parameters) {
        if (CommonUtils.isNullOrEmpty(request.getAk())) {
            throw new IllegalTrackArgumentException("ak can not be null or empty string.");
        }

        if (request.getServiceId() <= 0) {
            throw new IllegalTrackArgumentException("serviceId is lower than 0.");
        }
        parameters.append("ak=").append(request.getAk());
        parameters.append("&service_id=").append(request.getServiceId());
    }

    /**
     * 解析响应
     * 
     * @param requestId 响应对应的请求ID
     * @param action 响应对应的请求action
     * @param result 响应结果
     */
    public static void parseResponse(long requestId, String action, String result) {
        List<OnUploadListener> listeners = LBSTraceClient.getInstance().uploadListeners;
        UploadResponse response = new UploadResponse(requestId, StatusCodes.SUCCESS, StatusCodes.MSG_SUCCESS);
        parseCommonResponse(response, result);
        for (OnUploadListener listener : listeners) {
            if (response.getStatus() == StatusCodes.SUCCESS) {
                listener.onSuccess(requestId);
            } else {
                listener.onFailed(response);
            }
        }
    }

    /**
     * 解析通用响应
     * 
     * @param response
     * @param result
     */
    private static void parseCommonResponse(BaseResponse response, String result) {
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(result);
            if (jsonObject.containsKey("status")) {
                response.setStatus(jsonObject.getIntValue("status"));
            }
            if (jsonObject.containsKey("message")) {
                response.setMessage(jsonObject.getString("message"));
            }
        } catch (Exception e) {
            response.setStatus(StatusCodes.PARSE_FAILED);
            response.setMessage(StatusCodes.MSG_PARSE_FAILED);
        }
    }

}
