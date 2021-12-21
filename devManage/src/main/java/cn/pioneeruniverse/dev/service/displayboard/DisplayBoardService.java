package cn.pioneeruniverse.dev.service.displayboard;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.entity.DevTaskVo;
import cn.pioneeruniverse.dev.entity.TblDataDic;
import cn.pioneeruniverse.dev.entity.TblDevTask;
import cn.pioneeruniverse.dev.entity.TblProjectInfo;
import cn.pioneeruniverse.dev.entity.TblSprintInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;

/**
*  类说明 
* @author:tingting
* @version:2019年3月20日 下午1:36:51 
*/
public interface DisplayBoardService {

	List<TblProjectInfo> getAllProjectByUid(Long uid);

	List<TblSystemInfo> getSystemByPId(Long projectId);

	List<TblSprintInfo> getSprintBySystemId(Long systemId);

	List<DevTaskVo> getDevTaskBySprintId(Long sprintId);

	List<TblDevTask> getDevTaskByReqFeatureId(Long id);

	void updateDevTaskStatus(Long reqFeatureId, String status, HttpServletRequest request);

	List<Map<String, Object>> getWorkTaskStatusCount(Long id);

	List<TblDataDic> getReqFeatureStatus();

	List<TblDevTask> getWorkTaskBySprintId(Long sprintId);

	List<TblDataDic> getWorTaskStatus();

	void updateWorkTaskStatus(Long devTaskId, Integer status, HttpServletRequest request);

	String getProjectGroupByProjectId(long projectId);

    String getProjectGroupByProjectIdNoWu(long projectId);
}
