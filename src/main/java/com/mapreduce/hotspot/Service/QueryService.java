package com.mapreduce.hotspot.Service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mapreduce.hotspot.util.HBaseConnector;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    HBaseConnector hbc = new HBaseConnector();
    public String listArticle() throws JSONException {
        JSONArray json = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("venue", "ICML");
        jo.put("name", "Machine Learning");
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
    public String paperDetails(String paperName){
        JSONObject paper = new JSONObject();
        paper.put("name", paperName);
        paper.put("authors", "作者1");
        paper.put("authors", "作者2");
        paper.put("authors", "作者3");
        paper.put("venue", "ICML");
        paper.put("citation", 15);
        return paper.toString();
    }
    public String listAuthor(String hotspot){
        String[] keywords = hotspot.split(" ");
        JSONObject authors = HBaseConnector.getTopAuthors(keywords);
        return authors.toString();
    }

}
