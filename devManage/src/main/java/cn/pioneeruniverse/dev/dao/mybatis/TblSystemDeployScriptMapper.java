package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblSystemDeployScript;

public interface TblSystemDeployScriptMapper extends BaseMapper<TblSystemDeployScript>{
    int deleteByPrimaryKey(Long id);
    
    void deleteByBatchIds(@Param("list") List<Long> list);

    Integer insertScript(TblSystemDeployScript record);

    int updateByPrimaryKeySelective(TblSystemDeployScript record);
    
    List<TblSystemDeployScript> selectByDeployId(@Param("systemDeployId") Long systemDeployId);

	List<TblSystemDeployScript> selectScriptOrder(Long systemDeployId);

}