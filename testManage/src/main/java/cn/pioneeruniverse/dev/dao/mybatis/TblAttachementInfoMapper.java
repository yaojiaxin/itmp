package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblAttachementInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblAttachementInfoMapper extends BaseMapper<TblAttachementInfo> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblAttachementInfo record);

    TblAttachementInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblAttachementInfo record);

    int updateByPrimaryKey(TblAttachementInfo record);
}