package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDeptInfo;
import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import cn.pioneeruniverse.dev.entity.TblRequirementInfo;
import cn.pioneeruniverse.dev.vo.RequirementVo;
import cn.pioneeruniverse.dev.vo.TestTaskVo;

public interface TblRequirementFeatureMapper extends BaseMapper<TblRequirementFeature>{
	 int deleteByPrimaryKey(Long id);

	    int insertSelective(TblRequirementFeature record);

	    TblRequirementFeature selectByPrimaryKey(Long id);

	    int updateByPrimaryKeySelective(TblRequirementFeature record);

	    int updateByPrimaryKey(TblRequirementFeature record);

		List<TestTaskVo> getAllReqFeature(Map<String, Object> map);

		Map<String, Object> getOneTestTask(Long id);

		void insertReqFeature(TblRequirementFeature requirementFeature);

		void updateTestTask(TblRequirementFeature requirementFeature);

		List<TestTaskVo> getAll(TestTaskVo TestTaskVo);

		//List<TblDataDic> getAllReqFeatureStatus();
		int countTaskBySystemId(long id);

		List<Map<String, Object>> getSplitUser(Long systemId);

		List<TblRequirementFeature> findByName(TblRequirementFeature requirementFeature);

		List<Map<String, Object>> findBrother(Long requirementId, Long id);

		List<Map<String, Object>> findBrotherDiffWindow(TestTaskVo TestTaskVo2);

		void updateStatus(@Param("status")String status,@Param("id")Long id);

		String findMaxCode(Integer length);

		List<RequirementVo> getAllReq(RequirementVo requirementVo);
		
		List<Map<String, Object>> selectTaskByTestSetCon(@Param("nameOrNumber") String nameOrNumber,@Param("createBy") List<Long> createBy,
		@Param("testTaskId") List<Long> testTaskId,@Param("taskId") Long taskId);

		void updateTransfer(TblRequirementFeature requirementFeature);

		List<TblRequirementFeature> findSynDevTask(TblRequirementFeature requirementFeature);

		void updateTaskId(TblRequirementFeature requirementFeature);

		void updateSynStatus(Long synId);

		String getUserName(Long id);

		String getDeptName(Long deptId);

		int findDefectNum(@Param("id")Long id, @Param("status")int status);
		
		 /**
	     * 查询开发任务 实施中和待实施
	     * @param map
	     * @return
	     */
	    List<Map<String, Object>> getAllRequirementFeature(Map<String, Object> map);
	    
	    List<Map<String, Object>> getRequirementFeature(Map<String, Object> map);
		void changeCancelStatus(Long requirementId);

		List<TblRequirementFeature> findByrequirementId(Long requirementId);

		void synReqFeatureDeployStatus(Map<String, Object> map);

		void updateDeployStatus(@Param(value="id")Long reqFeatureId, @Param(value="deployStatus")String deployStatus);

   		 String getFeatureName(Long testTaskId);
   		 
   		 List<TblRequirementFeature> getTaskTree(@Param("userId")Long userId,@Param("taskName")String taskName,@Param("testSetName")String testSetName,@Param("requirementFeatureStatus")Integer requirementFeatureStatus);

    TblRequirementFeature getFeatureByTestTaskId(Long testTaskId);

	int findTestCaseNum(@Param("id")Long id, @Param("status")int status);

	List<TblRequirementFeature> selectBySystemIdAndReqId(@Param("systemId")Long systemId,@Param("requirementId") Long requirementId);

	List<TestTaskVo> getAllCondition(TestTaskVo testTaskVo);
	
	TblRequirementFeature getPlanDate(Long devId);

	String getFeatureFieldTemplateById(@Param("id")Long id,@Param("fieldName")String fieldName);

	void synReqFeaturewindow(Map<String, Object> map);

	TblRequirementFeature findRequirement(Long devID);

	Long getDesignCaseNumber(Long id);

	Long getExecuteCaseNumber(Long id);

	Long getDefectNumber(Long id);

	Long getDesignCaseNumber2(Long id);

	Long getExecuteCaseNumber2(Long id);

	Long getDefectNumber2(Long id);

	Double getWorkload(Long id);

	Double getWorkload2(Long id);

	void synReqFeatureDept(Map<String, Object> map);

	int getTestCaseNum(Long id);
	
	List<Map<String, Object>> selectDevVertionReport(@Param("startDate") String startDate,@Param("endDate") String endDate);

	int getCountBySystemIdAndRequirementId(Map<String, Object> map2);

	TblRequirementFeature findSelf(Map<String, Object> map);

	TblRequirementFeature findSyn(Map<String, Object> map);

	void synRequirementFeature(Map<String, Object> map2);

	void updateSynFeature(Long id);

	Long findDeptIdByDeptNumber(String developmentDeptNumber);

	TblDeptInfo findDeptNumberByRequirementId(Long id);

	List<TblRequirementFeature> selectBySystemIdAndReqId1(@Param("systemId")Long systemId,
														  @Param("requirementId") Long requirementId);

	List<Integer> selectTestStageByReqFeaId(Long id);

	List<Integer> selectTestTaskStatusByReqFeaId(Long id);

	List<Integer> selectTestTaskStatusByReqFeaId2(Long id);

	TblRequirementFeature getReqFeaByTaskId(Long id);

	Date getActualStartDate(@Param("id")Long id,@Param("testStage") int i);

	Date getActualEndDate(@Param("id")Long id, @Param("testStage")int i);

	Double getActualWorkoad(@Param("id")Long id, @Param("testStage") int i);

}