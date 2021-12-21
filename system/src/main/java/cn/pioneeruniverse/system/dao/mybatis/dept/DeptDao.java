package cn.pioneeruniverse.system.dao.mybatis.dept;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblDeptInfo;


public interface DeptDao extends BaseMapper<TblDeptInfo> {
	
	List<TblDeptInfo> getAllDept();
	
	TblDeptInfo selectDeptById(Long id);
	
	List<TblDeptInfo> selectDeptByParentId(Long id);
	
	void insertDept(TblDeptInfo dept);
	
	void updateDept(TblDeptInfo dept);
	
	String selectMaxNum();
	
	String selectPPDept(Long id);
	
	void setChildrenDisable(TblDeptInfo dept);
	
	void updateChildrenDept(TblDeptInfo dept);
	
	List<TblDeptInfo> selectDeptByCompanyId(Long companyId);

    TblDeptInfo getDeptByCode(String depCode);

    List<TblDeptInfoDTO> getAllDeptInfo();

	List<TblDeptInfo> getDeptByDeptName(TblDeptInfo deptInfo);

	List<TblDeptInfo> getAllDeptByPage(Map<String, Object> map);
}
