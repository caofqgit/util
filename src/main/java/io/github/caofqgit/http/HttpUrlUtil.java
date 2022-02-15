package io.github.caofqgit.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpUrlUtil {
    public static String doGet(String url) {
        URL requestUrl = null;
        if (StringUtils.isBlank(url))
            throw new RuntimeException("请求url为空");
        try {
            requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("请求url格式不合法");
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
        } catch (IOException e) {
            throw new RuntimeException("请求协议错误");
        }
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                bufferedReader = new BufferedReader(inputStreamReader);
                return bufferedReader.readLine();
            } else {
                String responseMessage = httpURLConnection.getResponseMessage();
                Map<String, Object> map = new HashMap<>();
                map.put("code", responseCode);
                map.put("message", responseMessage);
                return JSON.toJSONString(map);
            }
        } catch (IOException e) {
            throw new RuntimeException("请求异常");
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException ignored) {

            }


        }
    }

    public static String doPostJson(String url, String param) {
        return doPostJsonWhitHeader(url, param, null);
    }

    public static String doPostJsonWhitHeader(String url, String param, Map<String, String> header) {
        URL requestUrl = null;
        if (StringUtils.isBlank(url))
            throw new RuntimeException("请求url为空");
        try {
            requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("请求url格式不合法");
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("Charset", "UT-8");
            if (StringUtils.isNoneBlank(param) && JSON.isValid(param)) {
                byte[] bytes = param.getBytes(StandardCharsets.UTF_8);
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(param.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            } else {
                throw new RuntimeException("请求参数为空或者不合法");
            }
            if (ObjectUtils.isNotEmpty(header)) {
                Set<String> key = header.keySet();
                for (String s : key) {
                    httpURLConnection.setRequestProperty(s, header.get(s));
                }
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                bufferedReader = new BufferedReader(inputStreamReader);
                return bufferedReader.readLine();
            } else {
                String responseMessage = httpURLConnection.getResponseMessage();
                Map<String, Object> map = new HashMap<>();
                map.put("code", responseCode);
                map.put("message", responseMessage);
                return JSON.toJSONString(map);
            }
        } catch (IOException e) {
            throw new RuntimeException("请求协议错误");
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

    }
}
