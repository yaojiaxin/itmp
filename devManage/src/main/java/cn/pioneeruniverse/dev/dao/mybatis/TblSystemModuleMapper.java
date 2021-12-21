package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblSystemModule;

public interface TblSystemModuleMapper extends BaseMapper<TblSystemModule> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblSystemModule record);

    TblSystemModule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblSystemModule record);

    int updateByPrimaryKey(TblSystemModule record);

	List<Map<String, Object>> getSystemModuleBySystemId(Long id);
	
	List<TblSystemModule> selectSystemModule(Long systemid);

	Map<String, Object> getOneSystemModule(Long id);

	void insertModule(TblSystemModule systemModule);

	List<TblSystemModule> sortModule(Map<String, Object> result);


}