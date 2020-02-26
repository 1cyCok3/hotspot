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
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mapreduce.hotspot.Service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@Controller
public class QueryController {
    @Autowired
    private QueryService queryService;
    private String hotspot;
    private String paperId;
    private String authorName;
    private JSONObject paperJSON;
    private JSONObject authorJSON;
    @RequestMapping(value = "/references")
    public String reference(ModelMap modelMap){
        String paperName = paperJSON.getString("name");
        String authors = JSON.parseObject(paperJSON.getString("authors")).getString("name");
        String venue = JSON.parseObject(paperJSON.getString("venue")).getString("raw");
        String citation = paperJSON.getString("citationNumber");
        modelMap.addAttribute("name", paperName);
        modelMap.addAttribute("authors", authors);
        modelMap.addAttribute("venue", venue);
        modelMap.addAttribute("citationNumber", citation);
        return "references";
    }
    @RequestMapping(value = "/authors")
    public String authors(ModelMap modelMap){
        String paperName = paperJSON.getString("name");
        String authors = paperJSON.getString("authors");
        String venue = paperJSON.getString("venue");
        String citaiton = paperJSON.getString("citation");
        modelMap.addAttribute("name", paperName);
        modelMap.addAttribute("authors", authors);
        modelMap.addAttribute("venue", venue);
        modelMap.addAttribute("citation", citaiton);
        return "authors";

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
    public String PaperData() throws JSONException {
        return queryService.listPapers(hotspot);
    }

    @RequestMapping("/authordata")
    @ResponseBody
    public String AuthorData() throws JSONException {
        System.out.println(queryService.listAuthor(hotspot));
        return queryService.listAuthor(hotspot);
    }

    @RequestMapping(value="/authordetails",method = RequestMethod.POST)
    @ResponseBody
    public String Authordetails(@RequestBody String authorJSON) throws JSONException {
        System.out.println(authorJSON);
        authorName = JSON.parseObject(authorJSON).getString("name");
        this.authorJSON = JSON.parseObject(queryService.authorDetails(authorName));
        return "ok";
    }

    @RequestMapping(value="/paperdetails",method = RequestMethod.POST)
    @ResponseBody
    public String Paperdetails(@RequestBody String paperJSON) throws JSONException {
        System.out.println(paperJSON);
        paperId = JSON.parseObject(paperJSON).getString("id");
        System.out.println(paperId);
        this.paperJSON = JSON.parseObject(queryService.paperDetails(paperId));
        return paperJSON;
    }

    @RequestMapping(value="/years",method = RequestMethod.GET)
    @ResponseBody
    public Object GetYears(String hotspot){
        String years = queryService.yearAnalysis(hotspot);
        System.out.println(years);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("year", years);
        return map;
    }

    @RequestMapping("/reference")
    @ResponseBody
    public String reference() throws JSONException {
        String data = paperJSON.getString("references");
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("data", data);
        return ret.toString();
    }

    @RequestMapping("/referencedBy")
    @ResponseBody
    public String referencedBy() throws JSONException {
        String data = paperJSON.getString("referencedBy");
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("data", data);
        return ret.toString();
    }

}
