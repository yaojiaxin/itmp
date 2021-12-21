package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblRequirementInfo;

public interface TblRequirementInfoMapper extends BaseMapper<TblRequirementInfo> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblRequirementInfo record);

    TblRequirementInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblRequirementInfo record);

    int updateByPrimaryKey(TblRequirementInfo record);

	List<TblRequirementInfo> getAllRequirement();
	
	List<TblRequirementInfo> findRequirementByUser(Long userId);
	
	List<TblRequirementInfo> getRequirement(TblRequirementInfo record);

	String getReqName(Long requirementId);
	
	TblRequirementInfo findRequirementById(Long reqId);
	
	TblRequirementInfo findRequirementByCode(String reqCode);
	
	List<TblRequirementInfo> findRequirementByFeatureIds(Long []featureIds);

	String getProManageUserIds(Long requirementId);

}