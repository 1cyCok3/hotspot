/*
 * @Author: your name
 * @Date: 2020-02-03 18:08:08
 * @LastEditTime: 2020-02-09 22:41:26
 * @LastEditors: your name
 * @Description: In User Settings Edit
 * @FilePath: \hotspot\src\main\java\com\mapreduce\hotspot\Controller\QueryController.java
 */
package com.mapreduce.hotspot.Controller;


import java.util.HashMap;
import java.util.Map;


import com.mapreduce.hotspot.Service.QueryService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class QueryController {
    @Autowired
    private QueryService queryService;
    private String hotspot;
    private String paperJSON;
    @RequestMapping(value = "/references")
    public String reference(){
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

    @RequestMapping(value="/paperdetails",method = RequestMethod.POST)
    @ResponseBody
    public String Paperdetails(@RequestParam(value="paper") String paper, ModelMap modelMap) throws JSONException {
        System.out.println(paper);
        return "ok";
    }
}
