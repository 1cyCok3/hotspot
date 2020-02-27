/*
 * @Author: your name
 * @Date: 2020-02-03 18:08:08
 * @LastEditTime: 2020-02-09 22:41:26
 * @LastEditors: your name
 * @Description: In User Settings Edit
 * @FilePath: \hotspot\src\main\java\com\mapreduce\hotspot\Controller\QueryController.java
 */
package com.mapreduce.hotspot.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mapreduce.hotspot.Service.QueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;


@Controller
public class QueryController {
    @Autowired
    private QueryService queryService;
    private String hotspot;
    private String paperId;
    private JSONObject paperJSON;
    @RequestMapping(value = "/references")
    public String reference(ModelMap modelMap){
        String paperName = paperJSON.getString("title");
        JSONArray authorData = JSONArray.parseArray(paperJSON.getString("authors"));
        String authors = "";
        for(int i = 0 ; i < authorData.size() ; i++){
            authors += JSONObject.parseObject(authorData.get(i).toString()).getString("name");
            if(i!= authorData.size()-1) authors += ", ";
        }
        JSONObject venueData = JSONObject.parseObject(paperJSON.getString("venue"));
        String venue = venueData.getString("raw");
        String citation = paperJSON.getString("citationNumber");
        modelMap.addAttribute("name", paperName);
        modelMap.addAttribute("authors", authors);
        modelMap.addAttribute("venue", venue);
        modelMap.addAttribute("citationNumber", citation);
        System.out.println(modelMap);
        return "references";
    }

    @RequestMapping(value = "/articles",method = RequestMethod.GET)
    public String login(@RequestParam(value = "hotspot", required = false) String hotspot,
                        ModelMap modelMap){
        this.hotspot = hotspot;
        System.out.println("关键词="+hotspot);
        modelMap.addAttribute("hotspot",hotspot);
        return "report";

    }
    @RequestMapping("/paperdata")
    @ResponseBody
    public String PaperData(@RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "limit", defaultValue = "10") int limit) throws JSONException {
        return queryService.listPapers(hotspot, page, limit);
    }

    @RequestMapping("/authordata")
    @ResponseBody
    public String AuthorData() throws JSONException {
        System.out.println(queryService.listAuthor(hotspot));
        return queryService.listAuthor(hotspot);
    }

    @RequestMapping(value="/paperdetails",method = RequestMethod.POST)
    @ResponseBody
    public String Paperdetails(@RequestBody String paperJSON) throws JSONException {
        System.out.println(paperJSON);
        paperId = JSON.parseObject(paperJSON).getString("id");
        System.out.println(paperId);
        this.paperJSON = JSON.parseObject(queryService.paperDetails(paperId));
        return this.paperJSON.toString();
    }

    @RequestMapping(value="/years",method = RequestMethod.GET)
    @ResponseBody
    public Object GetYears(){
        String years = queryService.yearAnalysis(hotspot);
        years = years.replaceAll("\"","");
        System.out.println(years);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("year", years);
        return map;
    }

    @RequestMapping("/reference")
    @ResponseBody
    public String reference() throws JSONException {
        JSONArray data = paperJSON.getJSONArray("references");
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("count", data.size());
        ret.put("data", data);
        return ret.toString();
    }

    @RequestMapping("/referencedBy")
    @ResponseBody
    public String referencedBy() throws JSONException {
        JSONArray data = paperJSON.getJSONArray("referencedBy");
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("count", data.size());
        ret.put("data", data);
        return ret.toString();
    }

}
