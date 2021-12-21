package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

public interface DeptInfoMapper {

	List<Long> selectDeptIds(String deptName);

	Long selectDeptId(String deptName);
	
	String selectDeptName(Long deptId);

	List<String> selectAllDeptName();
	/*同步时查询部门信息，无视状态*/
	Long findIdByDeptNumber(String deptNumber);

	String getDeptNumById(Long deptId);

	Long getDeptIdByNumber(String deptNumber);
}
