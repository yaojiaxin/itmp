package cn.pioneeruniverse.system.service.CustomFieldTemplate;

import cn.pioneeruniverse.system.entity.TblCustomFieldTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ICustomFieldTemplateService {

    List<TblCustomFieldTemplate> selectTableStructureByTableName(String tableName);

    Map<String,Object> selectFieldByCustomForm(String tableName);

    Integer findFieldByFieldName(String customForm,String fieldName);

    void saveFieldTemplate(TblCustomFieldTemplate fieldTemplate,HttpServletRequest request);


    List<TblCustomFieldTemplate> selectTableStructureByTableNameItmp(String tableName);
}
