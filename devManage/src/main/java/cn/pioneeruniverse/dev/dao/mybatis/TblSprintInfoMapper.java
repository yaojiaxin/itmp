package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblSprintInfo;
import org.apache.ibatis.annotations.Param;

public interface TblSprintInfoMapper extends BaseMapper<TblSprintInfo>{
    int deleteByPrimaryKey(Long id);

    Integer insert(TblSprintInfo record);

    int insertSelective(TblSprintInfo record);


    TblSprintInfo selectByPrimaryKey(Long id);

	/**
	 *  根据冲刺id获取该冲刺相关信息
	 * @param sprintIdLists
	 * @return
	 */
	TblSprintInfo selectSprintInfoById(@Param("sprintIdLists") List<Object> sprintIdLists);


	int updateByPrimaryKeySelective(TblSprintInfo record);

    int updateByPrimaryKey(TblSprintInfo record);

	List<TblSprintInfo> getAllSprint();

	List<TblSprintInfo> getSprintInfos(HashMap<String, Object> map);

	void deleteSprintInfo(HashMap<String, Object> map);

	void addSprintInfo(TblSprintInfo sprintInfo);

	Integer getSprintInfosCount(HashMap<String, Object> map);

	List<TblSprintInfo> getSprintBySystemId(Long systemId);

	void closeSprint(HashMap<String, Object> map);

	List<TblSprintInfo> getSprintInfoCondition(HashMap<String, Object> map);

	Integer getSprintInfosCountCondition(HashMap<String, Object> map);

	void openSprint(HashMap<String, Object> map);

	List<TblSprintInfo> findSprintBySystemIdDate(Long systemId);

    List<TblSprintInfo> getSprintBySystemIdBefor(Long systemId);

	List<TblSprintInfo> getSprintBySystemIdAfter(Long systemId);

    List<TblSprintInfo> getSprintBySystemIdAndName(TblSprintInfo tblSprintInfo);

    //根据系统id和项目id获取冲刺信息
	List<TblSprintInfo> getSprintInfoListBySystemIdAndProjectId(@Param("systemId") Long systemId, @Param("projectId") Long projectId);

}