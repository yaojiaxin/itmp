package cn.pioneeruniverse.system.service.role.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import cn.pioneeruniverse.system.dao.mybatis.role.UserRoleDao;
import cn.pioneeruniverse.system.entity.TblUserRole;
import cn.pioneeruniverse.system.service.role.IUserRoleService;


@Service("iUserRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleDao, TblUserRole> implements IUserRoleService {

	@Autowired
	private UserRoleDao userRoleDao;
	
	
	@Transactional
	@Override
	public void insertUserRole(List<TblUserRole> list) {

		if(list != null && !list.isEmpty())
			userRoleDao.insertUserRole(list);
	}

	@Override
	public void delUserRole(List<Long> list) {

		if(list != null && !list.isEmpty())
			userRoleDao.delUserRole(list);
	}

	@Override
	public void delUserRoleByRoleId(List<Long> list,Long lastUpdateBy) {
		userRoleDao.delUserRoleByRoleId(list,lastUpdateBy);
	}

	@Override
	public void delUserRoleByUserIds(List<Long> list, Long lastUpdateBy) {
		userRoleDao.delUserRoleByUserIds(list, lastUpdateBy);
	}

	@Override
	public void delUserRoleByUserId(Long userId) {
		userRoleDao.delUserRoleByUserId(userId);
	}

}
