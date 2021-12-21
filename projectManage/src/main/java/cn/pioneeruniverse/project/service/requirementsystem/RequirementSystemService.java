package cn.pioneeruniverse.project.service.requirementsystem;

import cn.pioneeruniverse.project.entity.TblRequirementSystem;

public interface RequirementSystemService {

	void insertReqSystem(TblRequirementSystem requirementSystem);

	void updateReqSystem(TblRequirementSystem requirementSystem);

	int updateReqSystemData(String reqSystem, String reqId);

	int getFunctionCountByReqId(Long requirementId);
}
