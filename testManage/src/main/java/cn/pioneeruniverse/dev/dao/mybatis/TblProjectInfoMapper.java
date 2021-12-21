package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblProjectInfo;

public interface TblProjectInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TblProjectInfo record);

    int insertSelective(TblProjectInfo record);

    TblProjectInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblProjectInfo record);

    int updateByPrimaryKeyWithBLOBs(TblProjectInfo record);

    int updateByPrimaryKey(TblProjectInfo record);
}