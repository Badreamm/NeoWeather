package com.hzh.neoweather.activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.neoweather.R;
import com.hzh.neoweather.model.WeatherInfo;
import com.hzh.neoweather.listener.HttpCallbackListener;
import com.hzh.neoweather.util.HttpUtil;
import com.hzh.neoweather.util.LocationUtil;
import com.hzh.neoweather.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String TAG = "MainActivity";
    private String currentCity;
    private TextView cityNameTV;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView mNavigationView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getCityName();
    }

    private void initView(){
        cityNameTV = (TextView) findViewById(R.id.city_name);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.toolbar_mene);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_city:
                        Toast.makeText(MainActivity.this,"add",Toast.LENGTH_SHORT).show();
                }
                return false;

            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,
                toolbar,R.string.open,R.string.close);
        drawerLayout.setDrawerListener(mActionBarDrawerToggle);

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
    }

    private void getCityName(){
        LocationUtil locationUtil = new LocationUtil(this);
        Location location = locationUtil.getLocation();
        if(location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String url = "http://api.map.baidu.com/geocoder/v2/?ak=5qnVTG8AIE9m9m1RfIOz1ovyD0ggun5G" +
                    "&mcode=B2:6C:6D:A0:AF:22:51:A4:FE:25:75:70:BB:08:80:3D:72:A9:65:F3;com.hzh.neoweather" +
                    "&location=" +latitude+","+longitude+
                    "&output=json&pois=0";
            HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d(TAG,response);
                    try {
                        JSONObject jo = new JSONObject(response);
                        currentCity =jo.getJSONObject("result").getJSONObject("addressComponent").getString("city");
                        currentCity = StringUtils.cutCityWord(currentCity);
                        cityNameTV.setText(currentCity);
                        getWeatherInfo(currentCity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                toolbar.setTitle(currentCity);
                                //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
                                mCollapsingToolbarLayout.setTitle(currentCity);
                            }
                        });
                        Log.d(TAG, currentCity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                }
            });

        }

    }

    private void getWeatherInfo(String cityName){
        String url = "https://api.heweather.com/x3/weather?city="+cityName+
                "&key=aade6c300897492eb36c319cac413cd7";
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    WeatherInfo weatherInfo = WeatherInfo.parses(jo);
                    Log.d(TAG,weatherInfo.getAqi());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });


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

