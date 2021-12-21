package cn.pioneeruniverse.project.service.newproject.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.project.dao.mybatis.DeptInfoMapper;
import cn.pioneeruniverse.project.dao.mybatis.NewProjectMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblProgramInfoMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblProjectSystemMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblSystemDirectoryMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblSystemDirectoryTemplateMapper;
import cn.pioneeruniverse.project.entity.TblProgramInfo;
import cn.pioneeruniverse.project.entity.TblProgramProject;
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.entity.TblProjectSystem;
import cn.pioneeruniverse.project.entity.TblSystemDirectory;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryTemplate;
import cn.pioneeruniverse.project.service.newproject.NewProjectService;
import cn.pioneeruniverse.project.service.oamproject.OamProjectService;

@Service
public class NewProjectServiceImpl implements NewProjectService {
	
	@Autowired
	private NewProjectMapper newProjectMapper;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private DeptInfoMapper deptInfoMapper;
	
	@Autowired
	private TblProjectSystemMapper tblProjectSystemMapper;
	
	@Autowired
	private TblProgramInfoMapper programInfoMapper;
	
	@Autowired
	private TblSystemDirectoryMapper tblSystemDirectoryMapper;
	
	@Autowired
	private TblSystemDirectoryTemplateMapper tblSystemDirectoryTemplateMapper;

	@Override
	@Transactional(readOnly=true)
	public List<TblProjectInfo> getAllNewProject(TblProjectInfo projectInfo, Long uid, List<String> roleCodes,
			Integer page, Integer rows) {
		Integer start = (page-1)*rows;
		Map<String,Object> map = new HashMap<>();
		map.put("projectInfo", projectInfo);
		map.put("uid", uid);
		map.put("start", start);
		map.put("rows", rows);
		List<TblProjectInfo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) { //如果当前登录用户角色是系统管理员
			list = newProjectMapper.getAllNewProject(map);
		}else {
			list = newProjectMapper.getNewProjectByUid(map);
		}
		for (TblProjectInfo tblProjectInfo : list) {
			Integer projectStatus = tblProjectInfo.getProjectStatus();
			String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
			JSONObject jsonObj = JSON.parseObject(redisStr);
			String statusName = jsonObj.get(projectStatus+"").toString();
			tblProjectInfo.setProjectStatusName(statusName);
			
			Integer projectClass = tblProjectInfo.getProjectClass();
			String redisStr2 = redisUtils.get("TBL_PROJECT_INFO_PROJECT_CLASS").toString();
			JSONObject jsonObj2 = JSON.parseObject(redisStr2);
			String className = jsonObj2.get(projectClass+"").toString();
			tblProjectInfo.setProjectClassName(className);
		}
		return list;
	}

	
	//数据回显
	@Override
	@Transactional(readOnly=true)
	public TblProjectInfo getNewProjectById(Long id) {
		TblProjectInfo projectInfo = newProjectMapper.getNewProjectById(id);
		Integer projectStatus = projectInfo.getProjectStatus();
		String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
		JSONObject jsonObj = JSON.parseObject(redisStr);
		String statusName = jsonObj.get(projectStatus).toString();
		projectInfo.setProjectStatusName(statusName);
		
		Integer projectClass = projectInfo.getProjectClass();
		String redisStr2 = redisUtils.get("TBL_PROJECT_INFO_PROJECT_CLASS").toString();
		JSONObject jsonObj2 = JSON.parseObject(redisStr2);
		String className = jsonObj2.get(projectClass).toString();
		projectInfo.setProjectClassName(className);
		
		List<Long> developSystemIds = tblProjectSystemMapper.getDevelopSystemIds(id); //获取项目的开发系统
		List<Long> peripheralSystemIds = tblProjectSystemMapper.getPeripheralSystemIds(id);  //获取项目的周边系统
		projectInfo.setDevelopSystemIds(StringUtils.join(developSystemIds,","));
		projectInfo.setPeripheralSystemIds(StringUtils.join(peripheralSystemIds,","));
		
		return projectInfo;
	}


	//新增新建项目
	@Override
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation = Propagation.REQUIRED)
	public void insertNewProject(TblProjectInfo projectInfo, HttpServletRequest request) {
		String projectCode = getProjectCode(projectInfo);
		projectInfo.setProjectCode(projectCode);
		//根据数据字典中部门键值获取部门id
		Long deptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getDeptNumber());
		projectInfo.setDeptId(deptId);
		Long businessDeptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getBusinessDeptNumber());
		projectInfo.setBusinessDeptId(businessDeptId);
		projectInfo.setStatus(1);
		projectInfo.setProjectType(2);
		projectInfo.setProjectStatus(1);
		projectInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
		projectInfo.setCreateDate(new Timestamp(new Date().getTime()));
		projectInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		projectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		newProjectMapper.insertNewProject(projectInfo);
		Long projectId = projectInfo.getId();
//		String[] developSystemIds = projectInfo.getDevelopSystemIds().split(","); //开发系统
		String sIds = projectInfo.getDevelopSystemIds();
		if(!sIds.equals("")) {
			String[] developSystemIds = sIds.split(",");
			for (String developSystemId : developSystemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectId);
				projectSystem.setSystemId(Long.valueOf(developSystemId));
				projectSystem.setRelationType(1);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
		
//		String[] peripheralSysetemIds = projectInfo.getPeripheralSystemIds().split(","); //周边系统
		String sIds2 = projectInfo.getPeripheralSystemIds();
		if(!sIds2.equals("")) {
			String[] peripheralSysetemIds = sIds2.split(",");
			for (String peripheralSysetemId : peripheralSysetemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectId);
				projectSystem.setSystemId(Long.valueOf(peripheralSysetemId));
				projectSystem.setRelationType(2);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
		
		SpringContextHolder.getBean(NewProjectService.class).insertTmpNewProject(projectInfo,request);
	}
	
	//同步新增新建项目
	@Override
	@DataSource(name = "tmpDataSource")
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
	public void insertTmpNewProject(TblProjectInfo projectInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String projectCode = getProjectCode(projectInfo);
		projectInfo.setProjectCode(projectCode);
		//根据数据字典中部门键值获取部门id
		Long deptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getDeptNumber());
		projectInfo.setDeptId(deptId);
		Long businessDeptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getBusinessDeptNumber());
		projectInfo.setBusinessDeptId(businessDeptId);
		projectInfo.setStatus(1);
		projectInfo.setProjectType(2);
		projectInfo.setProjectStatus(1);
		projectInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
		projectInfo.setCreateDate(new Timestamp(new Date().getTime()));
		projectInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		projectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		newProjectMapper.insertNewProject(projectInfo);
		Long projectId = projectInfo.getId();
//		String[] developSystemIds = projectInfo.getDevelopSystemIds().split(","); //开发系统
		String sIds = projectInfo.getDevelopSystemIds();
		if(!sIds.equals("")) {
			String[] developSystemIds = sIds.split(",");
			for (String developSystemId : developSystemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectId);
				projectSystem.setSystemId(Long.valueOf(developSystemId));
				projectSystem.setRelationType(1);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
		
//		String[] peripheralSysetemIds = projectInfo.getPeripheralSystemIds().split(","); //周边系统
		String sIds2 = projectInfo.getPeripheralSystemIds();
		if(!sIds2.equals("")) {
			String[] peripheralSysetemIds = sIds2.split(",");
			for (String peripheralSysetemId : peripheralSysetemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectId);
				projectSystem.setSystemId(Long.valueOf(peripheralSysetemId));
				projectSystem.setRelationType(2);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
	}


	//获取项目编号
	private String getProjectCode(TblProjectInfo projectInfo) {
		String projectCode = "";
		Integer projectClass = projectInfo.getProjectClass();
		switch (projectClass) {
		case 1:
			projectCode += "SD";
			break;
		case 2:
			projectCode += "HW";
			break;
		case 3:
			projectCode += "SM";
			break;
		case 4:
			projectCode += "SI";
			break;
		case 5:
			projectCode += "TC";
			break;
		}
//		String deptNum = deptInfoMapper.getDeptNumById(projectInfo.getDeptId());
		String deptNum = projectInfo.getDeptNumber();
		projectCode += deptNum.substring(deptNum.length()-2, deptNum.length());
		Calendar date = Calendar.getInstance();
		projectCode += Integer.valueOf(date.get(Calendar.YEAR)).toString();
		Long count = newProjectMapper.getCountNewProject();
		Long i = count + 1;
		if(i < 10) {
			projectCode += "0" + i.toString();
		}else {
			projectCode += i.toString();
		}
		
		return projectCode;
	}


	//新增项目群
	@Override
	@Transactional(readOnly=false)
	public void insertProgram(TblProgramInfo programInfo, HttpServletRequest request) {
		String programNumber = getProgramNumber();
		programInfo.setProgramNumber(programNumber);
		programInfo.setStatus(1);
		programInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
		programInfo.setCreateDate(new Timestamp(new Date().getTime()));
		programInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		programInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		programInfoMapper.insertProgram(programInfo);
		Long programId = programInfo.getId();
//		String[] projectIds = programInfo.getProjectIds().split(",");
		String pIds = programInfo.getProjectIds();
		if(!pIds.equals("")) {
			String[] projectIds = pIds.split(",");
			for (String projectId : projectIds) {
				TblProgramProject programProject = new TblProgramProject();
				programProject.setProgramId(programId);
				programProject.setProjectId(Long.valueOf(projectId));
				programProject.setStatus(1);
				programProject.setCreateBy(CommonUtil.getCurrentUserId(request));
				programProject.setCreateDate(new Timestamp(new Date().getTime()));
				programProject.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				programProject.setLastUpdateDate(new Timestamp(new Date().getTime()));
				programInfoMapper.insertProgramProject(programProject);
			}
		}
	}


	//获取项目群编号
	private String getProgramNumber() {
		String number = "G";
		Calendar date = Calendar.getInstance();
		number += Integer.valueOf(date.get(Calendar.YEAR)).toString();
		Long count = programInfoMapper.getCountProgram();
		Long i = count + 1;
		if(i < 10) {
			number += "0" + i.toString();
		}else {
			number += i.toString();
		}
		return number;
	}


	//新增项目群时关联新建项目弹窗
	@Override
	@Transactional(readOnly=true)
	public List<TblProjectInfo> getNewProjectByPage(Integer pageNumber, Integer pageSize) {
		HashMap<String, Object> map = new HashMap<>();
		Integer start = (pageNumber-1)*pageSize;
		map.put("start", start);
		map.put("pageSize", pageSize);
		List<TblProjectInfo> list = newProjectMapper.getNewProjectByPage(map);
		for (TblProjectInfo projectInfo : list) {
			String redisStr2 = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
			JSONObject jsonObj2 = JSON.parseObject(redisStr2);
			String statusName = jsonObj2.get(projectInfo.getProjectStatus()).toString();
			projectInfo.setProjectStatusName(statusName);
		}
		return list;
	}


	//编辑新建项目
	@Override
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation = Propagation.REQUIRED)
	public void updateNewProject(TblProjectInfo projectInfo, HttpServletRequest request) {
		//编辑新建项目
		//根据修改的部门和类型修改项目编码
//		String code = getUpdateProjectCode(projectInfo);
//		projectInfo.setProjectCode(code);
		
		Long deptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getDeptNumber());
		projectInfo.setDeptId(deptId);
		Long businessDeptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getBusinessDeptNumber());
		projectInfo.setBusinessDeptId(businessDeptId);
		projectInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		projectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		newProjectMapper.updateNewProject(projectInfo);
		//删除项目系统
		tblProjectSystemMapper.deleteProjectSystem(projectInfo.getId());
		//开发系统
		String sIds = projectInfo.getDevelopSystemIds();
		if(!sIds.equals("")) {
			String[] developSystemIds = sIds.split(",");
			for (String developSystemId : developSystemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectInfo.getId());
				projectSystem.setSystemId(Long.valueOf(developSystemId));
				projectSystem.setRelationType(1);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
		 //周边系统
		String sIds2 = projectInfo.getPeripheralSystemIds();
		if(!sIds2.equals("")) {
			String[] peripheralSysetemIds = sIds2.split(",");
			for (String peripheralSysetemId : peripheralSysetemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectInfo.getId());
				projectSystem.setSystemId(Long.valueOf(peripheralSysetemId));
				projectSystem.setRelationType(2);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
		SpringContextHolder.getBean(NewProjectService.class).updateTmpNewProject(projectInfo,request);
	}
	
	
	//同步编辑新建项目
	@Override
	@DataSource(name = "tmpDataSource")
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
	public void updateTmpNewProject(TblProjectInfo projectInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		Long deptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getDeptNumber());
		projectInfo.setDeptId(deptId);
		Long businessDeptId = deptInfoMapper.getDeptIdByNumber(projectInfo.getBusinessDeptNumber());
		projectInfo.setBusinessDeptId(businessDeptId);
		projectInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		projectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		newProjectMapper.updateNewProject(projectInfo);
		//删除项目系统
		tblProjectSystemMapper.deleteProjectSystem(projectInfo.getId());
		//开发系统
		String sIds = projectInfo.getDevelopSystemIds();
		if(!sIds.equals("")) {
			String[] developSystemIds = sIds.split(",");
			for (String developSystemId : developSystemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectInfo.getId());
				projectSystem.setSystemId(Long.valueOf(developSystemId));
				projectSystem.setRelationType(1);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
		 //周边系统
		String sIds2 = projectInfo.getPeripheralSystemIds();
		if(!sIds2.equals("")) {
			String[] peripheralSysetemIds = sIds2.split(",");
			for (String peripheralSysetemId : peripheralSysetemIds) {
				TblProjectSystem projectSystem = new TblProjectSystem();
				projectSystem.setProjectId(projectInfo.getId());
				projectSystem.setSystemId(Long.valueOf(peripheralSysetemId));
				projectSystem.setRelationType(2);
				projectSystem.setStatus(1);
				projectSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setCreateDate(new Timestamp(new Date().getTime()));
				projectSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				projectSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblProjectSystemMapper.insertProjectSystem(projectSystem);
			}
		}
	}



	//编辑项目时根据修改的部门和类型修改项目编码
//	private String getUpdateProjectCode(TblProjectInfo projectInfo) {
//		// TODO Auto-generated method stub
//		String code = "";
//		String projectCode = newProjectMapper.getProjectCodeById(projectInfo.getId());
//		String string = projectCode.substring(4);
//		Integer projectClass = projectInfo.getProjectClass();
//		switch (projectClass) {
//		case 1:
//			code += "SD";
//			break;
//		case 2:
//			code += "HW";
//			break;
//		case 3:
//			code += "SM";
//			break;
//		case 4:
//			code += "SI";
//			break;
//		case 5:
//			code += "TC";
//			break;
//		}
//		String deptNum = projectInfo.getDeptNumber();
//		code += deptNum.substring(deptNum.length()-2, deptNum.length());
//		code += string;
//		return code;
//	}
	
	
	
	//新增或编辑新建项目时根据开发系统更新系统目录
	@Override
	@Transactional(readOnly=false)
	public void updateSystemDirectory(String developSystemIds, HttpServletRequest request) {
		String[] systemIds = developSystemIds.split(",");
		for (String systemId : systemIds) {
			//查询系统目录模板总模板
			List<TblSystemDirectoryTemplate> systemDirTems = tblSystemDirectoryTemplateMapper.getAllSystemDirectoryTemplate();
			List<TblSystemDirectory> systemDirs = tblSystemDirectoryMapper.getSystemDirectoryBySystemId(Long.valueOf(systemId));
			//循环排除系统目录已有的模板
			for (TblSystemDirectory tblSystemDirectory : systemDirs) {
				for(int i=0;i<systemDirTems.size();i++) {
					if(tblSystemDirectory.getSystemDirectoryTemplateId() == systemDirTems.get(i).getId()) {
						systemDirTems.remove(i);
					}
				}
			}
			for (TblSystemDirectoryTemplate tblSystemDirectoryTemplate : systemDirTems) {
				TblSystemDirectory systemDirectory = new TblSystemDirectory();
				systemDirectory.setSystemId(Long.valueOf(systemId));
				systemDirectory.setProjectType(tblSystemDirectoryTemplate.getProjectType());
				systemDirectory.setSystemDirectoryTemplateId(tblSystemDirectoryTemplate.getId());
				systemDirectory.setDirName(tblSystemDirectoryTemplate.getDirName());
				systemDirectory.setOrderNumber(tblSystemDirectoryTemplate.getOrderNumber());
				systemDirectory.setTierNumber(tblSystemDirectoryTemplate.getTierNumber());
//				systemDirectory.setParentId(tblSystemDirectoryTemplate.getParentId());
//				systemDirectory.setParentIds(tblSystemDirectoryTemplate.getParentIds());
				systemDirectory.setCreateType(1);
				systemDirectory.setStatus(1);
				systemDirectory.setCreateBy(CommonUtil.getCurrentUserId(request));
				systemDirectory.setCreateDate(new Timestamp(new Date().getTime()));
				systemDirectory.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
				systemDirectory.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblSystemDirectoryMapper.insertSystemDir(systemDirectory);
			}
			for (TblSystemDirectoryTemplate tblSystemDirectoryTemplate : systemDirTems) {
				if(tblSystemDirectoryTemplate.getParentId()==0) {  //如果模板的parntId是0，那么该系统下的这个系统目录的parentId也是0
					Long parentId = Long.valueOf(0);
					Map<String,Object> map = new HashMap<>();
					map.put("systemId", systemId);
					map.put("templateId", tblSystemDirectoryTemplate.getId());
					map.put("parentId", parentId);
					tblSystemDirectoryMapper.updateSystemDIr(map);
				}else {
					//通过模板parentId和系统id找到目录的id
					Map<String,Object> map = new HashMap<>();
					map.put("systemId", systemId);
					map.put("templateId", tblSystemDirectoryTemplate.getParentId());
					Long id = tblSystemDirectoryMapper.getId(map);
					Map<String,Object> map2 = new HashMap<>();
					map2.put("systemId", systemId);
					map2.put("templateId", tblSystemDirectoryTemplate.getId());
					map2.put("parentId", id);
					tblSystemDirectoryMapper.updateSystemDIr(map2);
				}
				if(tblSystemDirectoryTemplate.getParentIds().equals("0")) {
					String parentIds = "0";
					Map<String,Object> map = new HashMap<>();
					map.put("systemId", systemId);
					map.put("templateId", tblSystemDirectoryTemplate.getId());
					map.put("parentIds", parentIds);
					tblSystemDirectoryMapper.updateSystemDIr2(map);
				}else {
					String parentIds = tblSystemDirectoryTemplate.getParentIds();
					String ids = ",";
					for (String parentId : parentIds.split(",")) {
						Map<String,Object> map = new HashMap<>();
						map.put("systemId", systemId);
						map.put("templateId", parentId);
						Long id = tblSystemDirectoryMapper.getId(map);
						if(id!=null) {
							ids += id+",";
						}
					}
					Map<String,Object> map2 = new HashMap<>();
					map2.put("systemId", systemId);
					map2.put("templateId", tblSystemDirectoryTemplate.getId());
					map2.put("parentIds", ids);
					tblSystemDirectoryMapper.updateSystemDIr2(map2);
				}
			}
		}
	}


	


	
	

}