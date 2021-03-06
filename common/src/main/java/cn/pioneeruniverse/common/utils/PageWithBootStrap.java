package cn.pioneeruniverse.common.utils;

import java.util.Map;

/**
 * Description: bootstrap table 分页公共写法
 * Author:liushan
 * Date: 2019/1/2 上午 10:10
 */
public class PageWithBootStrap {

    public static Map<String,Object> setPageNumberSize(Map<String, Object> map, Integer pageNumber, Integer pageSize){
        if (pageNumber != null && pageSize != null){
            int start = (pageNumber - 1) * pageSize;
            map.put("start", start);
            map.put("pageSize", pageSize);
        } else {
            map.put("start", null);
            map.put("pageSize", null);
        }
        return map;
    }
}
