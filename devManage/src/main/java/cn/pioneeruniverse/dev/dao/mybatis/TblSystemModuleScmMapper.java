package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblSystemModuleScm;
import cn.pioneeruniverse.dev.entity.TblSystemScm;

public interface TblSystemModuleScmMapper extends BaseMapper<TblSystemModuleScm> {

    int deleteByPrimaryKey(Long id);

    int insertSelective(TblSystemModuleScm record);

    TblSystemModuleScm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblSystemModuleScm record);

    int updateByPrimaryKey(TblSystemModuleScm record);
    List<TblSystemModuleScm> getModuleScmByParam(Map<String,Object> map);

	List<TblSystemModuleScm> findModuleScm(Long id);

	void delete(Long systemModuleId);

	void insertModuleScm(TblSystemModuleScm moduleScm);

	void deleteBySystemId(Long id);

	void updateNo(Long systemModuleId);

	void updateYes(TblSystemModuleScm moduleScm);

	List<TblSystemModuleScm> judge(Map<String, Object> map);

	void deleteBySystemScmId(TblSystemScm tblSystemScm);

	void updateModuleStatusBySystemId(Map<String, Object> map);

	void updateModuleScm(Map<String, Object> moduleScmid);




	List<TblSystemModuleScm> getModuleScmBySystemId(Long systemId);
	void deleteModuleScmById(TblSystemModuleScm modelScm);
	void insertModuleScm1(TblSystemModuleScm modelScm);
	void updateModuleScm1(TblSystemModuleScm modelScm);
    Integer findModuleScmByEnvironmentType(TblSystemModuleScm modelScm);
    TblSystemModuleScm findModuleScmDoesItExist(TblSystemModuleScm modelScm);
	List<TblSystemModuleScm> findModuleScmBySystemId(Long systemId);

}