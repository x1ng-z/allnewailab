package hs.industry.ailab.utils.httpclient;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/29 13:57
 * http，数据
 */


@Slf4j
public class HttpUtils {
    public static <T> T postForEntity(String url, Object myclass, Class<T> returnClass) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost posturl = new HttpPost(url);

        String jsonSting = JSON.toJSONString(myclass);
        StringEntity entity = new StringEntity(jsonSting, "UTF-8");
        posturl.setEntity(entity);
        posturl.setHeader("Content-Type", "application/json;charset=utf8");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(posturl);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String stringValue = EntityUtils.toString(responseEntity);
                return JSONObject.parseObject(stringValue, returnClass);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public static String PostParam(String url, Map<String, String> data) {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        CloseableHttpResponse response2 = null;
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            if (data != null) {
                for (Map.Entry<String, String> datas : data.entrySet()) {
                    nvps.add(new BasicNameValuePair(datas.getKey(), datas.getValue()));
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            response2 = httpclient.execute(httpPost);
            log.info(url + (data != null ? data.toString() : ""));
            log.info("response code=" + response2.getStatusLine().getStatusCode());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            result = EntityUtils.toString(entity2);
//            logger.debug("ssss:" + EntityUtils.toString(entity2));
            EntityUtils.consume(entity2);
            return result;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                // 释放资源
                if (httpclient != null) {
                    httpclient.close();
                }
                if (response2 != null) {
                    response2.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }

        return result;
    }

}
