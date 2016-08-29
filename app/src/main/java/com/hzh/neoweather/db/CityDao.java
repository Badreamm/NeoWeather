package com.hzh.neoweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hzh.neoweather.model.City;

import java.util.ArrayList;
import java.util.List;


public class CityDao {
    public static final String TABLE_NAME = "City";
    public static final String COLUNM_ID = "id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_CITY_CODE = "city_code";
    public static final String COLUMN_PROVINCE_ID = "province_id";
    private NeoWeatherDbOpeanHelper dbOpeanHelper;
    private SQLiteDatabase db;
    public CityDao(Context context){
        dbOpeanHelper = NeoWeatherDbOpeanHelper.getInstance(context);
        db = dbOpeanHelper.getWritableDatabase();
    }

    public void saveCity(City city){
        ContentValues values = new ContentValues();
        if(db.isOpen()){
            values.put(COLUMN_CITY_NAME,city.getCityName());
            values.put(COLUMN_CITY_CODE,city.getCityCode());
            values.put(COLUMN_PROVINCE_ID,city.getProvinceId());
            db.insert(TABLE_NAME,null,values);
        }
    }

    public List<City> loadCities(int provinceId){
        List<City> lists = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,null,COLUMN_PROVINCE_ID+" = ?",
                new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex(COLUNM_ID)));
                city.setCityName(cursor.getString(cursor.getColumnIndex(COLUMN_CITY_NAME)));
                city.setCityCode(cursor.getString(cursor.getColumnIndex(COLUMN_CITY_CODE)));
                city.setProvinceId(provinceId);
                lists.add(city);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return  lists;
    }

}
