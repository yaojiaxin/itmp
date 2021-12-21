package cn.pioneeruniverse.project.service.systeminfo.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.pioneeruniverse.project.dao.mybatis.SystemInfoMapper;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.service.systeminfo.SystemInfoService;

@Service
public class SystemInfoServiceImpl implements SystemInfoService {
	
	@Autowired
	private SystemInfoMapper systemInfoMapper;

	@Override
	public List<TblSystemInfo> selectSystemInfo(TblSystemInfo tblSystemInfo, Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		Integer start = (pageNumber-1)*pageSize;
		map.put("start", start);
		map.put("pageSize", pageSize);
		map.put("tblSystemInfo", tblSystemInfo);
		return systemInfoMapper.selectSystemInfo(map);
	}


	@Override
	public void updateSystemInfo(Long projectId, List<String> systemNames) {
		// TODO Auto-generated method stub
		HashMap<String,Object> map = new HashMap<>();
		map.put("projectId", projectId);
		map.put("systemNames", systemNames);
		systemInfoMapper.updateSystemInfo(map);
		
	}


	@Override
	public List<String> selectSystemName(Long id) {
		// TODO Auto-generated method stub
		return systemInfoMapper.selectSystemName(id);
	}


	@Override
	public void updateSystemInfoBySystemName(String systemName) {
		// TODO Auto-generated method stub
		systemInfoMapper.updateSystemInfoBySystemName(systemName);
	}

}
