package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.common.dto.TblAttachementInfoDTO;
import cn.pioneeruniverse.dev.entity.TblDefectAttachement;
import cn.pioneeruniverse.dev.entity.TblDefectInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

public interface TblDefectAttachementMapper  extends BaseMapper<TblDefectAttachement> {
    int deleteByPrimaryKey(Long id);

    void insertDefectAttachement(TblDefectAttachement defectAttachement);

    TblDefectAttachement selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectAttachement record);

    int updateByPrimaryKey(TblDefectAttachement record);

    TblDefectAttachement getDefectAttByUrl(String path);

    List<TblDefectAttachement> findAttListByDefectId(Long defectId);

    void removeAttachement(TblDefectAttachement defectAttachement);

    void removeAttachements(Long defectId);

    TblDefectAttachement getAttById(Long id);

    void insertDefectDTOAttachement(TblAttachementInfoDTO attachementInfoDTO);
}