package cn.pioneeruniverse.project.dao.mybatis;

import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocumentAttachment;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblSystemDirectoryDocumentAttachmentMapper extends BaseMapper<TblSystemDirectoryDocumentAttachment> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblSystemDirectoryDocumentAttachment record);

    TblSystemDirectoryDocumentAttachment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblSystemDirectoryDocumentAttachment record);

    int updateByPrimaryKey(TblSystemDirectoryDocumentAttachment record);
}