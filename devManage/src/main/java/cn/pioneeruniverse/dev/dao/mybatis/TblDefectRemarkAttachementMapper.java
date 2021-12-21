package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblDefectRemarkAttachement;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblDefectRemarkAttachementMapper extends BaseMapper<TblDefectRemarkAttachement> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblDefectRemarkAttachement record);

    TblDefectRemarkAttachement selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectRemarkAttachement record);

    int updateByPrimaryKey(TblDefectRemarkAttachement record);

    void removeRemarkAttachements(Long[] remarkId);

	List<TblDefectRemarkAttachement> getRemarkAttsByDefectId(Long defectId);

	void addRemarkAttachement(List<TblDefectRemarkAttachement> files);
}