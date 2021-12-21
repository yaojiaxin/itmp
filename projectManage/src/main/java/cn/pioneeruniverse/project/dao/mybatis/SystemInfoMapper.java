package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblSystemInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface SystemInfoMapper extends BaseMapper<TblSystemInfo> {

	List<TblSystemInfo> selectSystemInfo(HashMap<String, Object> map);

	Integer selectCount();

	void updateSystemInfo(HashMap<String, Object> map);

	List<String> selectSystemName(Long id);

	void updateSystemInfoBySystemName(String systemName);

	void untyingSystem(Long id);

	List<TblSystemInfo> selectSystems(Long id);

	String selectSystemNameById(Long systemId);

	void updateSystem(HashMap<String, Object> map);

	Long findSystemIdBySystemCode(String systemCode);

	List<TblSystemInfo> getSystemsByProjectId(Long id);

	List<TblSystemInfo> getSystemsByWindowId(Long windowId);


}
