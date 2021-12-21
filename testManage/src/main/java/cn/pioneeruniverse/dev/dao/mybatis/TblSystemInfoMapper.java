package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.entity.TblTestTask;

public interface TblSystemInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TblSystemInfo record);

    int insertSelective(TblSystemInfo record);

    TblSystemInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblSystemInfo record);

    int updateByPrimaryKey(TblSystemInfo record);

	List<Map<String, Object>> getAllSystemInfo(Map<String, Object> map);

	List<Map<String, Object>> getAllSystemInfoModal(Map<String, Object> map);

	List<TblSystemInfo> getAllsystem(TblSystemInfo system);

	List<TblSystemInfo> getAll();

    String getSystemNameById(Long id);

	String getSystemName(Long id);

	List<TblSystemInfo> getSystems();

	List<Long> getSystemIdBySystemName(String value);
	
	List<Long> getSystemIdBySystemName2(@Param("systemName") String systemName);

    int countGetAllSystemInfo(Map<String,Object> map);

    Long findSystemIdBySystemCode(String systemCode);
}