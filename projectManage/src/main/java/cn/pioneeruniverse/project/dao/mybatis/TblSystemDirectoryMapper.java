package cn.pioneeruniverse.project.dao.mybatis;


import cn.pioneeruniverse.project.entity.TblSystemDirectory;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblSystemDirectoryMapper extends BaseMapper<TblSystemDirectory> {

	List<TblSystemDirectory> getSystemDirectoryBySystemId(Long systemId);

	void insertSystemDir(TblSystemDirectory systemDirectory);

	void updateSystemDIr(Map<String, Object> map);

	Long getId(Map<String, Object> map);

	void updateSystemDIr2(Map<String, Object> map);

}