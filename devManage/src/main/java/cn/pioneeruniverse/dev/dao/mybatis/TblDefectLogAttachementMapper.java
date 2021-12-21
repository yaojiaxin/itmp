package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblDefectLogAttachement;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

public interface TblDefectLogAttachementMapper extends BaseMapper<TblDefectLogAttachement> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblDefectLogAttachement record);

    TblDefectLogAttachement selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectLogAttachement record);

    int updateByPrimaryKey(TblDefectLogAttachement record);

    List<TblDefectLogAttachement> selectLogAttachementBylogId(Long logId);
}