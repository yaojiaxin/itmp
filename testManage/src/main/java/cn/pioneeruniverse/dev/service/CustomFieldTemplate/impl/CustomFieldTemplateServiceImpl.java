package cn.pioneeruniverse.dev.service.CustomFieldTemplate.impl;

import cn.pioneeruniverse.dev.dao.mybatis.TblCaseInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblDefectInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.entity.ExtendedField;
import cn.pioneeruniverse.dev.feignInterface.TestManageToSystemInterface;
import cn.pioneeruniverse.dev.service.CustomFieldTemplate.ICustomFieldTemplateService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("ICustomFieldTemplateService")
public class CustomFieldTemplateServiceImpl implements ICustomFieldTemplateService {

    @Autowired
    private TestManageToSystemInterface testManageToSystemInterface;
    @Autowired
    private TblRequirementFeatureMapper requirementFeatureMapper;
    @Autowired
    private TblDefectInfoMapper defectInfoMapper;
    @Autowired
    private TblCaseInfoMapper tblCaseInfoMapper;


    @Override
    public List<ExtendedField> findFieldByReqFeature(Long id) {
        Map<String,Object> map=testManageToSystemInterface.findFieldByTableName("tbl_requirement_feature");
        String listTxt = JSONArray.toJSONString(map.get("field"));
        List<ExtendedField> extendedFields = JSONArray.parseArray(listTxt, ExtendedField.class);

        //List<ExtendedField> extendedFields = JSONObject.parseArray(map.get("field"),ExtendedField.class);
        if(extendedFields!=null) {
            Iterator<ExtendedField> it = extendedFields.iterator();
            while (it.hasNext()) {
                ExtendedField extendedField = it.next();
                if (extendedField.getStatus().equals("2")) {
                    it.remove();
                } else {
                    String valueName = requirementFeatureMapper.getFeatureFieldTemplateById(id, extendedField.getFieldName());
                    extendedField.setValueName(valueName==null?"":valueName);
                }
            }
        }
        return extendedFields;
    }

    @Override
    public List<ExtendedField> findFieldByDefect(Long id) {
        Map<String,Object> map=testManageToSystemInterface.findFieldByTableName("tbl_defect_info");
        String listTxt = JSONArray.toJSONString(map.get("field"));
        List<ExtendedField> extendedFields = JSONArray.parseArray(listTxt, ExtendedField.class);

        if(extendedFields!=null){
            Iterator<ExtendedField> it = extendedFields.iterator();
            while(it.hasNext()){
                ExtendedField extendedField = it.next();
                if(extendedField.getStatus().equals("2")){
                    it.remove();
                }else{
                    String valueName=defectInfoMapper.getDafectFieldTemplateById(id,extendedField.getFieldName());
                    extendedField.setValueName(valueName==null?"":valueName);
                }
            }
        }
        return extendedFields;
    }

    @Override
    public List<ExtendedField> findFieldByTestCase(Long id) {
        Map<String,Object> map=testManageToSystemInterface.findFieldByTableName("tbl_case_info");
        String listTxt = JSONArray.toJSONString(map.get("field"));
        List<ExtendedField> extendedFields = JSONArray.parseArray(listTxt, ExtendedField.class);
        if(extendedFields!=null) {
            Iterator<ExtendedField> it = extendedFields.iterator();
            while (it.hasNext()) {
                ExtendedField extendedField = it.next();
                if (extendedField.getStatus().equals("2")) {
                    it.remove();
                } else {
                    String valueName = tblCaseInfoMapper.getCaseFieldTemplateById(id,extendedField.getFieldName());
                    extendedField.setValueName(valueName==null?"":valueName);
                }
            }
        }
        return extendedFields;
    }
}
