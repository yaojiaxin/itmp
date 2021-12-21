package cn.pioneeruniverse.dev.service.workTask.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskAttachementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskLogAttachementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskLogMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskRemarkAttachementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskRemarkMapper;
import cn.pioneeruniverse.dev.entity.TblRequirementFeatureRemarkAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskLog;
import cn.pioneeruniverse.dev.entity.TblTestTaskLogAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskRemark;
import cn.pioneeruniverse.dev.entity.TblTestTaskRemarkAttachement;
import cn.pioneeruniverse.dev.service.workTask.workTaskRemark;
@Service("workTaskRemarkImpl")
public class WorkTaskRemarkImpl implements workTaskRemark{
	@Autowired
    private TblTestTaskRemarkMapper tblTestTaskRemarkMapper;
	@Autowired
    private TblTestTaskRemarkAttachementMapper tblTestTaskRemarkAttachementMapper;
	@Autowired
	private TblTestTaskLogMapper tblTestTaskLogMapper;
	@Autowired
	private TblTestTaskLogAttachementMapper tblTestTaskLogAttachementMapper;
	@Autowired
	private TblTestTaskAttachementMapper tblTestTaskAttachementMapper;
	@Autowired
    private RedisUtils redisUtils;
	@Override
	public void addTaskRemark(String remark, Long userId, String userName, String userAccount,List<TblTestTaskRemarkAttachement> files) {
		TblTestTaskRemark tblTestTaskRemark=JSON.parseObject(remark, TblTestTaskRemark.class);
		tblTestTaskRemark.setCreateBy(userId);
		tblTestTaskRemark.setUserId(userId);
		tblTestTaskRemark.setUserName(userName);
		tblTestTaskRemark.setUserAccount(userAccount);
		tblTestTaskRemarkMapper.addTaskRemark(tblTestTaskRemark);
		Long id=tblTestTaskRemark.getId();
		
		if(null!=files&&files.size() > 0) {
		for(int i=0;i<files.size();i++) {
			files.get(i).setCreateBy(userId);
			files.get(i).setTestTaskRemarkId(id);
		}
		tblTestTaskRemarkAttachementMapper.addRemarkAttachement(files);
		}
		
	}

	@Override
	public List<TblTestTaskRemark> selectRemark(String testTaskId) {
		
		List<TblTestTaskRemark> list=tblTestTaskRemarkMapper.selectRemark(Long.parseLong(testTaskId));
		
		return list;
	}

	@Override
	public List<TblTestTaskRemarkAttachement> findTaskRemarkAttchement(List<Long> ids) {
		return tblTestTaskRemarkAttachementMapper.findTaskRemarkAttachement(ids);
	}

	@Override
	public List<TblTestTaskLog> findLogList(Long id) {
		List<TblTestTaskLog> list=tblTestTaskLogMapper.selectByPrimaryKey(id);
		return list;
	}

	@Override
	public List<TblTestTaskLogAttachement> findLogAttachement(List<Long> ids) {
		List<TblTestTaskLogAttachement> list=tblTestTaskLogAttachementMapper.findTaskLogAttachement(ids);
		return list;
	}

	@Override
	public List<TblTestTaskLogAttachement> findTestLogFile(Long testId) {
		
		return tblTestTaskLogAttachementMapper.findTestLogFile(testId);
	}

	@Override
	public List<TblTestTaskAttachement> findAttachement(Long testId) {
		List<TblTestTaskAttachement> list=tblTestTaskAttachementMapper.selectByPrimaryKey(testId);
		return list;
	}

	@Override
	public void updateNo(List<TblTestTaskAttachement> list, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("lastUpdateBy", CommonUtil.getCurrentUserId(request));
		tblTestTaskAttachementMapper.updateNo(map);
	}
	
}
