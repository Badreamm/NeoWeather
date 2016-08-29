package com.hzh.neoweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hzh.neoweather.model.Province;


public class NeoWeatherDbOpeanHelper extends SQLiteOpenHelper{
    public static final String DB_NAME = "neo_weather";
    public static final int DB_VERSION = 1;//数据库版本
    private static NeoWeatherDbOpeanHelper instance;
    private static final String TAG = "NeoWeatherDbOpeanHelper";

    public static final String CREATE_PROVINCE = "create table "
            +ProvinceDao.TABLE_NAME + " ( "
            +ProvinceDao.COLUMN_ID + " integer primary key autoincrement, "
            +ProvinceDao.COLUMN_PROVINCE_NAME + " text, "
            +ProvinceDao.COLUMN_PROVINCE_CODE + " text)";

    public static final String CREATE_CITY = "create table "
            +CityDao.TABLE_NAME + " ( "
            +CityDao.COLUNM_ID + " integer primary key autoincrement, "
            +CityDao.COLUMN_CITY_NAME + " text, "
            +CityDao.COLUMN_CITY_CODE + "text,"
            +CityDao.COLUMN_PROVINCE_ID + "integer)";

    public static final String CREATE_COUNTY = "create table "
            +CountyDao.TABLE_NAME + " ( "
            +CountyDao.COLUNM_ID + " integer primary key autoincrement, "
            +CountyDao.COLUMN_COUNTY_NAME + " text, "
            +CountyDao.COLUMN_COUNTY_CODE + "text,"
            +CountyDao.COLUMN_CITY_ID + "integer)";

    public NeoWeatherDbOpeanHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public synchronized static NeoWeatherDbOpeanHelper getInstance(Context context){
        if(instance == null){
            instance = new NeoWeatherDbOpeanHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
        Log.d(TAG,"create table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
