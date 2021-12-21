package cn.pioneeruniverse.dev.service.CustomFieldTemplate;

import cn.pioneeruniverse.dev.entity.ExtendedField;
import java.util.List;

public interface ICustomFieldTemplateService {

    List<ExtendedField> findFieldByReqFeature(Long id);

    List<ExtendedField> findFieldByDefect(Long id);

    List<ExtendedField> findFieldByTestCase(Long id);

}
