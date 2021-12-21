package cn.pioneeruniverse.dev.service.testSet;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import cn.pioneeruniverse.dev.entity.TblTestSet;
import cn.pioneeruniverse.dev.entity.TblTestSetCase;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStep;
import cn.pioneeruniverse.dev.entity.TblTestSetExcuteRoundUser;

public interface ITestSetService {

	Map<String, Object> getTestSet(Integer page, Integer rows, TblTestSet testSet, Integer tableType);

	Map<String, Object> getAllTestSet(String nameOrNumber,String createBy,String testTaskId);

	List<TblTestSet> getTestSetByFeatureId(Long featureId);

	void exportTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> importTestCase(MultipartFile file, Long testSetId, HttpServletRequest request) throws Exception;

	Map<String, Object> insertTestSet(TblTestSet tblTestSet, HttpServletRequest request);

	Map<String, Object> selectTestSetCaseByPage(Integer page, Integer rows, TblTestSetCase testSetCase);

	Map<String, Object> getCaseByTestSetId(Integer page, Integer pageSize, Long testSetId, Integer executeRound, String executeResult);

	void updateCase(TblTestSetCase testSetCase, HttpServletRequest request);

	void updateManyCase(String ids, HttpServletRequest request);

	void batchInsertCase(Long testSetId, String caseStr, HttpServletRequest request);
	
	public void batchInsertCaseStep(Long testSetId,String idStr, HttpServletRequest request);

	void updateTestSetStatus(Long id, Integer status, HttpServletRequest request);

	void addTestSetCase(TblTestSetCase testSetCase, List<TblTestSetCaseStep> testSetCaseStepList,
			HttpServletRequest request);

	void addExecuteUser(Long testSetId, Integer executeRound, String userIdStr, HttpServletRequest request);

	void updateExcuteUserStatus(Long id, HttpServletRequest request);

	void updateBatchStatus(String ids, HttpServletRequest request);

	Map<String, Object> getTestExcuteUser(Long testSetId, Integer excuteRound);

	Map<String, Object> getUserTable(Integer page, Integer rows, Long testSetId, Integer executeRound, String userName,
			String companyName,String deptName);

	List<Map<String, Object>> getTaskTree(Long userId,String taskName,String testSetName,Integer requirementFeatureStatus);
	
	Map<String, Object> getRelateSystem(Long testSetId);
	
	List<TblTestSetCaseStep> getTestSetCase(Long caseId);
}
