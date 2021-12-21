package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblDefectRemark;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblDefectRemarkMapper extends BaseMapper<TblDefectRemark> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblDefectRemark record);

    TblDefectRemark selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectRemark record);

    int updateByPrimaryKey(TblDefectRemark record);

    void insertDefectRemark(TblDefectRemark defectRemark);

    Long[] findRemarkByDefectId(Long id);

    void removeDefectRemark(Long[] remarkId);

	List<TblDefectRemark> getRemarkByDefectId(Long defectId);
}