package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.dev.entity.TblDefectLog;
import cn.pioneeruniverse.dev.entity.TblTestTaskLog;

public interface TblTestTaskLogMapper {

    int insertAdd(TblTestTaskLog record);


   List<TblTestTaskLog>  selectByPrimaryKey(Long id);

    
    void updateLogById(TblDefectLog log);
    
    void insertDefectLog(TblTestTaskLog defectLog);
}
