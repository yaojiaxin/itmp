package cn.pioneeruniverse.system.dao.mybatis.dataDic;

import java.util.List;

import cn.pioneeruniverse.common.dto.TblDataDicDTO;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblDataDic;

public interface DataDicDao extends BaseMapper<TblDataDic> {

    List<TblDataDic> getDataDicByTerm(String termCode);

    List<TblDataDicDTO> selectDataDicPage(TblDataDicDTO tblDataDicDTO);

    Integer addDataDict(TblDataDic tblDataDic);

    Integer updateDataDict(TblDataDic tblDataDic);

    Integer delDataDict(TblDataDic tblDataDic);

    Integer updateStatusByTermCode(TblDataDic tblDataDic);

}
