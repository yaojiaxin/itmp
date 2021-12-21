package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblDevTask;

public interface TblDevTaskMapper {

	List<TblDevTask> getDevTasks(Long id);

	List<Integer> getScmType(Long systemId);

}
