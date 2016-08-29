package com.hzh.neoweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hzh.neoweather.model.Province;

import java.util.ArrayList;
import java.util.List;


public class ProvinceDao {
    public static final String TABLE_NAME = "Province";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PROVINCE_NAME = "province_name";
    public static final String COLUMN_PROVINCE_CODE = "province_code";
    public static final String TAG  = "ProvinceDao";
    private NeoWeatherDbOpeanHelper dbOpeanHelper;
    private SQLiteDatabase db;
    public ProvinceDao(Context context){
        dbOpeanHelper = NeoWeatherDbOpeanHelper.getInstance(context);
        db = dbOpeanHelper.getWritableDatabase();
    }


    /**
    * 将Province储存到数据库
    */
    public void saveProvince(Province province){
        if(db.isOpen()){
            if(province != null) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PROVINCE_NAME, province.getProvinceName());
                values.put(COLUMN_PROVINCE_CODE, province.getProvinceCode());
                db.insert(TABLE_NAME,null,values);
                Log.d(TAG,"insert province success");
            }
        }
    }
    /**
     * 读取全国的省份信息
     * */
    public  List<Province> loadProvinces() {
        List<Province> lists = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex(COLUMN_PROVINCE_NAME)));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex(COLUMN_PROVINCE_CODE)));
                lists.add(province);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return lists;
    }


}
