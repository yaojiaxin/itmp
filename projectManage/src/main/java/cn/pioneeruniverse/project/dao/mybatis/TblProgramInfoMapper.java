package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblProgramInfo;
import cn.pioneeruniverse.project.entity.TblProgramProject;
import cn.pioneeruniverse.project.entity.TblProjectInfo;

public interface TblProgramInfoMapper {

	Long getCountProgram();

	void insertProgram(TblProgramInfo programInfo);

	void insertProgramProject(TblProgramProject programProject);

	List<TblProgramInfo> getAllPrograms(Map<String, Object> map);

	List<TblProgramInfo> getProgramsByUid(Map<String, Object> map);

	TblProgramInfo getProgramById(Long id);

	List<TblProjectInfo> getProjectsByProgramId(Long id);

	void updateProgram(TblProgramInfo tblProgramInfo);

	void updateProgramProject(Long id);

}
