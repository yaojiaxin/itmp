package cn.pioneeruniverse.system.controller;

import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.common.velocity.tag.VelocityDataDict;
import cn.pioneeruniverse.system.entity.TblCustomFieldTemplate;
import cn.pioneeruniverse.system.service.CustomFieldTemplate.ICustomFieldTemplateService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("fieldTemplate")
public class CustomFieldTemplateController{

    private final static Logger logger = LoggerFactory.getLogger(CustomFieldTemplateController.class);

    @Autowired
    private ICustomFieldTemplateService iCustomFieldTemplateService;

    @RequestMapping(value = "getTableName", method = RequestMethod.POST)
    public Map<String,Object> getTableName() {
        Map<String,Object> map=new HashMap<>();
        try {
            Map [] map1= getDictMap("TBL_CUSTOM_FIELD_TEMPLATE_TABLE",
                    "tableKey","tableValue");
            map.put("data", JSONObject.toJSONString(map1));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    @RequestMapping(value = "findFieldByTableName", method = RequestMethod.POST)
    public Map<String,Object> findFieldByTableName(String tableName) {
        Map<String,Object> map=new HashMap<>();
        try {
            Map [] map1= getDictMap("TBL_CUSTOM_FIELD_TEMPLATE_TYPE",
                    "typeKey","typeValue");
            List<TblCustomFieldTemplate> fieldTemp =new ArrayList<>();
            if(tableName!=null && tableName.endsWith("_itmpdb")){
                 String  tableNameItmp=tableName.replaceAll("_itmpdb","");
                fieldTemp =iCustomFieldTemplateService.selectTableStructureByTableNameItmp(tableNameItmp);
                map=iCustomFieldTemplateService.selectFieldByCustomForm(tableName);
            }else {
                fieldTemp =iCustomFieldTemplateService.selectTableStructureByTableName(tableName);
                map=iCustomFieldTemplateService.selectFieldByCustomForm(tableName);
            }


            map.put("type",map1);
            map.put("fieldTemp",fieldTemp);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    @RequestMapping(value = "findFieldByFieldName", method = RequestMethod.POST)
    public Integer findFieldByFieldName(String customForm,String fieldName) {
        Integer status;
        try {
            status=iCustomFieldTemplateService.findFieldByFieldName(customForm,fieldName);
        } catch (Exception e) {
            status=3;
            logger.error(e.getMessage(), e);
        }
        return status;
    }

    @RequestMapping(value = "saveFieldTemplate", method = RequestMethod.POST)
    public AjaxModel save(TblCustomFieldTemplate fieldTemplate,HttpServletRequest request) {
        try {
            iCustomFieldTemplateService.saveFieldTemplate(fieldTemplate,request);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    private Map [] getDictMap(String dictName,String putKey,String putValue){
        VelocityDataDict dict= new VelocityDataDict();
        Map<String,String> result= dict.getDictMap(dictName);
        Map [] map1 = new Map[result.size()];int i=0;
        for(Map.Entry<String, String> entry:result.entrySet()){
            Map<String, Object> map2 = new HashMap<>();
            map2.put(putValue,entry.getKey());
            map2.put(putKey,entry.getValue());
            map1[i]=map2;
            i++;
        }
        return map1;
    }
}