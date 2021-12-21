package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblRiskInfo;

public interface TblRiskInfoMapper {

	List<TblRiskInfo> getRiskInfo(Long projectId);

	void insertRiskInfo(TblRiskInfo tblRiskInfo);

	void deleteRiskInfo(TblRiskInfo riskInfo);

	TblRiskInfo getRiskInfoById(Long id);

	void updateRisk(TblRiskInfo tblRiskInfo);

	List<TblRiskInfo> getRiskInfoByProgram(Long programId);

}
