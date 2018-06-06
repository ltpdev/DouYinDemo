package com.gdcp.douyindemo;

/**
 * Created by asus- on 2018/6/5.
 */

public class URLManager {
    public static String getUrl(String channelId){
        return "http://c.m.163.com/nc/article/headline/" + channelId + "/0-20.html";
    }

    public static String getLoadMoreUrl(String channelId,int num){
        return "http://c.m.163.com/nc/article/headline/" + channelId + "/"+num+"-20.html";
    }

    public static final String VideoURL =
            "http://c.m.163.com/nc/video/list/V9LG4B3A0/y/0-20.html";

    public static String getLoadMoreVideoURL(int num){
        return "http://c.m.163.com/nc/video/list/V9LG4B3A0/y/"+num+"-10.html";
    }

}
