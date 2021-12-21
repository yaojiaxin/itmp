package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.dev.entity.TblDevTaskLog;

public interface TblDevTaskLogMapper {

    int insertAdd(TblDevTaskLog record);


   List<TblDevTaskLog>  selectByPrimaryKey(Long id);

    
    void updateLogById(TblDevTaskLog log);
    
    void insertDefectLog(TblDevTaskLog defectLog);
}
