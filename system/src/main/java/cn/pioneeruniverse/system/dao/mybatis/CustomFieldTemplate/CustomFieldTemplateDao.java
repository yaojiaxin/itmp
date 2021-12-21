package cn.pioneeruniverse.system.dao.mybatis.CustomFieldTemplate;

import cn.pioneeruniverse.system.entity.TblCustomFieldTemplate;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

public interface CustomFieldTemplateDao extends BaseMapper<TblCustomFieldTemplate> {

    List<TblCustomFieldTemplate> selectTableStructureByTableName(String tableName);

    List<TblCustomFieldTemplate> selectFieldByCustomForm(String tableName);

    Integer addCustomFieldTemplate(TblCustomFieldTemplate blCustomFieldTemplate);

    Integer updCustomFieldTemplate(TblCustomFieldTemplate blCustomFieldTemplate);

    List<TblCustomFieldTemplate> selectTableStructureByTableNameItmp(String tableName);
}
