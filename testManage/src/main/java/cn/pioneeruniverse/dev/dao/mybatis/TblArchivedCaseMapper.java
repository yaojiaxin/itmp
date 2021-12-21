package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;

import cn.pioneeruniverse.dev.entity.TblArchivedCase;
import cn.pioneeruniverse.dev.entity.TblCaseInfo;

public interface TblArchivedCaseMapper {

	void archivingCase(TblCaseInfo tblCaseInfo);

	List<Long> getArchivedCaseIds();

	void deleteArchivedCase(HashMap<String, Object> map);

	void updateArchivedCase(TblCaseInfo caseInfo);

	List<TblArchivedCase> getCaseInfos(HashMap<String, Object> map);

	TblArchivedCase getArchivedCaseById(Long id);

	void deleteArchivedCase(List<Long> ids);

}
