package com.hzh.neoweather.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.neoweather.NeoApplication;
import com.hzh.neoweather.R;
import com.hzh.neoweather.adapter.WeatherInfoFragmentAdapter;
import com.hzh.neoweather.fragment.WeatherInfoFragment;
import com.hzh.neoweather.model.WeatherInfo;
import com.hzh.neoweather.listener.HttpCallbackListener;
import com.hzh.neoweather.util.HttpUtil;
import com.hzh.neoweather.util.LocationUtil;
import com.hzh.neoweather.util.SharedPreferenceHelper;
import com.hzh.neoweather.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    public final static String CITIES_FLAG = "cities_flag";
    public final static String WEATHER_FLAG = "weather_flag";
    public static final int RequestCode = 1;
    public static final String TAG = "MainActivity";
    private String localCity = "";//地理位置定位的城市
    private List<String> myCities;//所有的城市
    private Toolbar toolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView mNavigationView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView nowWfDescTv;//当前天气描述
    private TextView nowWfAqiTv;//当前天气aqi
    private TextView nowWfTmp;//当前温度
    private TextView updateTime;//更新时间
    private ViewPager wfPager;
    private NeoApplication neoApplication;
    private boolean networkOk = false;//网络状态标志位
    private ImageView locationIcon;//位置图标 只有本地定位的城市显示
    private Map<String,WeatherInfo> weatherInfoMap;
    private List<WeatherInfo> weatherInfoList;
    private WeatherInfoFragmentAdapter weatherInfoFragmentAdapter;
    private List<WeatherInfoFragment> weatherFragments;

    /**
     * 定位 获取到所在位置
     * 获取sp中的所有城市
     *  没有就用定位到的位置，有的话就对比一下定位的位置在不在其中 在就不处理 不在就添加上
     *
     *
     *
     * 获取城市所有城市名称
     *   如果未设置 就定位 检查网络 获取天气
     * 获取到城市名称
     *  去sp中查询天气信息 如果更新时间超过6小时 更新天气 未超过直接显示
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherInfoMap = new HashMap<>();
        weatherInfoList = new ArrayList<>();
        initView();
        getCitiesFormSp();
        getWeatherInfoFromSp();
        checkNetwork();
        getCityNameFromLocation();
    }

    /**
     * 从ShraedPreference中获取城市
     */
    private void getCitiesFormSp(){
        myCities = new ArrayList<>();
        myCities = SharedPreferenceHelper.getMyCities(this);
    }

    /**
     * 从Sp中获取weatherinfo
     */
    private void getWeatherInfoFromSp(){
        weatherInfoMap = SharedPreferenceHelper.getWeatherInfoMap(MainActivity.this,myCities);
        weatherInfoList = SharedPreferenceHelper.getWeatherInfoList(MainActivity.this,myCities);
        setTitleInfo(weatherInfoList.get(0));
        for (WeatherInfo w : weatherInfoList){
            WeatherInfoFragment wf = new WeatherInfoFragment(this,w);
            weatherFragments.add(wf);
            weatherInfoFragmentAdapter.notifyDataSetChanged();
        }
    }
    /**
     *获取位置信息 通过位置信息查询城市
     * 此方法以后需要加上权限判断、错误提示等；
     */
    private void getCityNameFromLocation(){
        LocationUtil locationUtil = new LocationUtil(this);
        final Location location = locationUtil.getLocation();
        if(location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String url = StringUtils.getLocationUrl(latitude,longitude);
            HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d(TAG,response);
                    try {
                        JSONObject jo = new JSONObject(response);
                        localCity =jo.getJSONObject("result").getJSONObject("addressComponent").getString("city");
                        localCity = StringUtils.cutCityWord(localCity);
                        WeatherInfo w = weatherInfoMap.get(localCity);
                        if(w == null || needUpdate(w)){
                            getWeatherInfoFromNet(w.getCityName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }else{
            Snackbar.make(drawerLayout,R.string.location_error,Snackbar.LENGTH_LONG).show();
        }
    }




    /**
     * 检查网络是否可用
     */
    private void checkNetwork(){
        neoApplication = NeoApplication.instance;
        if (!neoApplication.isNetworkConnected()){
            networkOk = false;
            Snackbar.make(drawerLayout,R.string.net_error,Snackbar.LENGTH_SHORT).show();
        }else{
            networkOk = true;
        }

    }

    /**
     * @param cityName
     * 从网络获取城市天气
     */
    private void getWeatherInfoFromNet(String cityName){
        String url = StringUtils.getWeatherUrl(cityName);
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    final WeatherInfo weatherInfo = WeatherInfo.parses(jo);
                    SharedPreferenceHelper.saveWeatherInfo(MainActivity.this,weatherInfo);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addWeatherInfo(weatherInfo);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    /**
     * 更新时间超过6小时自动更新
     */
    private boolean needUpdate(WeatherInfo w){
        Date updateTime = new Date();
        Date nowTime = new Date();
        String updateStr = w.getUpdateTime();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            updateTime =s.parse(updateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long intervalMilli = nowTime.getTime() - updateTime.getTime();
        if(intervalMilli / (60 * 60 * 1000) > 6){
            return true;
        }
        return false;
    }

    /**
     * 添加天气信息
     */
    private void addWeatherInfo(WeatherInfo w){
        weatherInfoList.add(w);
        weatherInfoMap.put(w.getCityName(),w);
        myCities.add(w.getCityName());
        WeatherInfoFragment wf = new WeatherInfoFragment(this,w);
        weatherFragments.add(wf);
        weatherInfoFragmentAdapter.notifyDataSetChanged();
        wfPager.setCurrentItem(weatherFragments.size()-1);//将页面跳转到最后添加的
        setTitleInfo(w);

    }
    /**
     * 更新天气信息
     */
    private void updateWeatherInfo(WeatherInfo w){
        for(int i = 0;i<weatherInfoList.size();i++){
            if(weatherInfoList.get(i).getCityName().equals(w.getCityName())){
                weatherInfoList.set(i,w);//替换
                WeatherInfoFragment wf = new WeatherInfoFragment(this,w);
                weatherFragments.set(i,wf);
                break;
            }
        }
        weatherInfoMap.put(w.getCityName(),w);
        weatherInfoFragmentAdapter.notifyDataSetChanged();
    }
    /**
     * 删除天气信息
     */
    private void deleteWeatherInfo(String cityName){
        if(myCities.contains(cityName)){
            myCities.remove(cityName);
            weatherInfoMap.remove(cityName);
            for(int n = 0;n<weatherInfoList.size();n++){
                if(cityName.equals(weatherInfoList.get(n).getCityName())){
                    if(n==0&&weatherInfoList.size()>1){
                        //删除第一个时需要手动改变标题栏
                        setTitleInfo(weatherInfoList.get(1));
                    }
                    weatherInfoList.remove(n);
                    weatherFragments.remove(n);
                    weatherInfoFragmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    /**
     * 设置标题栏的信息
     */
    private void setTitleInfo(WeatherInfo weather){
        // toolbar.setTitle(weather.getCityName());
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        mCollapsingToolbarLayout.setTitle(weather.getCityName());
        nowWfDescTv.setText(weather.getNowForecast().getWeatherDESC());
        nowWfAqiTv.setText("AQI"+" "+weather.getAqi()+"("+weather.getQlty()+")");
        nowWfTmp.setText(weather.getNowForecast().getTemperature()+"℃");
        if(localCity.equals(weather.getCityName())){
            locationIcon.setVisibility(View.VISIBLE);
        }else {
            locationIcon.setVisibility(View.GONE);
        }
        updateTime.setText("更新于："+weather.getUpdateTime());
    }


    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.toolbar_mene);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_city:
                        Toast.makeText(MainActivity.this,"add",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,CityManagerActivity.class);
                        intent.putExtra(CITIES_FLAG,(Serializable) myCities);
                        intent.putExtra(WEATHER_FLAG,(Serializable) weatherInfoList);
                        startActivityForResult(intent,RequestCode);
                }
                return false;

            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,
                toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_about:
                        Toast.makeText(MainActivity.this,"about",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_setting:
                        Toast.makeText(MainActivity.this,"setting",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collasping_toolbar_layout);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色

        nowWfDescTv = (TextView) findViewById(R.id.now_wf_desc);
        nowWfAqiTv = (TextView) findViewById(R.id.now_wf_aqi);
        nowWfTmp = (TextView) findViewById(R.id.now_wf_tmp);
        locationIcon = (ImageView) findViewById(R.id.location_icon);
        updateTime = (TextView) findViewById(R.id.update_time);

        wfPager = (ViewPager) findViewById(R.id.weather_info_pager);
        weatherFragments = new ArrayList<>();

        weatherInfoFragmentAdapter = new WeatherInfoFragmentAdapter(getSupportFragmentManager(),weatherFragments);
        wfPager.setAdapter(weatherInfoFragmentAdapter);
        wfPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleInfo(weatherInfoList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode && resultCode == RESULT_OK){
            List<WeatherInfo> addWeatherInfos = (List<WeatherInfo>)
                    data.getSerializableExtra(CityManagerActivity.ADD_WEATHER_FLAG);
            if(addWeatherInfos!=null){
                for (WeatherInfo w : addWeatherInfos){
                    addWeatherInfo(w);
                }
            }
            List<String> deletedNames = (List<String>)
                    data.getSerializableExtra(CityManagerActivity.DEL_NAME_FLAG);
            if(deletedNames != null){
                for(String name : deletedNames){
                    deleteWeatherInfo(name);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}

