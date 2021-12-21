package cn.pioneeruniverse.dev.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblProjectResourcePlanning;

public interface TblProjectResourcePlanningMapper extends BaseMapper<TblProjectResourcePlanning> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblProjectResourcePlanning record);

    TblProjectResourcePlanning selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblProjectResourcePlanning record);

    int updateByPrimaryKey(TblProjectResourcePlanning record);
}