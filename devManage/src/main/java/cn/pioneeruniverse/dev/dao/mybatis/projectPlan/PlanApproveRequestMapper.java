package cn.pioneeruniverse.dev.dao.mybatis.projectPlan;

import cn.pioneeruniverse.dev.entity.projectPlan.PlanApproveRequest;

import java.util.List;

public interface PlanApproveRequestMapper {

    List<PlanApproveRequest> getApproveByUserId(Long userId);

    List<PlanApproveRequest> getRequestUserIdByUserId(Long userId);
}


