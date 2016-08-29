package com.hzh.neoweather.model;

import org.json.JSONObject;

/**
 * 数据操作结果实体类
 */
public class Result {

    private String mCode = "";
    private String mMessage;


    public String getCode() {
        return mCode;
    }
    public String getMessage() {
        return mMessage;
    }
    public void setCode(String code) {
        this.mCode = code;
    }
    public void setMessage(String message) {
        mMessage = message;
    }


    @Override
    public String toString(){
        return String.format("RESULT: CODE:%s,MSG:%s\nRESULT:%s"
                        + "", mCode, mMessage==null?"":mMessage,
                entity == null?"":entity.toString());
    }

    public static Result parse(String str) throws Exception{
        JSONObject json = new JSONObject(str);
        Result result = new Result();
        if(json.has("code"))
            result.mCode = json.getString("code");
        if(json.has("message"))
            result.mMessage = json.getString("message");
        if(json.has("result"))
            result.entity = json.get("result");
        return result;
    }
    public Object entity;

}
