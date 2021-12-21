package cn.pioneeruniverse.project.service.systeminfo;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblSystemInfo;

public interface SystemInfoService {

	List<TblSystemInfo> selectSystemInfo(TblSystemInfo tblSystemInfo, Integer pageNumber, Integer pageSize);


	void updateSystemInfo(Long projectId, List<String> systemNames);


	List<String> selectSystemName(Long id);


	void updateSystemInfoBySystemName(String systemName);

}
