package cn.pioneeruniverse.project.service.risk;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.project.entity.TblRiskInfo;
import cn.pioneeruniverse.project.entity.TblRiskLog;

public interface RiskService {

	List<TblRiskInfo> getRiskInfo(Long projectId, HttpServletRequest request);

	void insertRiskInfo(TblRiskInfo tblRiskInfo, HttpServletRequest request);

	void deleteRiskInfo(Long id, HttpServletRequest request);

	TblRiskInfo getRiskInfoById(Long id);

	List<TblRiskLog> getRiskLog(Long id);

	void updateRisk(TblRiskInfo tblRiskInfo, HttpServletRequest request);

	List<TblRiskInfo> getRiskInfoByProgram(Long programId, HttpServletRequest request);

}
