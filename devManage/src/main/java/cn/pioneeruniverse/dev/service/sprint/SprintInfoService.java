package cn.pioneeruniverse.dev.service.sprint;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.entity.TblSprintInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import org.apache.ibatis.annotations.Param;

public interface SprintInfoService {

	List<TblSystemInfo> getAllSystem();

	List<TblSprintInfo> getSprintInfos(String sprintName, Long uid, String systemIds, Integer validStatus, List<String> roleCodes, Integer page, Integer rows);

	void deleteSprintInfo(HttpServletRequest request, String sprintIdList);

	void addSprintInfo(TblSprintInfo sprintInfo, HttpServletRequest request);

	TblSprintInfo getSprintInfoById(Long id);

	void updateSprintInfo(TblSprintInfo sprintInfo, HttpServletRequest request);

	Integer getSprintInfosCount(String sprintName, Long uid, String systemIds, Integer validStatus,List<String> roleCodes);

	void closeSprint(String id, HttpServletRequest request);

	List<TblSprintInfo> getAllSprints(String sprintName, Long uid, String systemName, List<String> roleCodes,
			Integer pageNum, Integer pageSize);

	Integer getAllSprinsCount(String sprintName, Long uid, String systemName, List<String> roleCodes);

	void openSprint(String id, HttpServletRequest request);

	/**
	 *  根据冲刺id获取该冲刺相关信息
	 * @param sprintIdLists
	 * @return
	 */
	TblSprintInfo selectSprintInfoById(@Param("sprintIdLists") List<Object> sprintIdLists);

}
