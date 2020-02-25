package com.mapreduce.hotspot.Service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.mapreduce.hotspot.Model.Author;
import com.mapreduce.hotspot.util.HBaseConnector;

import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryService {
    HBaseConnector hbc = new HBaseConnector();
    public String listArticle() throws JSONException {
        JSONArray json = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("venue", "测试数据测试数据");
        jo.put("name", "测试数据测试数据");
        jo.put("hot", 2.33);
        json.add(jo);
        JSONObject jobj = new JSONObject();
        jobj.put("code",0);
        jobj.put("msg","");
        jobj.put("count", 1);
        jobj.put("data",json);
        System.out.println(jobj.toString());
        return jobj.toString();
    }

    public String listAuthor(String hotspot){
        String[] keywords = hotspot.split(" ");
        JSONObject authors = hbc.getTopAuthors(keywords);
        return authors.toString();
    }

}
