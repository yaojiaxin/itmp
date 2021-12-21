package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.entity.TblRequirementSystem;

public interface RequirmentSystemMapper {

	void insertReqSystem(TblRequirementSystem requirementSystem);

	void updateReqSystem(TblRequirementSystem requirementSystem);

	TblRequirementSystem selectReqSystemByReqSystem(TblRequirementSystem requirementSystem);

	void deleteReqSystem(Long requirementId);

	int getFunctionCountByReqId(Long requirementId);

	List<Long> getSystemByrequirementId(Long rId);

	List<String> getSystemNameByIds(List<Long> sysIds);

	Long getAssetSystemIdByRequirementId(Long rId);

	String getAssetSystemNameByAssetSystemId(Long assetSystemId);

	void updateReqAssSystemTree(TblRequirementInfo requirementInfo);

	List<TblRequirementSystem> getReqSystemByReqId(Long id);

	String getSystemNameBySystemId(Long systemId);

	String getSystemCodeBySystemId(Long systemId);

	List<String> getsystems(Long id);
}
