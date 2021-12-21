package cn.pioneeruniverse.project.service.dept.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.pioneeruniverse.project.dao.mybatis.DeptInfoMapper;
import cn.pioneeruniverse.project.service.dept.DeptInfoService;

@Service
public class DeptInfoServiceImpl implements DeptInfoService{

	@Autowired
	private DeptInfoMapper deptInfoMapper;
	
	@Override
	public Long selectDeptId(String deptName) {
		// TODO Auto-generated method stub
		return deptInfoMapper.selectDeptId(deptName);
		
	}

	@Override
	public String selectDeptName(Long deptId) {
		// TODO Auto-generated method stub
		return deptInfoMapper.selectDeptName(deptId);
	}

	@Override
	public List<String> selectAllDeptName() {
		// TODO Auto-generated method stub
		return deptInfoMapper.selectAllDeptName();
	}

	@Override
	public Long findIdByDeptNumber(String deptNumber) {
		return deptInfoMapper.findIdByDeptNumber(deptNumber);
	}

}
