package cn.pioneeruniverse.system.service.role.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import cn.pioneeruniverse.system.dao.mybatis.role.MenuRoleDao;
import cn.pioneeruniverse.system.entity.TblRoleMenuButton;
import cn.pioneeruniverse.system.service.role.IMenuRoleService;


@Service("iMenuRoleService")
public class MenuRoleServiceImpl extends ServiceImpl<MenuRoleDao, TblRoleMenuButton> implements IMenuRoleService{

	@Autowired
	private MenuRoleDao menuRoleDao;
	
	
	@Transactional
	@Override
	public void insertMenuRole(List<TblRoleMenuButton> list) {
		if(list != null && !list.isEmpty())
			menuRoleDao.insertMenuRole(list);
	}

	@Override
	public void delMenuRole(List<Long> list) {
		if(list != null && !list.isEmpty())
			menuRoleDao.delMenuRole(list);
	}

	@Override
	public void delMenuByRoleId(Long roleId) {
		menuRoleDao.delMenuByRoleId(roleId);
	}

	@Override
	public void delMenuByRoleIds(List<Long> list,Long lastUpdateBy) {
		menuRoleDao.delMenuByRoleIds(list,lastUpdateBy);
	}
	
	
	

}
