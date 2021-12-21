package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblProjectChangeInfo;

public interface TblProjectChangeInfoMapper {

	List<TblProjectChangeInfo> getChanges(Long projectId);

	void deleteProjectChange(TblProjectChangeInfo tblProjectChangeInfo);

	void insertProjectChange(TblProjectChangeInfo tblProjectChangeInfo);

	TblProjectChangeInfo getProjectChangeById(Long id);

	void updateProjectChange(TblProjectChangeInfo tblProjectChangeInfo);

	List<TblProjectChangeInfo> getChangesByProgram(Long programId);

}
