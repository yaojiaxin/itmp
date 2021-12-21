package cn.pioneeruniverse.system.dao.mybatis.defaultPage;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblUserDefaultPage;

public interface UserDefaultPageDao extends BaseMapper<TblUserDefaultPage> {

	void updateDefaultPage(Long userId);

	void saveDefaultPage(TblUserDefaultPage tblUserDefaultPage);

	List<Long> getDefaultPage(Long currentUserId);

	List<TblUserDefaultPage> getDefaultPage2(Long userId);

}
