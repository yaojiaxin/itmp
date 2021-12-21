package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblDefectLog;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

public interface TblDefectLogMapper extends BaseMapper<TblDefectLog> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblDefectLog record);

    TblDefectLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectLog record);

    int updateByPrimaryKeyWithBLOBs(TblDefectLog record);

    int updateByPrimaryKey(TblDefectLog record);

    void insertDefectLog(TblDefectLog defectLog);

    TblDefectLog findLogByDefectId(Long defectId);

    TblDefectLog findLogByDidAndType(TblDefectLog defectLog);

    void updateLogById(TblDefectLog log);

    List<TblDefectLog> getDefectLogsById(Long defectId);

    Long getDefectMaxLogId(Long defectId);

    TblDefectLog getDefectRecentLogById(Long logId);
}