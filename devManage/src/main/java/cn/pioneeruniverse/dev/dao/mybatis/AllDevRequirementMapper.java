	
package cn.pioneeruniverse.dev.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.DevDetailVo;

public interface AllDevRequirementMapper extends BaseMapper<DevDetailVo> {
 	DevDetailVo AlldevReuirement(Long devID);
 	String getdevName(Long devuserID);
 	String getSystemName(Long system);
 	String getdeptName(Long deptID);
}
