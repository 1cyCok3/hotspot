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
    private String paperName;
    private JSONObject paperJSON;
    @RequestMapping(value = "/references")
    public String reference(ModelMap modelMap){
        String paperName = paperJSON.getString("name");
        String authors = paperJSON.getString("authors");
        String venue = paperJSON.getString("venue");
        String citaiton = paperJSON.getString("citation");
        modelMap.addAttribute("name", paperName);
        modelMap.addAttribute("authors", authors);
        modelMap.addAttribute("venue", venue);
        modelMap.addAttribute("citation", citaiton);
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
    public String PaperData() throws JSONException {
        System.out.println(queryService.listArticle());
        return queryService.listArticle();
    }

    @RequestMapping("/authordata")
    @ResponseBody
    public String AuthorData() throws JSONException {
        System.out.println(queryService.listAuthor(hotspot));
        return queryService.listAuthor(hotspot);
    }

    @RequestMapping(value="/authordetails",method = RequestMethod.POST)
    @ResponseBody
    public String Authordetails(@RequestParam(value="paper") String name, ModelMap modelMap) throws JSONException {
        System.out.println(name);
        paperJSON = JSON.parseObject(name);
        return "ok";
    }

    @RequestMapping(value="/paperdetails",method = RequestMethod.POST)
    @ResponseBody
    public String Paperdetails(@RequestParam(value="paper") String paperJSON, ModelMap modelMap) throws JSONException {
        System.out.println(paperJSON);
/*        paperName = JSON.parseObject(JSON.parseObject(paperJSON).getString("data")).getString("name");
        System.out.println(paperName);
        this.paperJSON = JSON.parseObject(queryService.paperDetails(paperName));*/
        return "ok";
    }

    @RequestMapping(value="/years",method = RequestMethod.GET)
    @ResponseBody
    public Object GetYears(){
        int[] years = {5, 20, 36, 10, 10, 20, 30, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35};
        HashMap<Object, Object> map = new HashMap<>();
        map.put("year", years);
        return map;
    }
}
