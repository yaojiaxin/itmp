package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.TblAttachementInfoDTO;
import cn.pioneeruniverse.dev.entity.TblDevTaskAttachement;

public interface TblDevTaskAttachementMapper {
    //int deleteByPrimaryKey(Long id);

    void addAttachement(List<TblDevTaskAttachement> list);

    //int insertSelective(TblTestTaskAttachement record);

    List<TblDevTaskAttachement> selectByPrimaryKey(Long devId);

    //int updateByPrimaryKeySelective(TblTestTaskAttachement record);

    int updateByPrimaryKey(TblDevTaskAttachement record);
    void updateNo(Map<String, Object> map);

    void addworkTaskAttachement(TblAttachementInfoDTO attachementInfoDTO);
}