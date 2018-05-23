package cn.hylexus.jt808.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class HttpUtils {
	
	private static final int CONNECT_TIMEOUT = 3000;
	private static final int READ_TIMEOUT = 2000;
	
	private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * Send a get request
	 * @param url
	 * @return response
	 * @throws IOException 
	 */
	static public String get(String url){
		return get(url, null);
	}

	/**
	 * Send a get request
	 * @param url         Url as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String get(String url,
			Map<String, String> headers){
		return get(url, null, headers);
	}
	/**
	 * Send a get request
	 * @param url         Url as string
	 * @param params      map with parameters/values
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String get(String url,Map<String, String> params,
			Map<String, String> headers)  {
		return fetch("GET", appendQueryParams(url, params), null, headers);
	}
	/**
	 * Send a post request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String post(String url, String body,
			Map<String, String> headers) {
		return fetch("POST", url, body, headers);
	}

	/**
	 * Send a post request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String post(String url, String body){
		return post(url, body, null);
	}

	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      map with parameters/values
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String postJson(String url, Map<String, Object> params) {
		return postJson(url, params, null);
	}

	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      Map with parameters/values
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String postJson(String url, Map<String, Object> params,
			Map<String, String> headers){
		// set content type
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put("Content-Type", "application/json");


		// parse parameters
		String body = "";
		if (params != null) {
			body = JSON.toJSONString(params);
		}

		return post(url, body, headers);
	}
	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      map with parameters/values
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String postFrom(String url, Map<String, String> params) {
		return postFrom(url, params, null);
	}
	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      Map with parameters/values
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String postFrom(String url, Map<String, String> params,
			Map<String, String> headers){
		// set content type
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put("Content-Type", "application/x-www-form-urlencoded");

		// parse parameters
		String body = "";
		if (params != null) {
			try {
				body = getParamString(params);
			} catch (IOException e) {
				log.error("解析body出错", e.getCause());
			}
		}

		return post(url, body, headers);
	}
	/**
	 * Send a put request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String put(String url, String body){
		return put(url, body, null);
	}
	/**
	 * Send a put request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String put(String url, String body,
			Map<String, String> headers){
		return fetch("PUT", url, body, headers);
	}

	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      Map with parameters/values
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String putJson(String url, Map<String, Object> params){
		return putJson(url, params, null);
	}
	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      Map with parameters/values
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String putJson(String url, Map<String, Object> params,
			Map<String, String> headers) {
		// set content type
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put("Content-Type", "application/json");

		// parse parameters
		String body = "";
		if (params != null) {
			body = JSON.toJSONString(params);
		}

		return put(url, body, headers);
	}
	
	/**
	 * Send a delete request
	 * @param url         Url as string
	 * @param params      Map with query parameters
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String delete(String url,
			Map<String, String> params){
		return delete(url, params, null);
	}
	/**
	 * Send a delete request
	 * @param url         Url as string
	 * @param params      Map with query parameters
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String delete(String url,Map<String, String> params,
			Map<String, String> headers){
		return fetch("DELETE", appendQueryParams(url, params), null, headers);
	}
	
	/**
	 * Append query parameters to given url
	 * @param url         Url as string
	 * @param params      Map with query parameters
	 * @return url        Url with query parameters appended
	 * @throws IOException 
	 */
	static private String appendQueryParams(String url, 
			Map<String, String> params){
		String fullUrl = url;
		if (params != null) {
			try {
				fullUrl = fullUrl + "?" + getParamString(params);
			} catch (IOException e) {
				log.error("appendQueryParams error, url = "+url+",params = "+JSON.toJSONString(params), e.getCause());
			}
		}
		
		return fullUrl;
	}
	private static String getParamString(Map<String, String> params) throws IOException{
		if (params == null || params.size() <= 0) {
			return "";
		}
		String paramString = "";
		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
		}

		return paramString.substring(0, paramString.length() - 1);
	}
	/**
	 * Retrieve the query parameters from given url
	 * @param url         Url containing query parameters
	 * @return params     Map with query parameters
	 * @throws IOException 
	 */
	static public Map<String, String> getQueryParams(String url) throws IOException{
		Map<String, String> params = new HashMap<String, String>();
		
		int start = url.indexOf('?');
		while (start != -1) {
			// read parameter name
			int equals = url.indexOf('=', start);
			String param = "";
			if (equals != -1) {
				param = url.substring(start + 1, equals);
			}
			else {
				param = url.substring(start + 1);
			}
			
			// read parameter value
			String value = "";
			if (equals != -1) {
				start = url.indexOf('&', equals);
				if (start != -1) {
					value = url.substring(equals + 1, start);
				}
				else {
					value = url.substring(equals + 1);
				}
			}
			
			params.put(URLDecoder.decode(param, "UTF-8"), 
				URLDecoder.decode(value, "UTF-8"));
		}
		
		return params;
	}

	/**
	 * Returns the url without query parameters
	 * @param url         Url containing query parameters
	 * @return url        Url without query parameters
	 * @throws IOException 
	 */
	static public String removeQueryParams(String url) {
		int q = url.indexOf('?');
		if (q != -1) {
			return url.substring(0, q);
		}
		else {
			return url;
		}
	}
	
	/**
	 * Send a request
	 * @param method      HTTP method, for example "GET" or "POST"
	 * @param url         Url as string
	 * @param body        Request body as json string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static private String fetch(String method, String url, String body,
			Map<String, String> headers){
		// response
		String response = null;
		
		try {
			// connection
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);

			// method
			if (method != null) {
				conn.setRequestMethod(method);
			}

			// headers
			if (headers != null) {
				for(String key : headers.keySet()) {
					conn.addRequestProperty(key, headers.get(key));
				}
			}

			// body
			if (body != null) {
				conn.setDoOutput(true);
				OutputStream os = conn.getOutputStream();
				os.write(body.getBytes());
				os.flush();
				os.close();
			}
			conn.connect();
			
			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				InputStream is = conn.getInputStream();
				response = streamToString(is);
				is.close();
			}else {
				InputStream err = conn.getErrorStream();
				response = streamToString(err);
				err.close();
			}
		} catch (IOException e) {
			log.info("Invoke http error, url=" + url + ",body=" + body + ",result=" + response);
			log.error("Invoke http error, url=" + url + ",body=" + body + ",result=" + response, e.getCause());
		}

		log.info("Invoke http success, url=" + url + ",body=" + body + ",result=" + response);

		return response;
	}

	/**
	 * Read an input stream into a string
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static private String streamToString(InputStream in) throws IOException{
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[1024];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}
