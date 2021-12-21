package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;

import cn.pioneeruniverse.project.entity.TblUserInfo;

public interface UserMapper {

	Long findIdByUserName(String userName);

	List<String> findUserNameByIds(List<Long> userIds);

	/*同步时查询用户信息，无视状态 */
	Long findIdByUserAccount(String userAccount);

	TblUserInfo selectUserById(Long userId);

	List<TblUserInfo> selectUser(HashMap<String, Object> map);

	Long selectUserIdByUserName(String userName);

	String getUserNameById(Long id);
	
}
