package cn.hylexus.jt808.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.hylexus.jt808.common.PackageData.MsgHeader;
import cn.hylexus.jt808.util.HttpUtils;
import cn.hylexus.jt808.util.BaiduSnCal;
import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;

public class BaiduUploadService {
	
	private final static Logger log = LoggerFactory.getLogger(BaiduUploadService.class);
	
	public static void addpoint(MsgHeader header,LocationInfoUploadMsg locationInfoUploadMsg) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Map<String,Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("ak", "aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI");
		paramMap.put("service_id", "162014");
		paramMap.put("entity_name", header.getTerminalPhone());
		paramMap.put("latitude", String.valueOf(locationInfoUploadMsg.getLatitude()/1000000.00));
		paramMap.put("longitude", String.valueOf(locationInfoUploadMsg.getLongitude()/1000000.00));
		paramMap.put("loc_time", String.valueOf(locationInfoUploadMsg.getTime().getTime()/1000));
		paramMap.put("coord_type_input", "wgs84");
		paramMap.put("speed", String.valueOf(locationInfoUploadMsg.getSpeed()/10.00));
		paramMap.put("direction", String.valueOf(locationInfoUploadMsg.getDirection()));
		paramMap.put("height", String.valueOf(locationInfoUploadMsg.getElevation()));
		String sn = BaiduSnCal.work("/api/v3/track/addpoint", paramMap);
		paramMap.put("sn", sn);
		String baidu_api_url = "http://yingyan.baidu.com/api/v3/track/addpoint";
		String result = invoke_baidu_http(baidu_api_url, paramMap,HTTP_TYPE.POST);
		log.info("BaiduUploadService:addpoint = {}", result);
	}
	public static  String geocoder_location(double longitude,double latitude) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		//http://api.map.baidu.com/geocoder/v2/?
		//location=39.934%2C116.329&output=json&ak=aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI&sn=1b8f3a698cd1002588beef23e73f2284
		Map<String,Object> paramMap = new LinkedHashMap<String, Object>();
		String location = latitude+","+longitude;
		paramMap.put("location", location);
		paramMap.put("output", "json");
		paramMap.put("ak", "aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI");

		String sn = BaiduSnCal.work("/geocoder/v2/", paramMap);
		paramMap.put("sn", sn);
		String baidu_api_url = "http://api.map.baidu.com/geocoder/v2/";
		String result = invoke_baidu_http(baidu_api_url, paramMap,HTTP_TYPE.GET);
		log.info("BaiduGpsService:staypoint = {}", result);
		JSONObject resultobject = JSONObject.parseObject(result);
		
		if (resultobject.getInteger("status") == 0) {
			return resultobject.getJSONObject("result").getString("formatted_address");
		}
		return null;
		
	}
	/*
	 * 调用百度http请求,当百度内部出现异常,重试三次
	 */
	private static String invoke_baidu_http(String url, Map params, HTTP_TYPE type){
		
		String result = null;
		boolean work = true;
		int count = 1; //不进行重试
		while (work&&count>0) {
			if (type == HTTP_TYPE.GET) {
				result = HttpUtils.httpGet(url, params);
			}else {
				result = HttpUtils.httpPost(url, params);
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
