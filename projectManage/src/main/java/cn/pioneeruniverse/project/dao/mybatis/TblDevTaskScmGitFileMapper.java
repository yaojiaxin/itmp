package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblDevTaskScmGitFile;

public interface TblDevTaskScmGitFileMapper {

	List<TblDevTaskScmGitFile> getGitFile(Long id);

}
