package cn.hylexus.jt808.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.common.PackageData.MsgHeader;
import cn.hylexus.jt808.util.HttpUtils;
import cn.hylexus.jt808.util.SnCal;
import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;

public class BaiduUploadService {
	
	private final static Logger log = LoggerFactory.getLogger(BaiduUploadService.class);
	
	public static void addpoint(MsgHeader header,LocationInfoUploadMsg locationInfoUploadMsg) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Map<String,Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("ak", "aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI");
		paramMap.put("service_id", "162014");
		paramMap.put("entity_name", "彭杰gps设备");
		paramMap.put("latitude", String.valueOf(locationInfoUploadMsg.getLatitude()/1000000.00));
		paramMap.put("longitude", String.valueOf(locationInfoUploadMsg.getLongitude()/1000000.00));
		paramMap.put("loc_time", String.valueOf(locationInfoUploadMsg.getTime().getTime()/1000));
		paramMap.put("coord_type_input", "wgs84");
		paramMap.put("speed", String.valueOf(locationInfoUploadMsg.getSpeed()/10.00));
		paramMap.put("direction", String.valueOf(locationInfoUploadMsg.getDirection()));
		paramMap.put("height", String.valueOf(locationInfoUploadMsg.getElevation()));
		String sn = SnCal.work("/api/v3/track/addpoint", paramMap);
		paramMap.put("sn", sn);
		String baidu_api_url = "http://yingyan.baidu.com/api/v3/track/addpoint";
		String result = HttpUtils.httpPost(baidu_api_url, paramMap);
		log.info("BaiduUploadService:addpoint = {}", result);
	}
}
