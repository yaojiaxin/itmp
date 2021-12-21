package cn.pioneeruniverse.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pioneeruniverse.common.constants.Constants;

import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public final static Logger logger = LoggerFactory.getLogger(BaseController.class);

    public Map<String, Object> handleException(Exception e, String message) {
        e.printStackTrace();
        logger.error(message + ":" + e.getMessage(), e);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", Constants.ITMP_RETURN_FAILURE);
        map.put("errorMessage", message);
        return map;
    }
}
