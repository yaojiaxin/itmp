package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDefectLogAttachement;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStep;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStepExecute;

public interface TblTestSetCaseStepExecuteMapper extends BaseMapper<TblTestSetCaseStepExecute>{

	int insertSelective(List<Map<String,String>> list);

    List<TblTestSetCaseStepExecute>  selectByPrimaryKey(Long id);
    
    
    int insertPass(List<TblTestSetCaseStep> list);
    

}