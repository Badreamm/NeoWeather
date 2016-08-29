package com.hzh.neoweather.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.LocaleDisplayNames;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 位置服务工具
 */
public class LocationUtil {
    public static final String TAG = "LocationUtil";
    private LocationManager locationManager;
    private Context context;
    private Location location;
    private String currentCity;

    public LocationUtil (Context context){
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
    }

    public LocationManager getLocationManager(){
        return locationManager;
    }

    public Location getLocation(){
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        String provider;
        //获取所有可用的位置提供器
        List<String> providerLists = locationManager.getProviders(true);
        if(providerLists.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }else if(providerLists.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }else{
            Toast.makeText(context,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
            return null;
        }
        location = locationManager.getLastKnownLocation(provider);
        if(location != null){
            return location;
        }
        return null;
    }



}
