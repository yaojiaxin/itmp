package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblProjectSystem;

public interface TblProjectSystemMapper {

	void insertProjectSystem(TblProjectSystem projectSystem);

	List<Long> getDevelopSystemIds(Long id);

	List<Long> getPeripheralSystemIds(Long id);

	void updateProjectSystem(Long id);

	List<String> getSystemNames(Long id);

	void deleteProjectSystem(Long id);

}
