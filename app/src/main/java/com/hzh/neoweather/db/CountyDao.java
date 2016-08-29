package com.hzh.neoweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hzh.neoweather.model.County;

import java.util.ArrayList;
import java.util.List;


public class CountyDao {
    public static final String TABLE_NAME = "County";
    public static final String COLUNM_ID = "id";
    public static final String COLUMN_COUNTY_NAME = "county_name";
    public static final String COLUMN_COUNTY_CODE = "county_code";
    public static final String COLUMN_CITY_ID = "city_id";
    private NeoWeatherDbOpeanHelper dbOpeanHelper;
    private SQLiteDatabase db;

    public CountyDao(Context context){
        dbOpeanHelper = NeoWeatherDbOpeanHelper.getInstance(context);
        db = dbOpeanHelper.getWritableDatabase();
    }

    public void saveCity(County county){
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(COLUMN_COUNTY_NAME,county.getCountyName());
            values.put(COLUMN_COUNTY_CODE,county.getCountyCode());
            values.put(COLUMN_CITY_ID,county.getCityId());
            db.insert(TABLE_NAME,null,values);
        }
    }

    public List<County> loadCounties(int cityId){
        List<County> lists = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,null,COLUMN_CITY_ID + " = ?",
                new String[] {String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex(COLUNM_ID)));
                county.setCountyName(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTY_NAME)));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTY_CODE)));
                county.setCityId(cityId);
                lists.add(county);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return lists;
    }

}
