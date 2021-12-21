package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDevTask;
import cn.pioneeruniverse.dev.entity.TblSystemJenkinsJobRun;
import cn.pioneeruniverse.dev.entity.TblSystemVersion;

public interface TblSystemJenkinsJobRunMapper extends BaseMapper<TblSystemJenkinsJobRun> {
	// Integer insert(TblSystemJenkinsJobRun record);
	int deleteByPrimaryKey(Long id);

	int insertSelective(TblSystemJenkinsJobRun record);

	TblSystemJenkinsJobRun selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblSystemJenkinsJobRun record);

	int updateByPrimaryKey(TblSystemJenkinsJobRun record);

	List<TblSystemJenkinsJobRun> selectLastTimeBySystemId(Long id);

	List<TblSystemJenkinsJobRun> selectNowTimeBySystemId(Long id);

	List<TblSystemJenkinsJobRun> selectLastTimeByJobName(String jobName);

	List<TblSystemJenkinsJobRun> selectByParam(Map<String, Object> map);

	List<Map<String, Object>> selectMessageBySystemId(Map<String, Object> map);

	List<Map<String, Object>> selectMessageBySystemIdAndPage(Map<String, Object> map);

	void updateJobRun(TblSystemJenkinsJobRun record);

	List<Map<String, Object>> selectModuleBuildMessage(Map<String, Object> map);

	List<Map<String, Object>> selectModuleBuildMessageNow(Map<String, Object> map);

	List<Map<String, Object>> selectModuleBuildMessagesNow(Map<String, Object> map);

	List<TblSystemJenkinsJobRun> selectLastTimeBySystemIdManual(Long id);

	List<TblSystemJenkinsJobRun> selectLastTimeBySystemIdManualDeploy(Long id);

	List<TblSystemJenkinsJobRun> getErrorStructure(Map<String, Object> paramMap);

	List<TblSystemJenkinsJobRun> getAutoErrorStructure(Map<String, Object> paramMap);

	List<TblSystemJenkinsJobRun> selectByMapLimit(Map<String, Object> runMap);
	
	// 根据用户查询所关联的构建日志
	List<TblSystemJenkinsJobRun> findJenkinsJobRunByUser(Long userId);
	// 查询系统最近14天每天的构建次数
	List<TblSystemJenkinsJobRun> find14DayJenkinsCountBySystemId(Long systemId);
	
	// 查询系统最近14天每天的构建时间
	List<TblSystemJenkinsJobRun> find14DayJenkinsMinuteBySystemId(Long systemId);
	// 查询系统最近5条构建记录
	List<Map<String, Object>> findTop5JenkinsBySystemId(Long systemId);
	// 查询系统最近N天的构建数量
	TblSystemJenkinsJobRun findJenkinsCountBySystemId(@Param(value="systemId")Long systemId,
			@Param(value="day")Long day);
	
	// 查询系统最近7天的构建情况
	List<TblSystemJenkinsJobRun> find7DayJenkinsBySysyemId(Long projectId);
	
	// 查询该条构建是否是该系统下的最新一条自动构建记录
	Long findTop1AutomaticJenkinsBySystemId(Map<String, Object> map);
	// 查询该条构建是否是该系统下的最新一条手动构建记录
	Long findTop1CustomJenkinsBySystemId(Map<String, Object> map);

	List<TblSystemJenkinsJobRun> selectAutoDeployLastTimeBySystemId(Long id);

	List<Map<String, Object>> selectModuleBuildMessagesNowAutoDeploy(Map<String, Object> moduleParam);

	List<Map<String, Object>> selectModuleBuildMessageAutoDeploy(Map<String, Object> moduleParam);

	List<Map<String, Object>> selectMessageBySystemIdAndPageAutoDeploy(Map<String, Object> mapParam);

	Integer insertNew(TblSystemJenkinsJobRun tblSystemJenkinsJobRun);

	List<TblSystemJenkinsJobRun> selectAutoDeployLastTimeBySystemIdArtifact(Long id);

	List<TblSystemJenkinsJobRun> getAutoBreakeErrorStructure(Map<String, Object> paramMap);

	List<TblSystemJenkinsJobRun> getErrorBreakeStructure(Map<String, Object> paramMap);

    List<Map<String, Object>> getEnvName(Map<String, Object> paramMap);

	List<Map<String, Object>> getDeleteEnvName(Map<String, Object> param);

    List<Map<String, Object>> getModuleInfoIng(Map<String, Object> moduleParam);

	List<Map<String, Object>> selectModuleRunInfo(Map<String, Object> moduleParam);

    List<Map<String, Object>> selectModuleBuildMessagesNowAutoDeployNew(Map<String, Object> moduleParam);

    List<Map<String, Object>> selectModuleBuildMessageAutoDeployNew(Map<String, Object> moduleParam);
}