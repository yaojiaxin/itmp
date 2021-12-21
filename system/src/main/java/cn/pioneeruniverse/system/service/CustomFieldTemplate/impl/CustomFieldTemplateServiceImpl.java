package cn.pioneeruniverse.system.service.CustomFieldTemplate.impl;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.system.dao.mybatis.CustomFieldTemplate.CustomFieldTemplateDao;
import cn.pioneeruniverse.system.entity.ExtendedField;
import cn.pioneeruniverse.system.entity.TblCustomFieldTemplate;
import cn.pioneeruniverse.system.service.CustomFieldTemplate.ICustomFieldTemplateService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ICustomFieldTemplateService")
public class CustomFieldTemplateServiceImpl implements ICustomFieldTemplateService {

    @Autowired
    private CustomFieldTemplateDao fieldTemplateDao;

    @Override
    @DataSource(name="tmpDataSource")
    public List<TblCustomFieldTemplate> selectTableStructureByTableName(String tableName) {
        List<TblCustomFieldTemplate> fieldTemp=fieldTemplateDao.selectTableStructureByTableName(tableName);
        return fieldTemp;
    }

    @Override
    public Map<String,Object> selectFieldByCustomForm(String tableName) {
        Map<String,Object> map=new HashMap<>();
        List<TblCustomFieldTemplate> fieldTemps = fieldTemplateDao.selectFieldByCustomForm(tableName);
        for (TblCustomFieldTemplate fieldTemp:fieldTemps){
            if(fieldTemp.getCustomField()!=null) {
                map = JSON.parseObject(fieldTemp.getCustomField());
            }
        }
        return map;
    }

    @Override
    public Integer findFieldByFieldName(String customForm, String fieldName) {
        List<TblCustomFieldTemplate> fieldTemps = fieldTemplateDao.selectFieldByCustomForm(customForm);
        if(fieldTemps!=null&&fieldTemps.size()>0){
            Map<String,Object> map = JSON.parseObject(fieldTemps.get(0).getCustomField());
            List<ExtendedField> extendedFields = JSONObject.parseArray(map.get("field").toString(),ExtendedField.class);
            for (ExtendedField extendedField:extendedFields){
                if(extendedField.getFieldName().equals(fieldName)){
                    return 2;
                }
            }
        }
        return 1;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveFieldTemplate(TblCustomFieldTemplate fieldTemplate,HttpServletRequest request) {
        CommonUtil.setBaseValue(fieldTemplate,request);
        List<TblCustomFieldTemplate> fieldTemps=
                fieldTemplateDao.selectFieldByCustomForm(fieldTemplate.getCustomForm());
        if(fieldTemps!=null&&fieldTemps.size()>0){
            fieldTemplateDao.updCustomFieldTemplate(fieldTemplate);
        }else{
            fieldTemplateDao.addCustomFieldTemplate(fieldTemplate);
        }
    }

    @Override
    public List<TblCustomFieldTemplate> selectTableStructureByTableNameItmp(String tableName) {
        List<TblCustomFieldTemplate> fieldTemp=fieldTemplateDao.selectTableStructureByTableNameItmp(tableName);
        return fieldTemp;
    }
}
