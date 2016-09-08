package com.hzh.neoweather;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hzh.neoweather.model.WeatherInfo;
import com.hzh.neoweather.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeoApplication extends Application {
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public static NeoApplication instance;

    private Map<String,WeatherInfo> weatherInfos = new HashMap();
    private List<String> myCities = new ArrayList<>();
    private List<WeatherInfo> addWeatherInfos = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
    }


    /**
     * 检测当前系统声音是否为正常模式
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }


    /**
     * 检测网络是否可用
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if(!StringUtils.isEmpty(extraInfo)){
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    public Map<String, WeatherInfo> getWeatherInfos() {
        return weatherInfos;
    }

    public void setWeatherInfos(Map<String, WeatherInfo> weatherInfos) {
        this.weatherInfos = weatherInfos;
    }

    public List<String> getMyCities() {
        return myCities;
    }

    public void setMyCities(List<String> myCities) {
        this.myCities = myCities;
    }

    public List<WeatherInfo> getAddWeatherInfos() {
        return addWeatherInfos;
    }

    public void setAddWeatherInfos(List<WeatherInfo> addWeatherInfos) {
        this.addWeatherInfos = addWeatherInfos;
    }
}
