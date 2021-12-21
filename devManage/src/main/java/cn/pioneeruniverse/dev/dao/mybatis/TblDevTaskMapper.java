package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDataDic;
import cn.pioneeruniverse.dev.entity.TblDevTask;
import org.apache.ibatis.annotations.Param;

public interface TblDevTaskMapper extends BaseMapper<TblDevTask> {

   /* int insertSelective(TblDevTask record);*/

    TblDevTask selectByPrimaryKey(Long id);

    TblDevTask selectByDevTaskCode(String devTaskCode);

    int updateByPrimaryKeySelective(TblDevTask record);

    int updateByPrimaryKey(TblDevTask record);

    String getFeatureNameById(Long id);

    List<Map<String, Object>> findByReqFeature(Long id);

    void insertWorkTask(TblDevTask devTask);

    List<TblDevTask> getDevTask(Map<String, Object> map);

    List<TblDevTask> getDevTaskAll(Map<String, Object> map);

    /**
     * @param
     * @return
     *//*
    List<Map<String, Object>> getAllFeature(Map<String, Object> map);*/
    List<TblDevTask> getAllDevUser(Long id);

    // List<Map<String, Object>> getAllDevUser2(Map<String, Object> map);
    List<TblDevTask> getAllRequirt();


    List<TblDevTask> getUseName();

    void addDevTask(TblDevTask devTask);

    void addDefectDevTask(TblDevTask devTask);

    void updateDevTask(TblDevTask devTask);

    List<TblDevTask> getEditDevTask(Long id);

    void updateCommssioningWindowId(TblRequirementFeature requirementFeature);

    //处理
    void Handle(TblDevTask devTask);

    //评审
    void CodeHandle(TblDevTask devTask);

    //待处理
    void DHandle(TblDevTask devTask);

    //评审通过
    void reviewAdopt(String id);

    //评审通过
    void reviewNAdopt(String id);

    //修改关联任务
    void editFeature(Long id);

    TblDevTask getDevUser(Long testid);

    void assigDev(TblDevTask devTask);

    List<Map<String, Object>> getTaskListByDevUserId(Long devUserId);

    Map<String,Object> getTaskDetailById(Long id);

    int finishTask(TblDevTask tblDevTask);

    String DevfindMaxCode(Integer length);

    //根据开发人员id查询工作任务 
    List<TblDevTask> findTaskByUser(Long userId);

    //根据开发人员id查询工作任务 
    List<TblDevTask> findDevTaskByDevUser(Long userId);

    List<TblDevTask> findSystemByUser(Long userId);

    List<TblDevTask> findByReqFeatureId(Long synId);

    void updateReqFeatureId(Map<String, Object> map2);

    TblDevTask getDevOld(Long id);

    //开发任务取消
    void updateStatus(Long requirementFeatureId);

    TblDevTask getDevTaskByCode(String code);

    TblDevTask getDevTaskById(Long devTaskId);

    Integer checkMySelectedTask(@Param("userAccount") String username, @Param("taskId") Long taskId);

    List<Map<String, Object>> countWorkloadBysystemId(Long systemId);

    //根据开发任务id统计各状态工作任务的数量
    List<Map<String, Object>> getStatusCount(Long id);

    int updateCodeReviewStatus(TblDevTask tblDevTask);

    List<TblDevTask> findByReqFeature2(Long id);

    List<TblDevTask> getWorkTaskBySprintId(Long sprintId);

    List<TblDataDic> getWorTaskStatus();

    void updateWorkTaskStatus(@Param("id") Long id, @Param("devTaskStatus") Integer devTaskStatus);

    Integer selectSystemCodeReview(Long systemId);

    void updateSprintId(TblRequirementFeature requirementFeature);

    String getDevTaskFieldTemplateById(@Param("id")Long id,@Param("fieldName") String fieldName);


    void updateDevTaskNew(TblDevTask devTask);

    List<String> selectIdBySprintId(Long sprintId);

    List<String> selectIdByWindowId(Map<String, Object> param);
}