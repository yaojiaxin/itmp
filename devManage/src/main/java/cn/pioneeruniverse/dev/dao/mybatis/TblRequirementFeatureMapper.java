package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.dev.entity.*;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblRequirementFeatureMapper extends BaseMapper<TblRequirementFeature> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblRequirementFeature record);

    TblRequirementFeature selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblRequirementFeature record);

    int updateByPrimaryKey(TblRequirementFeature record);

    List<DevTaskVo> getAllReqFeature(Map<String, Object> map);

    Map<String, Object> getOneDevTask(Long id);

    void insertReqFeature(TblRequirementFeature requirementFeature);

    void updateDevTask(TblRequirementFeature requirementFeature);

    List<DevTaskVo> getAll(DevTaskVo devTaskVo);

    List<TblDataDic> getAllReqFeatureStatus();

    int countTaskBySystemId(long id);

    List<Map<String, Object>> getSplitUser(Long systemId);

    List<TblRequirementFeature> findByName(TblRequirementFeature requirementFeature);

    List<Map<String, Object>> findBrother(Long requirementId, Long id);

    List<Map<String, Object>> findBrotherDiffWindow(DevTaskVo devTaskVo2);

    void updateStatus(Long id);

    String findMaxCode(Integer length);

    void updateTransfer(TblRequirementFeature requirementFeature);

    List<TblRequirementFeature> findSynDevTask(TblRequirementFeature requirementFeature);

    void updateTaskId(TblRequirementFeature requirementFeature);

    void updateSynStatus(Long id);

    //查询未关联信息
    List<TblRequirementFeature> findFrature(TblRequirementFeature requirementFeature);

    String getUserName(Integer manageUserId);

    String getDeptName(Integer deptId);

    //查询投产窗口关联开发任务状态
    List<TblRequirementFeature> findFratureStatusByWindow(@Param(value = "windowId") Long windowId,
                                                          @Param(value = "systemId") Long systemId);
    //查询系统关联冲刺关联开发任务状态
    List<TblRequirementFeature> findFratureStatusBySystemId(Long systemId);
    //根据开发管理岗查询开发任务
    List<DevTaskVo> findFeatureByManageUserId(Long userId);

    TblRequirementFeature getRequirementFeatureByDevTaskCode(String devTaskCode);

    Long getRequirementFeatureIdByDevTaskId(Long devTaskId);

    void changeCancelStatus(Long requirementId);

    List<TblRequirementFeature> findByrequirementId(Long requirementId);

    List<TblDefectInfo> findDftNoReqFeature(HashMap<String, Object> map);

    List<TblDefectInfo> findDftByReqFId(Long id);

    List<TblDefectInfo> findDftByReqFIds(Long []ids);


    void updateDftReqFIdNull(Long id);

    void updateReqNumberNull(Long id);

    void updateReqNull(Long id);

    List<Map<String, Object>> getAllFeature(Map<String, Object> map);

    List<TblRequirementFeature> getFeatureByREQCode(String requirementCode);

    /**
     * 查询开发任务 实施中和待实施
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> getAllRequirementFeature(Map<String, Object> map);

    List<TblRequirementFeature> getReqFeatureByReqCodeAndSystemId(@Param(value = "requirementCode") String requirementCode, @Param(value = "systemId") Long systemId);

    List<DevTaskVo> getDeplayReqFesture(Map<String, Object> map);

    void updateDeployStatus(Map<String, Object> map);

    TblRequirementFeature getFeatureByCode(String code);

    void findByReqFeatureIds(Map<String, Object> map);

    void updateDeployStatusOne(@Param(value = "id") Long id, @Param(value = "deployStatus") String status);

    String getDeployStatus(Long id);

    void synReqFeatureDeployStatus(Map<String, Object> map);

    void updateDeployStatusOneAdd(@Param(value = "id") Long id, @Param(value = "deployStatus") String status);

    String getFeatureName(Long requirementFeatureId);


    int getAllReqFeatureCount(Map<String, Object> map);
    /**
     * 查询开发任务 根据系统id 需求id 并且是同步任务
     * */
    List<TblRequirementFeature> selectBySystemIdAndReqId(@Param(value="systemId")Long systemId, @Param(value="requirementId")Long requirementId);

    TblRequirementFeature getFeatureById(Long requirementFeatureId);

    List<DevTaskVo> getPrdDeplayReqFesture(Map<String, Object> map);
    /**根据冲刺id查询开发任务
     * */
    List<DevTaskVo> getDevTaskBySprintIds(Long sprintId);

    void updateReFeatureStatus(@Param(value="id")Long id, @Param(value="requirementFeatureStatus")String requirementFeatureStatus);

    List<TblRequirementFeature> findFestureByWindowDate(@Param(value="windowDate") String windowDate,
                                                        @Param(value="systemId")Long systemId);

    List<DevTaskVo> getAllCondition(DevTaskVo devTaskVo);

    List<TblDataDic> getReqFeatureStatus();

    List<Map<String, Object>> getAllRequirementFeatureCondition(Map<String, Object> map);

    void updateSprints(@Param(value = "id")Long id, @Param(value="sprintId")Long sprintId, @Param(value="requirementFeatureStatus")String requirementFeatureStatus,@Param(value="executeUserId") Integer executeUserId);

    void synReqFeaturewindow(Map<String, Object> map);

    void updateGroupAndVersion(long parseLong, Long systemVersionId, Long executeProjectGroupId);



    List<DevTaskVo> getReqFeaByartId(Map<String, Object> map);

	void synReqFeatureDept(Map<String, Object> map);

	List<Map<String, Object>> getDevTaskBySystemAndRequirement(Map<String, Object> map);

    List<Map<String,Object>> getRequirementByCode(String requirementCode);

    List<Map<String, Object>> getDeptByName(String deptName);



    String getFeatureFieldTemplateById(@Param(value = "id")Long id, @Param(value = "fieldName")String fieldName);

    List<TblRequirementFeature> selectBySystemIdAndReqId1(@Param(value="systemId")Long systemId,
                                                          @Param(value="requirementId")Long requirementId);

    List<Map<String, Object>> getTreeName(Map<String, Object> map);

	List<Long> findWindowByReqId(Long requirementId);

    //计划弹窗总数
    int getAllProjectPlanCount(TblProjectPlan tblProjectPlan);
    //计划弹窗（bootstrap）
    List<Map<String, Object>> getAllProjectPlan(Map<String, Object> map);
}