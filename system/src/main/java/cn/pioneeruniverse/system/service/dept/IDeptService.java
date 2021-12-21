package cn.pioneeruniverse.system.service.dept;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import cn.pioneeruniverse.system.entity.TblDeptInfo;

public interface IDeptService {

    List<TblDeptInfo> getAllDept();

    TblDeptInfo selectDeptById(Long id);

    List<TblDeptInfo> selectDeptByParentId(Long id);

    void insertDept(TblDeptInfo dept);

    void updateDept(TblDeptInfo dept);

    List<TblDeptInfo> selectDeptByCompanyId(Long companyId);

    List<Map<String, Object>> getDeptData(String deptData);

    List<TblDeptInfoDTO> getAllDeptInfo();

	int tmpDeptData(TblDeptInfo deptInfo);

	List<TblDeptInfo> getDeptByDeptName(TblDeptInfo deptInfo);
}
