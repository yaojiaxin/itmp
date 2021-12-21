package cn.pioneeruniverse.project.service.dept;

import java.util.List;

public interface DeptInfoService {

	Long selectDeptId(String deptName);

	String selectDeptName(Long deptId);

	List<String> selectAllDeptName();

	Long findIdByDeptNumber(String deptNumber);

}
