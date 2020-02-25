package com.mapreduce.hotspot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class HBaseConnector {

    private static Configuration conf = null;
    static {
        conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, "master001,slave002,slave003");
    }

    public static void main(String[] args) {
        int code = Integer.parseInt(args[0]);
        long curTime = System.currentTimeMillis();
        switch (code) {
            case 0:
                System.out.println("Testing func `getDetailedArticleInfoById`");
                System.out.println(getDetailedArticleInfoById(args[1]));
                System.out.println(String.format("\ntakes %d ms", System.currentTimeMillis() - curTime));
                break;
            case 1:
                System.out.println(getTopArticles(Arrays.copyOfRange(args, 1, args.length)));
                System.out.println(String.format("\ntakes %d ms", System.currentTimeMillis() - curTime));
                break;
            case 2:
                System.out.println(getYearAnalysis(Arrays.copyOfRange(args, 1, args.length)));
                System.out.println(String.format("\ntakes %d ms", System.currentTimeMillis() - curTime));
                break;
        }
    }


    /**
     * @param keywords 搜索的多个关键字 例如输入框中输入deep learning则为一个2个元素的数组["deep", "learning"]
     * @return 一个JSONObject
     * JSONObject格式如下：
     * {
     *     "code": 0表示成功，-1表示未查到符合条件的数据，-2表示HBase连接问题
     *     "msg": 目前置空，可以设定DEBUG信息或是ERROR信息
     *     "count": 符合条件的文章数量
     *     "data": JSONArray，包含文章的MainInfo(即不是文章的全部信息) ,每篇文章一个JSONObject{
     *         "id": 文章的id
     *         "name": 文章的title
     *         "venue": 文章所属的会议或是期刊，JSONObject {
     *             "id": Venue Id
     *             "raw": 期刊或会议名称
     *         }
     *         "authors": 文章的作者信息（这个应该在网页上用不到，是我用来分析作者热度的）
     *         "hot": 文章热度，即pagerank值（现在是个0-100的随机值，保留两位小数）
     *     }
     *     "top_authors": JSONArray,包含TOP5的作者信息(不多于5个JSONObject){
     *         "id": 作者ID
     *         "name": 作者姓名
     *         "org": 作者所属的机构
     *         "prScore": 分析得到的作者的热度得分（这个应该用不到）
     *     }
     * }
     */
    public static JSONObject getTopArticles(String[] keywords) {
        Set<String> ans = _getIdsByMultiKeywords(keywords);
        if (ans.size() == 0) {
            //未找到同时命中多关键字的文章
            JSONObject ret = new JSONObject();
            ret.put("code", HCConstants.ERROR_NO_RECORD_FOUND);
            ret.put("msg", "");
            ret.put("count", 0);
            return ret;
        }
        JSONArray data = _getArticleMainInfoById(new ArrayList<>(ans));
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("count", data.size());
        ret.put("data", data);
        return ret;
    }
    public static JSONObject getTopAuthors(String[] keywords){
        Set<String> ans = _getIdsByMultiKeywords(keywords);
        if (ans.size() == 0) {
            //未找到同时命中多关键字的文章
            JSONObject ret = new JSONObject();
            ret.put("code", HCConstants.ERROR_NO_RECORD_FOUND);
            ret.put("msg", "");
            ret.put("count", 0);
            return ret;
        }
        JSONArray data = _getArticleMainInfoById(new ArrayList<>(ans));
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("count", _getTopAuthors(data).size());
        ret.put("data", _getTopAuthors(data));
        return ret;
    }

    /**
     * @param id 文章ID
     * @return 一个JSONObject
     * JSONObject格式如下：{
     *     "code": 0表示成功，-1表示未查到符合条件的数据，-2表示HBase连接问题
     *     "msg": 目前置空，可以设定DEBUG信息或是ERROR信息
     *     "data": 一个JSONOBject存储文章的具体信息{
     *         "title": 文章标题
     *         "authors": 文章作者，JSONArray，包含多个JSONObject {
     *             "id": 作者id
     *             "name": 作者姓名
     *             "org": 作者所属机构
     *         }
     *         "venue": 文章所属的会议或是期刊，JSONObject {
     *              "id": Venue Id
     *              "raw": 期刊或会议名称
     *         }
     *         "year": 年份
     *         "abstractWords": abstract的单词数组，JSONArray
     *         "citationNumber": 引用次数
     *         "fieldOfStudy": JSONArray，显示文章与某领域的相关程度包含多个JSONObject{
     *             "name": 领域名
     *             "w": 相关程度，一个0-1浮点数
     *         }
     *         "doi": doi
     *         "references": JSONArray，包含引用文章的MainInfo,每篇文章一个JSONObject{
     *              "id": 文章的id
     *              "name": 文章的title
     *              "venue": 文章所属的会议或是期刊，JSONObject {
     *                  "id": Venue Id
     *                  "raw": 期刊或会议名称
     *              }
     *              "authors": 文章的作者信息（无用）
     *              "hot": 文章热度，即pagerank值（现在是个0-100的随机值，保留两位小数）
     *         }
     *         "referencedBy": JSONArray, 包含被引用文章的MainInfo，每篇文章一个JSONObject，同上
     *     }
     * }
     *
     */
    public static JSONObject getDetailedArticleInfoById(String id) {
        final String TABLE_NAME = "st07article";
        final String COLUMN_FAMILY = "family1";
        JSONObject ret = new JSONObject();
        try {
            Connection conn = HBaseConnectionManager.getConnection();
            Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
            Get get = new Get(Bytes.toBytes(id));
            Result result = table.get(get);
            if (result.isEmpty()) {
                //未找到对应id的文章
                ret.put("code", HCConstants.ERROR_NO_RECORD_FOUND);
                ret.put("msg", "");
                return ret;
            }
            NavigableMap<byte[], byte[]> resMap = result.getFamilyMap(Bytes.toBytes(COLUMN_FAMILY));
            JSONObject article = new JSONObject();
            article.put("title", _cleanNullValue(resMap.get(Bytes.toBytes("title")), ""));
            JSONArray authorsJsonArr = JSONArray.parseArray(_cleanNullValue(resMap.get(Bytes.toBytes("authors")), new JSONArray().toString()));
            if (authorsJsonArr == null) {
                authorsJsonArr = new JSONArray();
            }
            for (Object obj: authorsJsonArr) {
                JSONObject tempJo = (JSONObject) obj;
                tempJo.putIfAbsent("id", "");
                tempJo.putIfAbsent("name", "");
                tempJo.putIfAbsent("org", "");
            }
            article.put("authors", authorsJsonArr);
            JSONObject venueObj = JSONObject.parseObject(_cleanNullValue(resMap.get(Bytes.toBytes("venue")), new JSONObject().toString()));
            if (venueObj == null) {
                venueObj = new JSONObject();
            }
            venueObj.putIfAbsent("id", "");
            venueObj.putIfAbsent("raw", "");
            article.put("venue", venueObj);
            article.put("year", _cleanNullValue(resMap.get(Bytes.toBytes("year")), ""));
            article.put("abstractWords", _cleanNullValue(resMap.get(Bytes.toBytes("abstractWords")), new JSONArray().toString()));
            article.put("citationNumber", _cleanNullValue(resMap.get(Bytes.toBytes("citationNumber")), "0"));
            article.put("fieldOfStudy", _cleanNullValue(resMap.get(Bytes.toBytes("fieldOfStudy")), new JSONArray().toString()));
            article.put("doi", _cleanNullValue(resMap.get(Bytes.toBytes("doi")), ""));
            Random random = new Random();
            article.put("hot", String.format("%.2f", random.nextDouble() * 100));

            JSONArray referencesIds = JSONArray.parseArray(Bytes.toString(resMap.get(Bytes.toBytes("references"))));
            if (referencesIds != null) {
                article.put("references", _getArticleMainInfoById(referencesIds.toJavaList(String.class)));
            }
            article.putIfAbsent("references", new JSONArray());

            if (result.containsColumn(Bytes.toBytes("referencedByFamily"), Bytes.toBytes(""))) {
                JSONArray byReferIds = JSONArray.parseArray(Bytes.toString(result.getValue(Bytes.toBytes("referencedByFamily"), Bytes.toBytes(""))));
                if (byReferIds != null) {
                    article.put("referencedBy", _getArticleMainInfoById(byReferIds.toJavaList(String.class)));
                }
            }
            //给定referencedBy的默认值为空数组
            article.putIfAbsent("referencedBy", new JSONArray());

            ret.put("code", 0);
            ret.put("msg", "");
            ret.put("data", article);
            return ret;

        } catch (IOException e) {
            e.printStackTrace();
        }
        ret.put("code", HCConstants.ERROR_NETWORK_ERROR);
        ret.put("msg", "");
        return ret;
    }

    /**
     * @param keywords 搜索的多个关键字 例如输入框中输入deep learning则为一个2个元素的数组["deep", "learning"]
     * @return 一个JSONObject
     * JSONObject格式如下：{
     *     "code": 0表示成功，-1表示未查到符合条件的数据，-2表示HBase连接问题
     *     "msg": 目前置空，可以设定DEBUG信息或是ERROR信息
     *     "data": JSONArray，按顺序给出 2000以前 - 2019的文章数量
     * }
     */
    public static JSONObject getYearAnalysis(String[] keywords) {
        final String TABLE_NAME = "st07_word_year";
        final String COLUMN_FAMILY = "years_info";
        Connection conn = HBaseConnectionManager.getConnection();
        NavigableMap<byte[], byte[]> ansMap = null;
        for (String keyword: keywords) {
            try {
                Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
                Get get = new Get(Bytes.toBytes(keyword)).addFamily(Bytes.toBytes(COLUMN_FAMILY));
                Result result = table.get(get);
                if (result.isEmpty()) {
                    JSONObject ret = new JSONObject();
                    ret.put("code", HCConstants.ERROR_NO_RECORD_FOUND);
                    ret.put("msg", "");
                    return ret;
                }
                NavigableMap<byte[], byte[]> resMap = result.getFamilyMap(Bytes.toBytes(COLUMN_FAMILY));
                if (ansMap != null) {
                    ansMap = unionYearStatistics(ansMap, resMap);
                } else {
                    ansMap = resMap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Integer> temp = new LinkedHashMap<>();
        temp.put("2000以前", 0);
        for (int i = 2000;i <= 2019;i++) {
            temp.put(Integer.toString(i), 0);
        }
        if (ansMap != null) {
            for (byte[] key: ansMap.keySet()) {
                String year = Bytes.toString(key);
                if (Integer.parseInt(year) < 2000) {
                    temp.put("2000以前", temp.get("2000以前") + Bytes.toString(ansMap.get(key)).split(",").length);
                } else {
                    temp.put(year, temp.get(year) + Bytes.toString(ansMap.get(key)).split(",").length);
                }
            }
        }
        JSONObject ret = new JSONObject();
        JSONArray data = new JSONArray();
        for (String key: temp.keySet()) {
            data.add(String.format("%d", temp.get(key)));
        }
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("data", data);
        return ret;
    }

    private static Set<String> _getIdsByMultiKeywords(String[] keywords) {
        Set<String> ans = new HashSet<>();
        //主关键字结果
        String[] ids = _getAllArticleIds(keywords[0]);
        if (ids == null) {
            return ans;
        }

        //获取副关键词结果
        Map<String, Set<String>> paraKeywordsMap = new HashMap<>();
        for (String keyword: keywords) {
            if (keyword == keywords[0]) continue;
            String[] paraKeywordsIds = _getAllArticleIds(keyword);
            if (paraKeywordsIds == null) continue;
            paraKeywordsMap.put(keyword, new HashSet<>(Arrays.asList(paraKeywordsIds)));
        }

        //找到包含全部关键词的文章ID
        outer: for (String id: ids) {
            for (String keyword: paraKeywordsMap.keySet()) {
                if (!paraKeywordsMap.get(keyword).contains(id)) {
                    continue outer;
                }
            }
            ans.add(id);
            //控制结果集大小不超过1000，否则影响后续查询HBase的速度
            if (ans.size() >= 1000) break;
        }

        return ans;
    }

    private static JSONArray _getArticleMainInfoById(List<String> ids) {
        final String TABLE_NAME = "st07article";
        final String COLUMN_FAMILY = "family1";
        JSONArray ret = new JSONArray();
        try {
            Connection conn = HBaseConnectionManager.getConnection();
            Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
            List<Get> gets = ids.parallelStream().map(id -> new Get(Bytes.toBytes(id)).addFamily(Bytes.toBytes(COLUMN_FAMILY))).collect(Collectors.toList());
            Result[] results = table.get(gets);
            for (Result res: results) {
                NavigableMap<byte[], byte[]> resMap = res.getFamilyMap(Bytes.toBytes(COLUMN_FAMILY));
                JSONObject jsonObject = new JSONObject();
                String artId = Bytes.toString(res.getRow());
                String artTitle = _cleanNullValue(resMap.get(Bytes.toBytes("title")), "");

                String venueTempStr = Bytes.toString(resMap.get(Bytes.toBytes("venue")));
                JSONObject venueObj = JSONObject.parseObject(venueTempStr);
                if (venueObj == null) {
                    venueObj = new JSONObject();
                }
                venueObj.putIfAbsent("id", "");
                venueObj.putIfAbsent("raw", "");

                String artVenue = venueObj.toString();
                String artAuthors = _cleanNullValue(resMap.get(Bytes.toBytes("authors")), new JSONArray().toString());
                //todo resMap.get("pagerank")
                jsonObject.put("id", artId);
                jsonObject.put("name", artTitle);
                jsonObject.put("venue", artVenue);
                jsonObject.put("authors", artAuthors);
                //todo jsonObject.put("hot", pagerankScore);
                Random random = new Random();
                jsonObject.put("hot", String.format("%.2f", random.nextDouble() * 100.0));
                ret.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static String[] _getAllArticleIds(String fieldname) {
        final String TABLE_NAME = "dblp_word_index";
        final String COLUMN_FAMILY = "article";
        try {
            Connection conn = HBaseConnectionManager.getConnection();
            Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
            Get get = new Get(Bytes.toBytes(fieldname)).addFamily(Bytes.toBytes(COLUMN_FAMILY));
            Result result = table.get(get);
            if (result.isEmpty()) {
                return null;
            }
            int blockIndex = 1;
            byte[] res = result.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(Integer.toString(blockIndex)));
            StringBuilder sb = new StringBuilder();
            while (res != null) {
                if (sb.length() != 0) sb.append(",");
                sb.append(Bytes.toString(res));
                blockIndex ++;
                res = result.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(Integer.toString(blockIndex)));
            }
            return sb.toString().split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray _getTopAuthors(JSONArray data) {
        Map<String, JSONObject> author2infoMap = new HashMap<>();
        Map<String, Double> author2prScoreMap = new HashMap<>();
        //计算每个作者所写文章的总pagerank分数（根据论文作者顺序加权）
        for (Object obj: data) {
            JSONArray authors = JSONArray.parseArray(((JSONObject) obj).getString("authors"));

            Double prScore = 1.0;
            //todo Double prScore = ((JSONObject) obj).getDouble("hot");
            Double weight = 0.8;
            for (Object authorObj: authors) {
                JSONObject authorJO = (JSONObject)authorObj;
                String id = authorJO.getString("id");
                author2infoMap.putIfAbsent(id, authorJO);
                author2prScoreMap.put(id, author2prScoreMap.getOrDefault(id, 0.0) + prScore * weight);
                // 第一作者获得0.8倍pagerank分数，第二作者0.16，第三作者0.032....
                weight *= 0.2;
            }
        }

        List<Map.Entry<String, Double>> list = author2prScoreMap.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getValue() > o2.getValue()) {
                return -1;
            } else if (o1.getValue().equals(o2.getValue())) {
                return 0;
            } else {
                return 1;
            }
        }).collect(Collectors.toList());
        JSONArray ret = new JSONArray();
        final int TOP_AUTHOR_NUM = Math.min(10, list.size());;
        for (int i = 0;i < TOP_AUTHOR_NUM;i++) {
            JSONObject info = author2infoMap.get(list.get(i).getKey());
            info.putIfAbsent("id", "");
            info.putIfAbsent("name", "");
            info.putIfAbsent("org", "");
            info.put("prScore", list.get(i).getValue());
            ret.add(info);
        }
        return ret;
    }

    private static String _cleanNullValue(byte[] data, String defaultValue) {
        if (data == null) {
            return defaultValue;
        }
        return Bytes.toString(data);
    }

    private static NavigableMap<byte[], byte[]> unionYearStatistics(NavigableMap<byte[], byte[]> a, NavigableMap<byte[], byte[]> b) {
        NavigableMap<byte[], byte[]> ret = new TreeMap<>(Bytes.BYTES_COMPARATOR);
        for (byte[] key: a.keySet()) {
            Set<String> aSet = new HashSet<String>(Arrays.asList(Bytes.toString(a.get(key)).split(",")));
            String bIdstr = Bytes.toString(b.get(key));
            if (bIdstr == null) continue;
            aSet.retainAll(Arrays.asList(bIdstr.split(",")));
            StringBuffer sb = new StringBuffer();
            for (String elem: aSet) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(elem);
            }
            ret.put(key, Bytes.toBytes(sb.toString()));
        }
        return ret;
    }
}
