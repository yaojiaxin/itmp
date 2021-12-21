package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblDevTaskScmFile;

public interface TblDevTaskScmFileMapper {

	List<TblDevTaskScmFile> getFile(Long id);

}
