package com.netty.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil implements Serializable {
    private static final long serialVersionUID = -8872078079583695100L;
    private static JsonUtil instance = new JsonUtil();

    private JsonUtil() {
    }

    public static JsonUtil getInstance() {
        return instance;
    }

    public static Map<String, Object> object2Map(Object obj, SerializerFeature... serializerFeature) {
        if(obj == null) {
            return new HashMap(16);
        } else {
            String json = object2JSON(obj, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteMapNullValue});
            Map<String, Object> map = (Map)JSONObject.parse(json);
            return map;
        }
    }

    public static List<Map<String, Object>> objects2List(Object obj, SerializerFeature... serializerFeature) {
        if(obj == null) {
            return new ArrayList();
        } else {
            String json = object2JSON(obj);
            List<Map<String, Object>> rows = (List)JSONObject.parse(json);
            return rows;
        }
    }

    public static String object2JSON(Object obj, SerializerFeature... serializerFeature) {
        return obj == null?"{}":JSON.toJSONString(obj, serializerFeature);
    }

    public static String object2JSON(Object obj) {
        return obj == null?"{}":JSON.toJSONString(obj, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat, SerializerFeature.QuoteFieldNames, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty});
    }

    public static JSONObject object2JSONObject(Object obj) {
        return JSON.parseObject(object2JSON(obj));
    }

    public static <T> T json2Object(String json, Class<T> clazz) {
        return json != null && !json.isEmpty()?JSON.parseObject(json, clazz):null;
    }

    public static <T> T json2Reference(String json, TypeReference<T> reference) {
        return json != null && !json.isEmpty()?JSON.parseObject(json, reference, new Feature[0]):null;
    }

    public static <T> T json2Reference(String json, TypeReference<T> reference, Feature... features) {
        return json != null && !json.isEmpty()?JSON.parseObject(json, reference, features):null;
    }

    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
        return json2Object(JSON.toJSONString(map), clazz);
    }

    public static JSONObject string2JSON(String str) {
        return JSON.parseObject(str);
    }

    public static JSONArray string2JSONArray(String str) {
        return JSON.parseArray(str);
    }
}
