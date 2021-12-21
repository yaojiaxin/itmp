package cn.pioneeruniverse.dev.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblProjectSchedulePlanning;

public interface TblProjectSchedulePlanningMapper extends BaseMapper<TblProjectSchedulePlanning> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblProjectSchedulePlanning record);

    TblProjectSchedulePlanning selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblProjectSchedulePlanning record);

    int updateByPrimaryKey(TblProjectSchedulePlanning record);
}