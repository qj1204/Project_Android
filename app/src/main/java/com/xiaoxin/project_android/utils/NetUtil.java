package com.xiaoxin.project_android.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class NetUtil {

    public static void getNetData(String url, Map<String, String> data, final Handler handler){
        new Thread(()  -> {
            String result = OkHttpUtils.builder().url(Static.SERVICE_PATH + url)
                    .setParamMap(data)
                    .addHeader("Content-Type","application/json; charset-utf-8")
                    .get()
                    .sync();
            Message message = new Message();
            Bundle bundle = new Bundle();
            if (result == null){
                message.what = 0;
                bundle.putString("data","网络请求超时");
            } else {
                Log.d("请求结果为",result);

                JSONObject jsonObject = JSON.parseObject(result);
                Integer code = jsonObject.getInteger("code");
                if (code != null && (code == 100 || code == 302)) {
                    message.what = 1;
                } else {
                    message.what = 0;
                }

                bundle.putString("msg", jsonObject.getString("msg"));
                bundle.putString("data", jsonObject.getString("data"));
            }
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();

    }

    public static void getNetData(String url, Map<String, String> data, int timeout, final Handler handler){
        new Thread(()  -> {
            String result = OkHttpUtils.builder(timeout).url(Static.SERVICE_PATH + url)
                    .setParamMap(data)
                    .addHeader("Content-Type","application/json; charset-utf-8")
                    .get()
                    .sync();
            Message message = new Message();
            Bundle bundle = new Bundle();
            if (result == null){
                message.what = 0;
                bundle.putString("data","网络请求超时");
            } else {
                Log.d("NET-->",result);

                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getInteger("code") != null && jsonObject.getInteger("code") == 100) {
                    message.what = 1;
                } else {
                    message.what = 0;
                }
                bundle.putString("msg", jsonObject.getString("msg"));
                bundle.putString("data", jsonObject.getString("data"));
            }
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();

    }

}