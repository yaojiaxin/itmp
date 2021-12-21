package cn.pioneeruniverse.project.dao.mybatis.projectPlan;

import cn.pioneeruniverse.project.entity.TblProjectPlanApproveRequestLog;

import java.util.List;

public interface ProjectPlanApproveRequestLogMapper {


    void insertPlanApproveRequestLog(TblProjectPlanApproveRequestLog approveRequestLog);

    List<TblProjectPlanApproveRequestLog> getApproveRequestLog(Long approveRequestId);

    void deleteApproveRequestLogByRequestId(Long approveRequestId);
}
