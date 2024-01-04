package com.cmy.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author:Cmy
 * @Date:2023-11-30 16:20
 */
@Slf4j
public class UrlRecordParseUtil {

    public static Map<String,String> parseUrlRecords(String path){
        Map<String, String> map = new LinkedHashMap<>();
        List<String> urlMapList = FileUtil.readLines(path, StandardCharsets.UTF_8);
        for (String s : urlMapList) {
            int i = s.indexOf(":");
            String key = s.substring(0, i);
            String value = s.substring(i+1);
            map.put(key,value);
        }
        //log.debug("解析urlRecords:\n{}",map);
        return map;
    }

    public static void saveUrlRecords(String urlPath,Map<String,String> urlRecordsMap){
        //直接覆写
        File file = new File(urlPath);
        List<String> lines = new ArrayList<>();
        urlRecordsMap.forEach((key,value)->{
            lines.add(key+":"+value);
        });
        //log.debug("保存urlRecords:\n{}",lines);
        FileUtil.writeLines(lines,file,StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String s = "0.0000000000000000000000000000";
        Double aDouble = Convert.toDouble(s);
        log.info(String.valueOf(aDouble == 0));
    }

}

    