package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.pioneeruniverse.dev.entity.TblUserInfo;

public interface TblUserInfoMapper {

	String getUserNameById(Long createById);
	List<Map<String, Object>> getAllTestUser(Map<String, Object> map);
	List<Long> getUserIdByUserName(String string);
	List<Map<String, Object>> getUserTable(@Param("testSetId") Long testSetId,@Param("executeRound") Integer executeRound,@Param("userName") String userName,@Param("companyName") String companyName,@Param("deptName") String deptName);
	TblUserInfo getUserById(Long userId);

	/*同步时查询用户信息，无视状态 */
	Long findIdByUserAccount(String userAccount);
}
