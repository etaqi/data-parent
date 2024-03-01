package com.tdp.data.web.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class RabbitUtils {


    @Value("${spring.rabbitmq.host}")
    private String RABBIT_HOST;
    @Value("${spring.rabbitmq.username}")
    private String RABBIT_USER;
    @Value("${spring.rabbitmq.password}")
    private String RABBIT_PWD;


    public QueueModel getQueueModel(String queueName){
        try {
            String response = getApiMessage(queueName);
            if(StringUtils.isEmpty(response)){
                return null;
            }
            return JSONObject.parseObject(response, QueueModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getApiMessage(String queueName) throws IOException {
        //发送一个GET请求
        HttpURLConnection httpConn;
        BufferedReader in;

        String urlString = "http://" + RABBIT_HOST + ":15672/api/queues/%2f/" + queueName;
        URL url = new URL(urlString);
        httpConn = (HttpURLConnection) url.openConnection();
        //设置用户名密码
        String auth = RABBIT_USER + ":" + RABBIT_PWD;
        BASE64Encoder enc = new BASE64Encoder();
        String encoding = enc.encode(auth.getBytes());
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("Authorization", "Basic " + encoding);
        // 建立实际的连接
        httpConn.connect();
        //读取响应
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            StringBuilder content = new StringBuilder();
            String tempStr = "";
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while ((tempStr = in.readLine()) != null) {
                content.append(tempStr);
            }
            in.close();
            httpConn.disconnect();
            return content.toString();
        } else {
            httpConn.disconnect();
            return "";
        }
    }
}
