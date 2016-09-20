package com.hzh.neoweather.model;


import com.hzh.neoweather.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WeatherInfo implements Serializable {
    private String cityName;
    private String cityId;
    private String aqi;//空气质量指数
    private String qlty;//空气质量类别
    private String pm25;//PM2.5 1小时平均值(ug/m³)
    private String pm10;
    private String updateTime;//数据更新时间
    private List<DailyForecast> dailyForecasts;
    private List<HourlyForecast> hourlyForecasts;
    private NowForecast nowForecast;
    private WeatherSuggestion weatherSuggestion;

    public WeatherInfo (){
        dailyForecasts = new ArrayList<>();
        hourlyForecasts = new ArrayList<>();
    }

    public static WeatherInfo parses(JSONObject jo){
        WeatherInfo w = new WeatherInfo();
        try {
            if(jo.has("HeWeather data service 3.0")){
                JSONArray ja = jo.getJSONArray("HeWeather data service 3.0");
                jo = ja.getJSONObject(0);
            }

            if(jo.has("aqi")){
                JSONObject city =jo.getJSONObject("aqi").getJSONObject("city");
                w.setAqi(city.getString("aqi"));
                w.setQlty(city.getString("qlty"));
                w.setPm10(city.getString("pm10"));
                w.setPm25(city.getString("pm25"));
            }
            if(jo.has("basic")){
                JSONObject basic = jo.getJSONObject("basic");
                if(basic.has("city")){
                    w.setCityName(basic.getString("city"));
                }
                if(basic.has("id")){
                    w.setCityId(basic.getString("id"));
                }
                if(basic.has("update")){
                    w.setUpdateTime(basic.getJSONObject("update").getString("utc"));
                }
            }
            if(jo.has("daily_forecast")){
                w.setDailyForecasts(DailyForecast.parseList(jo.getJSONArray("daily_forecast")));
            }
            if(jo.has("hourly_forecast")){
                w.setHourlyForecasts(HourlyForecast.parseList(jo.getJSONArray("hourly_forecast")));
            }
            if(jo.has("now")){
                w.setNowForecast(NowForecast.parse(jo.getJSONObject("now")));
            }
            if(jo.has("suggestion")){
                w.setWeatherSuggestion(WeatherSuggestion.parses(jo.getJSONObject("suggestion")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return w;
    }



    public List<DailyForecast> getDailyForecasts() {
        return dailyForecasts;
    }

    public void setDailyForecasts(List<DailyForecast> dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }

    public List<HourlyForecast> getHourlyForecasts() {
        return hourlyForecasts;
    }

    public void setHourlyForecasts(List<HourlyForecast> hourlyForecasts) {
        this.hourlyForecasts = hourlyForecasts;
    }

    public NowForecast getNowForecast() {
        return nowForecast;
    }

    public void setNowForecast(NowForecast nowForecast) {
        this.nowForecast = nowForecast;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public WeatherSuggestion getWeatherSuggestion() {
        return weatherSuggestion;
    }

    public void setWeatherSuggestion(WeatherSuggestion weatherSuggestion) {
        this.weatherSuggestion = weatherSuggestion;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQlty() {
        return qlty;
    }

    public void setQlty(String qlty) {
        this.qlty = qlty;
    }




}
