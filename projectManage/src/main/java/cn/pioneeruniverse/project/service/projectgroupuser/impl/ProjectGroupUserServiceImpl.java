package cn.pioneeruniverse.project.service.projectgroupuser.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.pioneeruniverse.project.dao.mybatis.ProjectGroupUserMapper;
import cn.pioneeruniverse.project.service.projectgroupuser.ProjectGroupUserService;

@Service
public class ProjectGroupUserServiceImpl implements ProjectGroupUserService {

	@Autowired
	private ProjectGroupUserMapper projectGroupUserMapper;
	
	@Override
	public List<Long> findProjectGroupIdsByUserId(Long userId) {
		// TODO Auto-generated method stub
		return projectGroupUserMapper.findProjectGroupIdsByUserId(userId);
		
	}



}
