package cn.pioneeruniverse.project.service.risk.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.dao.mybatis.TblRiskInfoMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblRiskLogMapper;
import cn.pioneeruniverse.project.dao.mybatis.UserMapper;
import cn.pioneeruniverse.project.entity.TblRiskInfo;
import cn.pioneeruniverse.project.entity.TblRiskLog;
import cn.pioneeruniverse.project.service.risk.RiskService;

@Service
public class RiskServiceImpl implements RiskService {
	
	@Autowired
	private TblRiskInfoMapper tblRiskInfoMapper;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private TblRiskLogMapper tblRiskLogMapper;
	
	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional(readOnly=true)
	public List<TblRiskInfo> getRiskInfo(Long projectId, HttpServletRequest request) {
		List<TblRiskInfo> list = tblRiskInfoMapper.getRiskInfo(projectId);
		Long number = Long.valueOf(1);
		for (TblRiskInfo tblRiskInfo : list) {
			String riskPriorityName = getValue(tblRiskInfo.getRiskPriority(),"TBL_RISK_INFO_RISK_PRIORITY");  //优先级
			String statusName = getValue(tblRiskInfo.getRiskStatus(),"TBL_RISK_INFO_RISK_STATUS");   //状态
			tblRiskInfo.setRiskPriorityName(riskPriorityName);
			tblRiskInfo.setStatusName(statusName);
			tblRiskInfo.setNumber(number);
			number ++;
		}
		return list;
	}

	//数据字典
	private String getValue(Integer key, String string) {
		String redisStr = redisUtils.get(string).toString();
		JSONObject jsonObj = JSON.parseObject(redisStr);
		String name = jsonObj.get(key).toString();
		return name;
	}

	//新增风险
	@Override
	@Transactional(readOnly=false)
	public void insertRiskInfo(TblRiskInfo tblRiskInfo, HttpServletRequest request) {
		tblRiskInfo.setStatus(1);
		tblRiskInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
		tblRiskInfo.setCreateDate(new Timestamp(new Date().getTime()));
		tblRiskInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		tblRiskInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblRiskInfoMapper.insertRiskInfo(tblRiskInfo);
		//新增风险日志
		TblRiskLog log = new TblRiskLog();
		log.setRiskId(tblRiskInfo.getId());
		log.setLogType("新增风险");
		log.setUserId(CommonUtil.getCurrentUserId(request));
		log.setUserName(CommonUtil.getCurrentUserName(request));
		log.setUserAccount(CommonUtil.getCurrentUserAccount(request));
		log.setStatus(1);
		log.setCreateBy(CommonUtil.getCurrentUserId(request));
		log.setCreateDate(new Timestamp(new Date().getTime()));
		log.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		log.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblRiskLogMapper.insertLog(log);
	}

	//删除风险
	@Override
	@Transactional(readOnly=false)
	public void deleteRiskInfo(Long id, HttpServletRequest request) {
		TblRiskInfo riskInfo = new TblRiskInfo();
		riskInfo.setId(id);
		riskInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		riskInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblRiskInfoMapper.deleteRiskInfo(riskInfo);
	}

	//获取详情
	@Override
	@Transactional(readOnly=true)
	public TblRiskInfo getRiskInfoById(Long id) {
		TblRiskInfo tblRiskInfo = tblRiskInfoMapper.getRiskInfoById(id);
		tblRiskInfo.setRiskPriorityName(getValue(tblRiskInfo.getRiskPriority(),"TBL_RISK_INFO_RISK_PRIORITY"));
		tblRiskInfo.setStatusName(getValue(tblRiskInfo.getRiskStatus(),"TBL_RISK_INFO_RISK_STATUS"));
		tblRiskInfo.setRiskTypeName(getValue(tblRiskInfo.getRiskType(),"TBL_RISK_INFO_RISK_TYPE"));
		tblRiskInfo.setRiskFactorName(getValue(tblRiskInfo.getRiskFactor(),"TBL_RISK_INFO_RISK_FACTOR"));
		tblRiskInfo.setRiskProbabilityName(getValue(tblRiskInfo.getRiskProbability(),"TBL_RISK_INFO_RISK_PROBABILITY"));
		return tblRiskInfo;
	}

	//获取风险日志
	@Override
	@Transactional(readOnly=true)
	public List<TblRiskLog> getRiskLog(Long id) {
		return tblRiskLogMapper.getRiskLog(id);
		
	}

	//编辑风险
	@Override
	@Transactional(readOnly=false)
	public void updateRisk(TblRiskInfo tblRiskInfo, HttpServletRequest request) {
		//获取修改之前的旧数据
		TblRiskInfo oldTblRiskInfo = tblRiskInfoMapper.getRiskInfoById(tblRiskInfo.getId());
		tblRiskInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		tblRiskInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblRiskInfoMapper.updateRisk(tblRiskInfo);
		//新增日志
		TblRiskLog log = new TblRiskLog();
		log.setRiskId(tblRiskInfo.getId());
		log.setLogType("修改风险");
		log.setUserId(CommonUtil.getCurrentUserId(request));
		log.setUserName(CommonUtil.getCurrentUserName(request));
		log.setUserAccount(CommonUtil.getCurrentUserAccount(request));
		log.setStatus(1);
		log.setCreateBy(CommonUtil.getCurrentUserId(request));
		log.setCreateDate(new Timestamp(new Date().getTime()));
		log.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		log.setLastUpdateDate(new Timestamp(new Date().getTime()));
		String logDetail = "";
		if(tblRiskInfo.getResponsibleUserId()!=oldTblRiskInfo.getResponsibleUserId()) {
			String beforeName = userMapper.getUserNameById(oldTblRiskInfo.getResponsibleUserId());
			String afterName = userMapper.getUserNameById(tblRiskInfo.getResponsibleUserId());
			logDetail += "责任人：&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(!tblRiskInfo.getRiskDescription().equals(oldTblRiskInfo.getRiskDescription())) {
			logDetail += "风险描述：&nbsp;&nbsp;“&nbsp;<b>"+oldTblRiskInfo.getRiskDescription()+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+tblRiskInfo.getRiskDescription()+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(tblRiskInfo.getRiskType()!=oldTblRiskInfo.getRiskType()) {
			String beforeName = getValue(oldTblRiskInfo.getRiskType(),"TBL_RISK_INFO_RISK_TYPE");
			String afterName = getValue(tblRiskInfo.getRiskType(),"TBL_RISK_INFO_RISK_TYPE");
			logDetail += "风险分类：&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(tblRiskInfo.getRiskFactor()!=oldTblRiskInfo.getRiskFactor()) {
			String beforeName = getValue(oldTblRiskInfo.getRiskFactor(),"TBL_RISK_INFO_RISK_FACTOR");
			String afterName = getValue(tblRiskInfo.getRiskFactor(),"TBL_RISK_INFO_RISK_FACTOR");
			logDetail += "危险度：&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(tblRiskInfo.getRiskProbability()!=oldTblRiskInfo.getRiskProbability()) {
			String beforeName = getValue(oldTblRiskInfo.getRiskProbability(),"TBL_RISK_INFO_RISK_PROBABILITY");
			String afterName = getValue(tblRiskInfo.getRiskProbability(),"TBL_RISK_INFO_RISK_PROBABILITY");
			logDetail += "发生概率：&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(tblRiskInfo.getRiskPriority()!=oldTblRiskInfo.getRiskPriority()) {
			String beforeName = getValue(oldTblRiskInfo.getRiskPriority(),"TBL_RISK_INFO_RISK_PRIORITY");
			String afterName = getValue(tblRiskInfo.getRiskPriority(),"TBL_RISK_INFO_RISK_PRIORITY");
			logDetail += "风险优先级：&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(!tblRiskInfo.getRiskInfluence().equals(oldTblRiskInfo.getRiskInfluence())) {
			logDetail += "风险影响：&nbsp;&nbsp;“&nbsp;<b>"+oldTblRiskInfo.getRiskInfluence()+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+tblRiskInfo.getRiskInfluence()+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(!tblRiskInfo.getRiskTriggers().equals(oldTblRiskInfo.getRiskTriggers())) {
			logDetail += "触发条件：&nbsp;&nbsp;“&nbsp;<b>"+oldTblRiskInfo.getRiskTriggers()+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+tblRiskInfo.getRiskTriggers()+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(tblRiskInfo.getRiskStatus()!=oldTblRiskInfo.getRiskStatus()) {
			String beforeName = getValue(oldTblRiskInfo.getRiskStatus(),"TBL_RISK_INFO_RISK_STATUS");
			String afterName = getValue(tblRiskInfo.getRiskStatus(),"TBL_RISK_INFO_RISK_STATUS");
			logDetail += "风险状态：&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(!tblRiskInfo.getRemark().equals(oldTblRiskInfo.getRemark())) {
			logDetail += "备注：&nbsp;&nbsp;“&nbsp;<b>"+oldTblRiskInfo.getRemark()+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+tblRiskInfo.getRemark()+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(!tblRiskInfo.getCopingStrategy().equals(oldTblRiskInfo.getCopingStrategy())) {
			logDetail += "缓解措施：&nbsp;&nbsp;“&nbsp;<b>"+oldTblRiskInfo.getCopingStrategy()+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+tblRiskInfo.getCopingStrategy()+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(!tblRiskInfo.getCopingStrategyRecord().equals(oldTblRiskInfo.getCopingStrategyRecord())) {
			logDetail += "缓解措施执行记录：&nbsp;&nbsp;“&nbsp;<b>"+oldTblRiskInfo.getCopingStrategyRecord()+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+tblRiskInfo.getCopingStrategyRecord()+"</b>&nbsp;”&nbsp;&nbsp;"+"；<br/>";
		}
		if(logDetail.equals("")) {
			logDetail +="未作任何修改";
		}
		log.setLogDetail(logDetail);
		tblRiskLogMapper.insertLog(log);
	}

	
	//项目群管理风险列表
	@Override
	@Transactional(readOnly=true)
	public List<TblRiskInfo> getRiskInfoByProgram(Long programId, HttpServletRequest request) {
		List<TblRiskInfo> list = tblRiskInfoMapper.getRiskInfoByProgram(programId);
		Long number = Long.valueOf(1);
		for (TblRiskInfo tblRiskInfo : list) {
			String riskPriorityName = getValue(tblRiskInfo.getRiskPriority(),"TBL_RISK_INFO_RISK_PRIORITY");  //优先级
			String statusName = getValue(tblRiskInfo.getRiskStatus(),"TBL_RISK_INFO_RISK_STATUS");   //状态
			tblRiskInfo.setRiskPriorityName(riskPriorityName);
			tblRiskInfo.setStatusName(statusName);
			tblRiskInfo.setNumber(number);
			number ++;
		}
		return list;
	}

	
	
	

}
