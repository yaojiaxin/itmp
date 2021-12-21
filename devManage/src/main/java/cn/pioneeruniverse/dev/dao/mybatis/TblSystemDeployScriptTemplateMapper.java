package cn.pioneeruniverse.dev.dao.mybatis;


import cn.pioneeruniverse.dev.entity.TblSystemDeployScriptTemplate;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TblSystemDeployScriptTemplateMapper extends BaseMapper<TblSystemDeployScriptTemplate>{
    int deleteByPrimaryKey(Long id);

    void deleteByBatchIds(@Param("list") List<Long> list);

    Integer insertScriptTemplate(TblSystemDeployScriptTemplate record);

    int updateByPrimaryKeySelective(TblSystemDeployScriptTemplate record);



}