package cn.hylexus.jt808.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {

	private static final int CONNECT_TIMEOUT = 3000;
	private static final int READ_TIMEOUT = 2000;

	private static TrustManager myX509TrustManager = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	};

	private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

	public static void main(String[] args) {
		String url = "http://api.yiautos.com/v1/sessions";
		Map<String, Object> params = new HashMap<>();
		params.put("username", "18150119999");
		params.put("password", "123456");
		params.put("role", "user");
		// httpGet(url, null);
		httpPost(url, params);
	}

	public static String httpGet(String url, Map<String, Object> params) {
		return httpGet(url, params, null);
	}

	public static String httpGet(String url, String params) {
		return httpGet(url, params, null);
	}
	
	public static HttpResponse httpGetHeader(String url, Map<String, Object> params, Map<String, String> heads){
		return httpGetHeader(url, getParamString(params), heads);
	}
	
	public static HttpResponse httpGetHeader(String url, String params, Map<String, String> heads){

		HttpResponse response = new HttpResponse();
		try {
			// String paramStr = getParamString(params);
			URL uri = new URL(url + "?" + params);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.setRequestProperty("Authorization", "Token " + token);
			if (heads != null && heads.size() > 0) {
				for (Map.Entry<String, String> entry : heads.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			conn.connect();
			
			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				
				String result = "";
				
				response.setSuccess(true);
				response.setMessage("SUCCESS");
				String total = conn.getHeaderField("Total");
				String size = conn.getHeaderField("Per-Page");
				response.setTotal(total == null ? 0 : Integer.parseInt(total));
				response.setSize(size == null ? 0 : Integer.parseInt(size));
				
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(buffer))) {
					baos.write(buffer, 0, len);
					baos.flush();
				}
				result = baos.toString("utf-8");
				log.info("invoke http get success, url=" + url + ",params=" + params + ",result=" + result);
				response.setResult(result);
				return response;
			}
			response.setMessage("http get faild, error_code=" + conn.getResponseCode());
			log.warn("invoke http get faild, url=" + url + ",params=" + params + ",result_code="
					+ conn.getResponseCode());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String httpGet(String url, String params, Map<String, String> heads) {
		String result = "";

		try {
			// String paramStr = getParamString(params);
			URL uri = new URL(url + "?" + params);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.setRequestProperty("Authorization", "Token " + token);
			if (heads != null && heads.size() > 0) {
				for (Map.Entry<String, String> entry : heads.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			conn.connect();
			
			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				
				System.out.println("total:"+conn.getHeaderField("Total"));
				System.out.println("per-page:"+conn.getHeaderField("Per-Page"));
				
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(buffer))) {
					baos.write(buffer, 0, len);
					baos.flush();
				}
				result = baos.toString("utf-8");
				log.info("invoke http get success, url=" + url + ",params=" + params + ",result=" + result);
				return result;
			}
			log.warn("invoke http get faild, url=" + url + ",params=" + params + ",result_code="
					+ conn.getResponseCode());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String httpGet(String url, Map<String, Object> params, Map<String, String> heads) {
		return httpGet(url, getParamString(params), heads);
	}

	private static String getParamString(Map<String, Object> params) {
		if (params == null || params.size() <= 0) {
			return "";
		}
		String paramString = "";
		try {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				paramString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8") + "&";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paramString.substring(0, paramString.length() - 1);
	}

	public static String httpPost(String url, Map<String, Object> params) {
		String result = "";
		try {
			URL uri = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();

			PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
			String paramStr = getParamString(params);
			log.info("invoke http post start..., url=" + url + ",params=" + paramStr);
			printWriter.write(paramStr);
			printWriter.flush();

			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(buffer))) {
					baos.write(buffer, 0, len);
					baos.flush();
				}

				baos.close();
				result = baos.toString("utf-8");
				log.info("invoke http post success, url=" + url + ",params=" + paramStr + ",result=" + result);
				return result;
			}
			log.warn("invoke http post fail, url=" + url + ",params=" + paramStr + ",result=" + result);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String httpsPost(String url, String params) {
		String result = "";
		try {

			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

			URL uri = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();
			conn.setSSLSocketFactory(sslcontext.getSocketFactory());
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			if (params != null)
				out.writeBytes(params);

			out.flush();
			out.close();

			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(buffer))) {
					baos.write(buffer, 0, len);
					baos.flush();
				}

				baos.close();
				result = baos.toString("utf-8");
				log.info("invoke http post success, url=" + url + ",params=" + params + ",result=" + result);
				return result;
			}
			log.warn("invoke http post fail, url=" + url + ",params=" + params + ",result=" + result);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return result;
	}
}
