package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import cn.pioneeruniverse.dev.entity.TblUserInfo;

import java.util.List;
import java.util.Map;

public interface TblUserInfoMapper {

	TblUserInfo getUserById(Long userId);

	TblDeptInfoDTO findDeptById(Long deptId);

	List<TblUserInfo> getUserByAccount(String userAccount);

	/*同步时查询用户信息，无视状态 */
	Long findIdByUserAccount(String userAccount);
	List<String> findRoleByUserIds(Map<String,Object> map);
}
