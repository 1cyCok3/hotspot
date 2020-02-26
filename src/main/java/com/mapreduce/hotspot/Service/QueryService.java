package com.mapreduce.hotspot.Service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mapreduce.hotspot.util.HBaseConnector;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    HBaseConnector hbc = new HBaseConnector ();
    public String listPapers(String hotspot) throws JSONException {
        String[] keywords = hotspot.split(" ");
        JSONObject papers = HBaseConnector.getTopArticles(keywords);
        String venue = JSON.parseObject(papers.getString("venue")).getString("raw");
        System.out.println(venue);
        papers.remove("venue");
        papers.put("venue", venue);
        return papers.toString();
    }
    public String paperDetails(String paeprId){
        JSONObject paperDetails = HBaseConnector.getDetailedArticleInfoById(paeprId);
        return paperDetails.getString("data");
    }
    public String listAuthor(String hotspot){
        String[] keywords = hotspot.split(" ");
        JSONObject authors = HBaseConnector.getTopAuthors(keywords);
        return authors.toString();
    }

    public String authorDetails(String authorName) {
        JSONObject author = new JSONObject();
        author.put("name", authorName);
        author.put("org", "NJU");
        author.put("field", "machine learning");
        author.put("prScore", "2.77");
        return author.toString();
    }

    public String yearAnalysis(String hotspot){
        String[] keywords = hotspot.split(" ");
        JSONObject years = HBaseConnector.getYearAnalysis(keywords);
        System.out.println(years);
        String result = years.getString("data");
        return result;
    }
}
