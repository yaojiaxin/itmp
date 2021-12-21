package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.pioneeruniverse.dev.entity.TblCaseInfo;

public interface TblCaseInfoMapper {

	List<TblCaseInfo> getCaseInfos(HashMap<String, Object> map);

	void insertCaseInfo(TblCaseInfo tblCaseInfo);

	void deleteCaseInfo(HashMap<String, Object> map);

	String selectMaxCaseNumber();

	TblCaseInfo findCaseInfoById(Long id);

	List<TblCaseInfo> getCaseByIds(List<Long> ids);

	List<Map<String, Object>> getCaseInfo(@Param("testSetId")Long testSetId,@Param("caseCode") String caseCode,@Param("caseName") String caseName);

	void updateCaseInfo(TblCaseInfo caseInfo);

	Long getIdByCaseNumber(String caseNumber);

	void updateArchiveStatus(HashMap<String, Object> map);

	void updateCaseArchivedStatus(HashMap<String, Object> map);

	void updateCase(Map<String, Object> result);

	String getCaseFieldTemplateById(@Param("id")Long id, @Param("fieldName")String fieldName);

}