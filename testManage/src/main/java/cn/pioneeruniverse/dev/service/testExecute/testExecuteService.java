package cn.pioneeruniverse.dev.service.testExecute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.dev.entity.TblTestSetCase;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseExecute;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStep;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStepExecute;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseVo;

import javax.servlet.http.HttpServletRequest;

/**
*  测试案例执行 
* @author:xukai
* @version:2018年12月5日 下午3:13:08 
*/
public interface testExecuteService {

	/**
	 * 查询测试案例批量执行列表
	 * @param Execute
	 * @param userId
	 * @param rows
	 * @param page
	 * @return
	 */
	 
	List<Map<String, Object>> selectByPrimaryKey(String testSetId,int excuteRound,String caseExecuteResultList,Integer pageNumber,Integer pageSize);
	 /**
	  * 批量执行
	  * @param allExecuteDate
	  * @param userId
	  */
	 void uodateTestCaseExecute(String allExecuteDate,Long userId);
	/**
	 * 测试案例执行弹窗
	 * @param id
	 * @return
	 */
	 List<TblTestSetCaseStep> selectTestCaseRun(Long id);
	 /**
	  * 添加测试执行
	  * @param rows
	  * @param type
	  * @param enforcementResults
	  * @param testUserId
	  */
	 String insertSelective(String rows,String type,String enforcementResults,String excuteRemark,int excuteRound,Long testUserId,Long testSetId,String files);
	 /**
	  * 查询执行详情信息
	  * @param testCaseExecuteId
	  * @return
	  */
	 Map<String, Object> selectTestExecute(Long testCaseExecuteId,Long testSetCaseId);
	 /**
	  * 查询测试执行列表信息
	  * @param testSetId
	  * @param executeRound
	  * @return
	  */
	 List<TblTestSetCaseExecute> selectTestCaseExecute(String myData);
	 /**
	  * 删除附件
	  * @param id
	  * @param userId
	  */
	 void delecteFile(Long id,Long userId);
	 
	 Map<String, Object> selectUpdateCase(Long testSetCaseId);
	 
	 void updateCaseStep(String testSetCase,String testCaseStep,Long testUserId) throws Exception;
	 
	 
	 List<TblTestSetCaseVo> findExeTestSetByexcuteRound(Long testSetId,int excuteRound);
	 
	 List<TblTestSetCaseVo> findExeTestSet(Long testSetId,int excuteRound[]);
	 
	 List<TblTestSetCaseExecute>  selectByTestSet(Long testSetId);
	Long getCaseExecuteResult(Long testSetId, String caseNumber);
	List<TblTestSetCaseExecute> getCaseExecute(Long testSetId, String caseNumber);
}
