package cn.pioneeruniverse.project.service.program.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.dao.mybatis.TblProgramInfoMapper;
import cn.pioneeruniverse.project.entity.TblProgramInfo;
import cn.pioneeruniverse.project.entity.TblProgramProject;
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.service.program.ProgramService;

@Service
public class ProgramServiceImpl implements ProgramService {
	
	@Autowired
	private TblProgramInfoMapper tblProgramMapper;
	
	@Autowired
	private RedisUtils redisUtils;

	//项目群列表查询
	@Override
	@Transactional(readOnly=true)
	public List<TblProgramInfo> getAllPrograms(TblProgramInfo programInfo, Long uid, List<String> roleCodes,
			Integer page, Integer rows) {
		Integer start = (page-1)*rows;
		Map<String,Object> map = new HashMap<>();
		map.put("programInfo", programInfo);
		map.put("uid", uid);
		map.put("start", start);
		map.put("rows", rows);
		List<TblProgramInfo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) { //如果当前登录用户角色是系统管理员
			list = tblProgramMapper.getAllPrograms(map);
		}else {
			list = tblProgramMapper.getProgramsByUid(map);
		}
		return list;
	}

	//数据回显
	@Override
	@Transactional(readOnly=true)
	public TblProgramInfo getProgramById(Long id) {
		// TODO Auto-generated method stub
		TblProgramInfo programInfo = tblProgramMapper.getProgramById(id);
		List<TblProjectInfo> list = tblProgramMapper.getProjectsByProgramId(id);
		for (TblProjectInfo tblProjectInfo : list) {
			Integer projectStatus = tblProjectInfo.getProjectStatus();
			String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
			JSONObject jsonObj = JSON.parseObject(redisStr);
			String statusName = jsonObj.get(projectStatus).toString();
			tblProjectInfo.setProjectStatusName(statusName);
		}
		programInfo.setProjectList(list);
		return programInfo;
	}

	//编辑项目群
	@Override
	@Transactional(readOnly=false)
	public void updateProgram(TblProgramInfo tblProgramInfo, HttpServletRequest request) {
		tblProgramInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		tblProgramInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblProgramMapper.updateProgram(tblProgramInfo);
		tblProgramMapper.updateProgramProject(tblProgramInfo.getId());
		String pIds = tblProgramInfo.getProjectIds();
		if(!pIds.equals("")) {
			String[] projectIds = pIds.split(",");
			for (String projectId : projectIds) {
				TblProgramProject programProject = new TblProgramProject();
				programProject.setProgramId(tblProgramInfo.getId());
				programProject.setProjectId(Long.valueOf(projectId));
				programProject.setStatus(1);
				programProject.setCreateBy(CommonUtil.getCurrentUserId(request));
				programProject.setCreateDate(new Timestamp(new Date().getTime()));
				programProject.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				programProject.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProgramMapper.insertProgramProject(programProject);
			}
		}
	}

}
