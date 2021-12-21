package cn.pioneeruniverse.project.service.projectgroupuser;

import java.util.List;

public interface ProjectGroupUserService {

	List<Long> findProjectGroupIdsByUserId(Long userId);



}
