package cn.hylexus.jt808.service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import cn.hylexus.jt808.common.PackageData.MsgHeader;
import cn.hylexus.jt808.service.baidu.trace.LBSTraceClient;
import cn.hylexus.jt808.service.baidu.trace.api.track.AddPointRequest;
import cn.hylexus.jt808.service.baidu.trace.api.track.UploadResponse;
import cn.hylexus.jt808.service.baidu.trace.model.CoordType;
import cn.hylexus.jt808.service.baidu.trace.model.LatLng;
import cn.hylexus.jt808.service.baidu.trace.model.OnUploadListener;
import cn.hylexus.jt808.service.baidu.trace.model.TrackPoint;
import cn.hylexus.jt808.util.BaiduSnCal;
import cn.hylexus.jt808.util.HttpUtils;
import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;

public class BaiduUploadService {
	
	private final static Logger logger = LoggerFactory.getLogger(BaiduUploadService.class);
	
    private static AtomicLong mSequenceGenerator = new AtomicLong();

    private static AtomicInteger successCounter = new AtomicInteger();

    private static AtomicInteger failedCounter = new AtomicInteger();
    
    private static LoadingCache<String, TrackPoint> currentTrack = null;
    
    private static LBSTraceClient client = LBSTraceClient.getInstance();

    
	private  static String ak;
	private  static String service_id;
	private static final String PROPERTIES_NAME = "application.properties";
	static{
		Properties properties = new Properties();
		try {
			InputStream in = BaiduUploadService.class.getClassLoader().getResourceAsStream(PROPERTIES_NAME);
			properties.load(in);
			ak = properties.getProperty("baidu.app.ak");
			service_id = properties.getProperty("baidu.app.serviceid");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		init();
	}
	public static void init() {
        client.init();
        client.start();

        client.registerUploadListener(new OnUploadListener() {

            @Override
            public void onSuccess(long responseId) {
            	logger.info("上传成功 , MsgFlowId : " + responseId + ", successCounter : " + successCounter.incrementAndGet());
            }

            @Override
            public void onFailed(UploadResponse response) {
            	logger.info("上传失败 , MsgFlowId : " + response.getResponseID() + ", failedCounter : "
                        + failedCounter.incrementAndGet() + ", " + response);
            }
        });
        
        currentTrack = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, TrackPoint>() {
                    @Override
                    public TrackPoint load(String arg0) throws Exception {
                        return new TrackPoint();
                    }
                });
	}
	public static void addpoint(MsgHeader header,LocationInfoUploadMsg locationInfoUploadMsg) throws UnsupportedEncodingException, NoSuchAlgorithmException, ExecutionException {
		
        TrackPoint trackPoint = new TrackPoint(new LatLng(locationInfoUploadMsg.getLatitude()/1000000.00, locationInfoUploadMsg.getLongitude()/1000000.00), CoordType.wgs84, 30,
        		locationInfoUploadMsg.getTime().getTime()/1000, locationInfoUploadMsg.getDirection(), locationInfoUploadMsg.getSpeed()/10.00, locationInfoUploadMsg.getElevation(), null, null);
        
    	TrackPoint cachePoint = currentTrack.get(header.getTerminalPhone());
    	if (cachePoint.equals(trackPoint)) {
    		logger.info("设备【"+header.getTerminalPhone() + "】定位数据持续相同，不上传至百度");
			return;
		}else {
			logger.info("加入内存：currentTrack 【】 " + trackPoint);
			currentTrack.put(header.getTerminalPhone(), trackPoint);
		}

        
        AddPointRequest request1 = new AddPointRequest(header.getFlowId(), ak ,Long.valueOf(service_id),
        		header.getTerminalPhone(), trackPoint);
        client.addPoint(request1);

	}
	public static  String geocoder_location(double longitude,double latitude) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		//http://api.map.baidu.com/geocoder/v2/?
		//location=39.934%2C116.329&output=json&ak=aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI&sn=1b8f3a698cd1002588beef23e73f2284
		Map<String,String> paramMap = new LinkedHashMap<String, String>();
		String location = latitude/1000000.00+","+longitude/1000000.00;
		paramMap.put("location", location);
		paramMap.put("output", "json");
		paramMap.put("ak", ak);

		String sn = BaiduSnCal.work("/geocoder/v2/", paramMap);
		paramMap.put("sn", sn);
		String baidu_api_url = "http://api.map.baidu.com/geocoder/v2/";
		String result = invoke_baidu_http(baidu_api_url, paramMap,HTTP_TYPE.GET);
		logger.info("BaiduGpsService:staypoint = {}", result);
		JSONObject resultobject = JSONObject.parseObject(result);
		
		if (resultobject.getInteger("status") == 0) {
			return resultobject.getJSONObject("result").getString("formatted_address");
		}
		return null;
		
	}
	/*
	 * 调用百度http请求,当百度内部出现异常,重试三次
	 */
	private static String invoke_baidu_http(String url, Map<String,String> params, HTTP_TYPE type){
		
		String result = null;
		boolean work = true;
		int count = 1; //不进行重试
		while (work&&count>0) {
			if (type == HTTP_TYPE.GET) {
				result = HttpUtils.get(url, params, null);
			}else {
				result = HttpUtils.postFrom(url, params);
			}
			JSONObject resultobject = JSONObject.parseObject(result);
			//百度内部出现异常,可能是超时引起
			if (resultobject.getInteger("status") == 1) {
				count--;
				continue;
			}
			work = false;
		}
		return result;
	}
	private enum HTTP_TYPE{
		GET,
		POST
	}
}
