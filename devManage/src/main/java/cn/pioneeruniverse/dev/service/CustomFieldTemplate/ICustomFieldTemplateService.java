package cn.pioneeruniverse.dev.service.CustomFieldTemplate;

import cn.pioneeruniverse.dev.entity.ExtendedField;

import java.util.List;

public interface ICustomFieldTemplateService {

    List<ExtendedField> findFieldByDefect(Long id);
}
