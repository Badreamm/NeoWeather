package com.hzh.neoweather.listener;


public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
