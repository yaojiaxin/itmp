package cn.pioneeruniverse.project.service.user.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.pioneeruniverse.project.dao.mybatis.UserMapper;
import cn.pioneeruniverse.project.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	


	@Override
	public Long findIdByUserAccount(String userAccount) {
		return userMapper.findIdByUserAccount(userAccount);
	}



	@Override
	public Long findIdByUserName(String userName) {
		return userMapper.findIdByUserName(userName);
	}

}
