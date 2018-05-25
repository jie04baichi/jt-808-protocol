package cn.hylexus.jt808.service.baidu.trace.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import cn.hylexus.jt808.service.baidu.trace.model.StatusCodes;
import cn.hylexus.jt808.service.baidu.trace.util.CommonUtils;
import cn.hylexus.jt808.service.baidu.trace.util.HttpUtils;

/**
 * http客户端
 * 
 */
public class HttpClient {

    public static final String METHOD_POST = "POST";

    public static final String METHOD_GET = "GET";

    /**
     * HTTP连接超时，单位：毫秒
     */
    private static final int CONNECT_TIMEOUT = 5 * 1000;

    /**
     * HTTP读超时，单位：毫秒
     */
    private static final int READ_TIMEOUT = 5 * 1000;

    /**
     * 是否开启HTTPS
     */
    public static boolean isEnableHttps = false;

    /**
     * 发送请求
     * 
     * @param action
     * @param parameters
     * @param method
     * @return
     * @throws IOException
     */
    public static String sendRequest(String action, String parameters, String method) {
        HttpURLConnection urlConnection = null;
        try {
            if (isEnableHttps) {
                urlConnection = initHttps(action, parameters, method);
            } else {
                urlConnection = initHttp(action, parameters, method);
            }
        } catch (IOException e) {
            StringBuilder result = new StringBuilder();
            result.append("{\"status\":").append(StatusCodes.IO_EXCEPTION).append(",").append("\"message\":")
                    .append("\"").append(CommonUtils.transExceptionToString(e)).append("\"}");
            return result.toString();
        }

        String result = null;

        switch (method) {
            case METHOD_POST:
                result = sendPostRequest(urlConnection, parameters);
                break;

            case METHOD_GET:
                result = sendGetRequest(urlConnection);
                break;

            default:
                break;
        }

        return result;
    }

    /**
     * 初始化HTTPS连接
     * 
     * @param action
     * @param parameters
     * @param method
     * @return
     * @throws IOException
     */
    private static HttpsURLConnection initHttps(String action, String parameters, String method) throws IOException {
        StringBuilder path = new StringBuilder(UrlDomain.YINGYAN_HTTPS_URL);
        path.append(action);
        URL url = initURL(path.toString(), parameters, method);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        initConnection(urlConnection, method);
        ((HttpsURLConnection) urlConnection).setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                return hVerifier.verify(hostname, session);
            }
        });
        return urlConnection;
    }

    /**
     * 初始化HTTP
     * 
     * @param action
     * @param parameters
     * @param method
     * @return
     * @throws IOException
     */
    private static HttpURLConnection initHttp(String action, String parameters, String method) throws IOException {
        StringBuilder path = new StringBuilder(UrlDomain.YINGYAN_HTTP_URL);
        path.append(action);
        URL url = initURL(path.toString(), parameters, method);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        initConnection(urlConnection, method);
        return urlConnection;
    }

    /**
     * 初始化URL
     * 
     * @param path
     * @param parameters
     * @param method
     * @return
     * @throws MalformedURLException
     */
    private static URL initURL(String path, String parameters, String method) throws MalformedURLException {
        URL url = null;
        if (METHOD_GET.equalsIgnoreCase(method)) {
            url = new URL(path + "?" + parameters);
        } else {
            url = new URL(path);
        }
        return url;
    }

    /**
     * 初始化Connection（HttpsURLConnection继承于HttpURLConnection）
     * 
     * @param urlConnection
     * @param method
     * @throws ProtocolException
     */
    private static void initConnection(HttpURLConnection urlConnection, String method) throws ProtocolException {
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        urlConnection.setRequestMethod(method);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
    }

    /**
     * 发送get请求
     * 
     * @param urlConnection
     * @return
     */
    private static String sendGetRequest(HttpURLConnection urlConnection) {
        InputStream inStream = null;
        try {
            int responseCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inStream = urlConnection.getInputStream();
                return handleInputStream(inStream);
            } else {
                return handleHttpResponseCode(responseCode, urlConnection.getResponseMessage());
            }
        } catch (IOException ex) {
            return handleIOException(ex);
        } finally {
            if (null != inStream) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送post请求
     * 
     * @param urlConnection
     * @param parameters
     * @return
     */
    private static String sendPostRequest(HttpURLConnection urlConnection, String parameters) {
        OutputStream outStream = null;
        InputStream inStream = null;
        try {
            outStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, HttpUtils.CHAR_ENC));
            writer.write(parameters);
            writer.flush();
            writer.close();
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inStream = urlConnection.getInputStream();
                return handleInputStream(inStream);
            } else {
                return handleHttpResponseCode(responseCode, urlConnection.getResponseMessage());
            }
        } catch (IOException ex) {
            return handleIOException(ex);
        } finally {
            try {
                if (null != outStream) {
                    outStream.close();
                }
                if (null != inStream) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析输入流
     *
     * @param inputStream
     *
     * @return
     *
     * @throws IOException
     */
    private static String handleInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer result = new StringBuffer("");
        String line;
        try {
            while (null != (line = bufferedReader.readLine())) {
                result.append(line);
            }
        } catch (IOException ex) {
            return handleIOException(ex);
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bufferedReader = null;
                }
            }
        }
        return result.toString();
    }

    /**
     * 处理IO异常
     * 
     * @param ex
     * @return
     */
    private static String handleIOException(IOException ex) {
        StringBuilder result = new StringBuilder();
        result.append("{\"status\":").append(StatusCodes.IO_EXCEPTION).append(",").append("\"message\":").append("\"")
                .append(CommonUtils.transExceptionToString(ex)).append("\"}");
        return result.toString();
    }

    /**
     * 处理http非ok的响应状态码
     * 
     * @param responseCode
     * @param responseString
     * @return
     */
    private static String handleHttpResponseCode(int responseCode, String responseString) {
        StringBuilder result = new StringBuilder();
        result.append("{\"status\":").append(String.valueOf(responseCode)).append(",").append("\"message\":")
                .append("\"").append(responseString).append("\"}");
        return result.toString();
    }
}
