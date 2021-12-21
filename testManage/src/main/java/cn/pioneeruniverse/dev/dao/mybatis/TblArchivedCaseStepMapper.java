package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.dev.entity.TblArchivedCase;
import cn.pioneeruniverse.dev.entity.TblArchivedCaseStep;
import cn.pioneeruniverse.dev.entity.TblCaseStep;

public interface TblArchivedCaseStepMapper {

	void archivingCaseStep(TblCaseStep tblCaseStep);

	void updateArchivedCaseStepByCseId(HashMap<String, Object> map);

	void insertArchivedCaseStep(TblCaseStep tblCaseStep);

	List<TblArchivedCaseStep> findCaseStepByCaseId(Long id);

	void deleteArchivedCaseStep(List<Long> ids);

	List<Long> getStepIdsByCaseId(Long id);

	void updateStep(TblCaseStep tblCaseStep);

	void deleteCaseSteps(Map<String, Object> map);

}
