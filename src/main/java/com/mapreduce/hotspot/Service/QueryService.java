package com.mapreduce.hotspot.Service;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mapreduce.hotspot.util.HBaseConnector;

import org.springframework.stereotype.Service;

@Service
public class QueryService {
    HBaseConnector hbc = new HBaseConnector ();

    public String listPapers(String hotspot, int page, int limit) throws JSONException {
        String[] keywords = hotspot.split(" ");
//        JSONObject papers = HBaseConnector.getTopArticles(keywords);
        JSONObject papers = HBaseConnector.getPagedTopArticles(keywords, page, limit);
/*        JSONObject venueData = JSONObject.parseObject(papers.getString("venue"));
        String venue = venueData.getString("raw");
        System.out.println(venue);
        papers.remove("venue");
        papers.put("venue", venue);*/
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

    public String listPaperReferences(JSONObject articleJO, int page, int limit) {
        JSONObject refers = HBaseConnector.getPagedReference(articleJO, page, limit, false);
        return refers.toString();
    }

    public String listPaperReferencedBys(JSONObject articleJO, int page, int limit) {
        JSONObject refers = HBaseConnector.getPagedReference(articleJO, page, limit, true);
        return refers.toString();
    }



    public String yearAnalysis(String hotspot){
        String[] keywords = hotspot.split(" ");
        JSONObject years = HBaseConnector.getYearAnalysis(keywords);
        System.out.println(years);
        String result = years.getString("data");
        return result;
    }
}
