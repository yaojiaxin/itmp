package cn.pioneeruniverse.project.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import cn.pioneeruniverse.project.entity.TblRequirementFeatureTimeTrace;


public interface RequirementFeatureTimeTraceMapper extends BaseMapper<TblRequirementFeatureTimeTrace>{

    Integer insertFeatureTimeTrace(TblRequirementFeatureTimeTrace record);
}