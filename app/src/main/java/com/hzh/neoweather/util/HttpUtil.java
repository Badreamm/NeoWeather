package com.hzh.neoweather.util;

import com.hzh.neoweather.listener.HttpCallbackListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder respone = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        respone.append(line);
                    }
                    if(listener != null){
                        listener.onFinish(respone.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                    try {
                        if(inputStream != null){
                            inputStream.close();
                        }
                        if(inputStream != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }
}
