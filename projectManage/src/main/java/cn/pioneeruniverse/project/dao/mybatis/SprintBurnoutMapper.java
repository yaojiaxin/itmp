package cn.pioneeruniverse.project.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.project.entity.TblSprintBurnout;

public interface SprintBurnoutMapper extends BaseMapper<TblSprintBurnout> {
	int deleteByPrimaryKey(Long id);

	Integer insert(TblSprintBurnout record);

	int insertSelective(TblSprintBurnout record);

	TblSprintBurnout selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblSprintBurnout record);

	int updateByPrimaryKey(TblSprintBurnout record);
}