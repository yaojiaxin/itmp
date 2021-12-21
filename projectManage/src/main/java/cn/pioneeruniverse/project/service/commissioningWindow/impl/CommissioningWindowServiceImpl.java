package cn.pioneeruniverse.project.service.commissioningWindow.impl;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.project.service.oamproject.OamProjectService;
import sun.misc.BASE64Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.dao.mybatis.CommissioningWindowMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementFeatureMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementMapper;
import cn.pioneeruniverse.project.dao.mybatis.SystemInfoMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblDevTaskMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblDevTaskScmFileMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblDevTaskScmGitFileMapper;
import cn.pioneeruniverse.project.entity.TblCommissioningWindow;
import cn.pioneeruniverse.project.entity.TblDevTask;
import cn.pioneeruniverse.project.entity.TblDevTaskScmFile;
import cn.pioneeruniverse.project.entity.TblDevTaskScmGitFile;
import cn.pioneeruniverse.project.entity.TblRequirementFeature;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.feignInterface.ProjectToSystemInterface;
import cn.pioneeruniverse.project.service.commissioningWindow.CommissioningWindowService;

@Service
public class CommissioningWindowServiceImpl implements CommissioningWindowService {
	
	@Autowired
	private CommissioningWindowMapper commissioningWindowMapper;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private RequirementMapper requirementMapper;

	@Autowired
	private RequirementFeatureMapper requirementFeatureMapper;
	
	@Autowired
	private SystemInfoMapper systemInfoMapper;
	@Autowired
	private ProjectToSystemInterface projectToSystemInterface;
	
	@Autowired
	private TblDevTaskMapper tblDevTaskMapper;
	
	@Autowired
	private TblDevTaskScmFileMapper tblDevTaskScmFileMapper;
	
	@Autowired
	private TblDevTaskScmGitFileMapper tblDevTaskScmGitFileMapper;
	
	/**
	 * 投产窗口的条件查询和分页
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TblCommissioningWindow> selectCommissioningWindows(TblCommissioningWindow tblCommissioningWindow, String year,
			Integer page, Integer rows) throws Exception {
		// TODO Auto-generated method stub
		String typeName = tblCommissioningWindow.getTypeName();
		String redisStr = redisUtils.get("TBL_COMMISSIONING_WINDOW_WINDOW_TYPE").toString();
		JSONObject jsonObj = JSON.parseObject(redisStr);
		for(String key : jsonObj.keySet()) {
			String value = jsonObj.get(key).toString();
			if(value.equals(typeName)) {
				tblCommissioningWindow.setWindowType(Integer.valueOf(key));
			}
		}
		String startTime = null;
		String endTime = null;
		if(StringUtil.isNotEmpty(year)) {
			startTime = year + "-01-01";
			endTime = year + "-12-31";
		}
		Integer start = (page-1)*rows;
		Integer end = rows;
		HashMap<Object,Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("tblCommissioningWindow", tblCommissioningWindow);
		List<TblCommissioningWindow> list = commissioningWindowMapper.selectCommissioningWindows(map);
		for (TblCommissioningWindow tblCommissioningWindow2 : list) {
			String type = tblCommissioningWindow2.getWindowType().toString();
			String str = redisUtils.get("TBL_COMMISSIONING_WINDOW_WINDOW_TYPE").toString();
			JSONObject jsonObject = JSON.parseObject(str);
			String tname = jsonObject.get(type).toString();
			tblCommissioningWindow2.setTypeName(tname);
			}
		return list;
	}

	//窗口类型的解析
	@Override
	@Transactional
	public List<String> selectWindowType() throws Exception {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<>();
		String redisStr = redisUtils.get("TBL_COMMISSIONING_WINDOW_WINDOW_TYPE").toString();
			JSONObject jsonObject = JSON.parseObject(redisStr);
			for (String key : jsonObject.keySet()) {
				String value = jsonObject.get(key).toString();
				list.add(value);
			}
		return list;
	}

	//新增窗口至itmp_db
	@Override
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation = Propagation.REQUIRED)
	public void insertCommissioningWindowItmp(TblCommissioningWindow commissioningWindow, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String typeName = commissioningWindow.getTypeName();
		String redisStr = redisUtils.get("TBL_COMMISSIONING_WINDOW_WINDOW_TYPE").toString();
		JSONObject jsonObject = JSON.parseObject(redisStr);
		for(String key : jsonObject.keySet()) {
			String value = jsonObject.get(key).toString();
			if(value.equals(typeName)) {
				commissioningWindow.setWindowType(Integer.valueOf(key));
			}
		}
//		//设置版本号
//		//获取当前最大版本号,然后加1
//		String maxVersion = commissioningWindowMapper.selectMaxVersion();
//		Integer version = Integer.valueOf(maxVersion)+1;
//		commissioningWindow.setWindowVersion(String.valueOf(version));
		//设置状态 1=正常；2=删除
		commissioningWindow.setStatus(1);
		//创建者
		commissioningWindow.setCreateBy(CommonUtil.getCurrentUserId(request));
		commissioningWindow.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		//设置创建时间
		commissioningWindow.setCreateDate(new Timestamp(new Date().getTime()));
		commissioningWindow.setLastUpdateDate(new Timestamp(new Date().getTime()));		
		commissioningWindowMapper.insertCommissioningWindow(commissioningWindow);

		SpringContextHolder.getBean(CommissioningWindowService.class).insertCommissioningWindowTmp(commissioningWindow);
	}

	//新增窗口至tmp_db
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
	public void insertCommissioningWindowTmp(TblCommissioningWindow commissioningWindow) throws Exception {		
		commissioningWindowMapper.insert(commissioningWindow);
	}
	
	//删除itmp_db窗口
	@Override
	@Transactional(readOnly=false)
	public HashMap<String, Object> delectCommissioningWindowItmp(Long id,Long status, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		Long currentUserId = CommonUtil.getCurrentUserId(request);
		Timestamp timestamp = new Timestamp(new Date().getTime());
		HashMap<String, Object> map = new HashMap<>();
		map.put("currentUserId", currentUserId);
		map.put("lastUpdateTime", timestamp);
		map.put("id", id);
		map.put("status", status);
		commissioningWindowMapper.delectCommissioningWindow(map);
		return map;
	}

	//删除tmp_db窗口
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly=false)
	public void delectCommissioningWindowTmp(HashMap<String, Object> map) throws Exception {		
		commissioningWindowMapper.delectCommissioningWindow(map);
	}
	
	//编辑窗口数据回显
	@Override
	@Transactional(readOnly=true)
	public TblCommissioningWindow selectCommissioningWindowById(Long id) throws Exception {
		// TODO Auto-generated method stub
//		Long id2 = Long.parseLong(id);
//		System.err.println(id2);
		TblCommissioningWindow tblCommissioningWindow = commissioningWindowMapper.selectCommissioningWindowById(id);
		//解析窗口类型
		String type = tblCommissioningWindow.getWindowType().toString();
		String redisStr = redisUtils.get("TBL_COMMISSIONING_WINDOW_WINDOW_TYPE").toString();
		JSONObject jsonObject = JSON.parseObject(redisStr);
		String typeName = jsonObject.get(type).toString();
		tblCommissioningWindow.setTypeName(typeName);
		
		return tblCommissioningWindow;
	}

	//编辑投产窗口至itmp_db
	@Override
	@Transactional(readOnly=false)
	public TblCommissioningWindow editCommissioningWindowItmp(TblCommissioningWindow commissioningWindow, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		//转换类型
		String typeName = commissioningWindow.getTypeName();
		String redisStr = redisUtils.get("TBL_COMMISSIONING_WINDOW_WINDOW_TYPE").toString();
		JSONObject jsonObject = JSON.parseObject(redisStr);
		for (String key : jsonObject.keySet()) {
			String value = jsonObject.get(key).toString();
			if(value.equals(typeName)) {
				commissioningWindow.setWindowType(Integer.valueOf(key));
			}
		}
		//得到本次修改者
		commissioningWindow.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		//本次修改时间
		commissioningWindow.setLastUpdateDate(new Timestamp(new Date().getTime()));
		commissioningWindowMapper.editCommissioningWindow(commissioningWindow);
		return commissioningWindow;
	}
	
	//编辑投产窗口至tmp_db
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly=false)
	public void editCommissioningWindowTmp(TblCommissioningWindow commissioningWindow) throws Exception {	
		commissioningWindowMapper.editCommissioningWindow(commissioningWindow);
	}

	//搜索栏任务状态的下拉列表
	@Override
	@Transactional
	public List<String> selectFeatureType() throws Exception {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<>();
		String redisStr = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS").toString();
		JSONObject jsonObject = JSON.parseObject(redisStr);
		for (String key : jsonObject.keySet()) {
			String value = jsonObject.get(key).toString();
			if(!value.equals("取消")) {
				list.add(value);
			}
		}
		return list;
	}

	//关联开发任务的分页搜索
	@Override
	@Transactional(readOnly=true)
	public List<TblRequirementFeature> selectRequirement(TblRequirementFeature tblRequirementFeature,
			Integer pageNumber, Integer pageSize) throws Exception {
		// TODO Auto-generated method stub
		//根据需求名称搜索 实际上就是根据需求id查询
		String requirementName = tblRequirementFeature.getRequirementName();
		if(!StringUtils.isEmpty(requirementName)) {
			List<Long> ids = requirementMapper.selectIdsByName(requirementName);
			tblRequirementFeature.setRequirementIds(ids);
		}
		//解析开发任务状态
		String valueName = tblRequirementFeature.getValueName();
		if(!StringUtils.isEmpty(valueName)) {
			String redisStr = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS").toString();
			JSONObject jsonObject = JSON.parseObject(redisStr);
			for (String key : jsonObject.keySet()) {
				String value = jsonObject.get(key).toString();
				if(value.equals(valueName)) {
					tblRequirementFeature.setRequirementFeatureStatus(key);
				}
			}
		}
		Integer start = (pageNumber-1)*pageSize;
		HashMap<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("pageSize", pageSize);
		map.put("tblRequirementFeature", tblRequirementFeature);
		List<TblRequirementFeature> list = requirementFeatureMapper.selectFeatures(map);
		for (TblRequirementFeature requirementFeature : list) {
			//获得与该开发任务相关的系统
			Long systemId = requirementFeature.getSystemId();
			String systemName = null;
			if(systemId != null) {
				systemName = systemInfoMapper.selectSystemNameById(systemId);
			}
			
			//获取需求编号及名称
			Long requirementId = requirementFeature.getRequirementId();
			String requirementName2 =null;
			String requirementCode = null;
			if(requirementId != null) {
				TblRequirementInfo tblRequirementInfo = requirementMapper.selectRequirementById(requirementId);
				if(tblRequirementInfo != null) {
					requirementName2 = tblRequirementInfo.getRequirementName();
					requirementCode = tblRequirementInfo.getRequirementCode();
				}
			}
			//解析开发任务状态
			String status = requirementFeature.getRequirementFeatureStatus();
			if(StringUtils.isNotEmpty(status)) {
				String redisStr = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS").toString();
				JSONObject jsonObject = JSON.parseObject(redisStr);
				String value = jsonObject.get(status).toString();
				requirementFeature.setValueName(value);
			}
			//获取窗口日期（投产日期）
			Long windowId = requirementFeature.getCommissioningWindowId();
			if(windowId != null) {
				TblCommissioningWindow commissioningWindow = commissioningWindowMapper.selectCommissioningWindowById(windowId);
				requirementFeature.setWindowDate(commissioningWindow.getWindowDate());
			}
			
			requirementFeature.setSystemName(systemName);
			requirementFeature.setRequirementName(requirementName2);
			requirementFeature.setRequirementCode(requirementCode);
			
			
		}
		
		return list;
	}

	//关联开发任务
	@Override
	@Transactional(readOnly=false)
	public void relationRequirement(Long windowId, Long[] ids, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		//得到本次修改者
		Long currentUserId = CommonUtil.getCurrentUserId(request);
		//本次修改时间
		Timestamp time = new Timestamp(new Date().getTime());
		HashMap<String, Object> map = new HashMap<>();
		map.put("windowId", windowId);
		map.put("ids", ids);
		map.put("currentUserId", currentUserId);
		map.put("time", time);
		requirementFeatureMapper.relationRequirement(map);
		//排期告警  同需求下开发任务排期不一致 ---ztt
		for (Long id : ids) {
			TblRequirementFeature tblRequirementFeature = requirementFeatureMapper.getReqFeatureById(id);
			//根据该任务所在需求 查询这个需求下的开发任务 以投产窗口分组
			List<Map<String, Object>> list = commissioningWindowMapper.getReqFeatureGroupbyWindow(tblRequirementFeature.getRequirementId());
			if (!list.isEmpty() && list.size()>1) {//说明该需求下的开发任务不全都是在同一个投产窗口  需提醒
				String messContent = "需求  “"+tblRequirementFeature.getRequirementCode()+" | "+tblRequirementFeature.getRequirementName()+"” 下，";
				for (Map<String, Object> map1 : list) {
					messContent+= "开发任务"+map1.get("featureCode")+ (map1.get("windowName")== null ? "暂未排期，" : "排期在"+(map1.get("windowName")+"投产窗口，"));
				}
				//接收人 项目管理岗、开发管理岗、
				String userIds = "";
				userIds += requirementMapper.getProManageUserIds(tblRequirementFeature.getRequirementId())+",";//获取该需求所在系统所在项目的项目管理岗
				userIds += tblRequirementFeature.getManageUserId()+",";//开发管理岗
				if(StringUtils.isNotBlank(userIds)) {
					Map<String,Object> emWeMap = new HashMap<String, Object>();
				    emWeMap.put("messageTitle", "【IT开发测试管理系统】- 任务排期不一致");
				    emWeMap.put("messageContent",messContent+"请留意。");
				    emWeMap.put("messageReceiver",userIds );//接收人 
				    emWeMap.put("sendMethod", 3);//发送方式 3 邮件和微信
				    projectToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
				}
			}
			
		}
		
	}

	@Override
	public List<TblCommissioningWindow> findCommissioningByWindowDate(TblCommissioningWindow commissioningWindow) {
		return commissioningWindowMapper.findCommissioningByWindowDate(commissioningWindow);
	}

	@Override
	public List<TblCommissioningWindow> selectAllWindows(TblCommissioningWindow tblCommissioningWindow,
			Integer pageNumber, Integer pageSize) {
		Map<String,Object> map = new HashMap<>();
		map.put("start", (pageNumber-1) *pageSize);
		map.put("pageSize", pageSize);
		map.put("tblCommissioningWindow", tblCommissioningWindow);
		return commissioningWindowMapper.selectAllWindows(map);
	}

	
	//根据投产窗口id通过开发任务查找相关系统
	@Override
	public List<TblSystemInfo> getSystemsByWindowId(Long windowId) {
		return systemInfoMapper.getSystemsByWindowId(windowId);
	}

	
	//获取导出数据
//	@Override
//	public List<TblRequirementInfo> getExportRequirements(Long windowId, String systemIds) {
//		Map<String,Object> map = new HashMap<>();
//		map.put("windowId", windowId);
//		map.put("systemIds", systemIds);
//		List<Long> ids = requirementFeatureMapper.getRequirementId(map);
//		List<TblRequirementInfo> list = requirementMapper.getRequirementsByIds(ids);  //需求
//		for (TblRequirementInfo tblRequirementInfo : list) {
//			Map<String,Object> map2 = new HashMap<>();
//			map2.put("windowId", windowId);
//			map2.put("systemIds", systemIds);
//			map2.put("requirementId", tblRequirementInfo.getId());
//			List<TblRequirementFeature> reqfeaList = requirementFeatureMapper.getRequirementFeatures(map2);
//			tblRequirementInfo.setReqfeaList(reqfeaList);   //开发任务
//			for (TblRequirementFeature tblRequirementFeature : reqfeaList) {
//				List<TblDevTask> devTaskList = tblDevTaskMapper.getDevTasks(tblRequirementFeature.getId());
//				tblRequirementFeature.setDevTaskList(devTaskList);  //工作任务
//			}
//		}
//		
//		return list;
//	}

	@Override
	public void export(List<TblRequirementInfo> list, List<TblRequirementFeature> feaList, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		String sheetName = "Release_notes";
        String[] title = {"编号|标题","描述","需求类型","状态","开发管理岗","执行人","开发人员","代码"};

        String filename = "Release_notes"+DateUtil.
				getDateString(new Timestamp(new Date().getTime()),"yyyyMMddHHmmss")+".xls";

        HSSFFont font = workbook.createFont();
        font.setFontName("微软雅黑"); 
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
        font.setItalic(false); 
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillPattern(HSSFCellStyle.VERTICAL_CENTER); 
        headStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        headStyle.setFont(font);

        HSSFCellStyle valueStyle = workbook.createCellStyle();
        valueStyle.setWrapText(true);
        
        HSSFCellStyle valueStyle1 = workbook.createCellStyle();
        valueStyle1.setWrapText(true);
        
        int size = 0;
        if(list != null && list.size() > 0) {
        	size += list.size();
        	for (int a = 0; a < list.size(); a++) {
        		List<TblRequirementFeature> reqfeaList = list.get(a).getReqfeaList();
        		if(reqfeaList != null && reqfeaList.size() > 0) {
        			size += reqfeaList.size();
        			for (int b = 0; b < reqfeaList.size(); b++) {
        				List<TblDevTask> taskList = reqfeaList.get(b).getDevTaskList();
        				if(taskList != null && taskList.size() > 0) {
        					size += taskList.size();
        				}
        			}
        		}
        	}
        }
        if(feaList != null && feaList.size() > 0) {
        	size += feaList.size();
        	for (int a = 0; a < feaList.size(); a++) {
        		List<TblDevTask> taskList = feaList.get(a).getDevTaskList();
        		if(taskList != null && taskList.size() > 0) {
        			size += taskList.size();
        		}
        	}
        }
        
        int i = 0;
        String[][] value = new String[size][];
        if (list != null && list.size() > 0) {
        	for (int a = 0; a < list.size(); a++) {
        		value[i] = new String[title.length+1];
        		value[i][0] = a%2!=0?"1":"2";
				value[i][1] = "【"+list.get(a).getRequirementCode()+"】"+list.get(a).getRequirementName();
				value[i][2] = list.get(a).getRequirementOverview();
				value[i][3] = !list.get(a).getRequirementType().equals("") ? getValue(list.get(a).getRequirementType(),"TBL_REQUIREMENT_INFO_REQUIREMENT_TYPE"):"";
				value[i][4] = !list.get(a).getRequirementStatus().equals("") ? getValue(list.get(a).getRequirementStatus(),"TBL_REQUIREMENT_INFO_REQUIREMENT_STATUS"):"";
				value[i][5] = list.get(a).getDevManageUserName();
				value[i][6] = "";
				value[i][7] = "";
				value[i][8] = "";
				i = i+1;
				List<TblRequirementFeature> reqfeaList = list.get(a).getReqfeaList();
				if(reqfeaList != null && reqfeaList.size() > 0) {
					for(int b = 0; b < reqfeaList.size(); b++) {
//						value = new String[i][];
		        		value[i] = new String[title.length+1];
		        		value[i][0] = a%2!=0?"1":"2";
		        		value[i][1] = "["+reqfeaList.get(b).getFeatureCode()+"]"+reqfeaList.get(b).getFeatureName();
		        		value[i][2] = reqfeaList.get(b).getFeatureOverview();
		        		value[i][3] = "";
		        		value[i][4] = !reqfeaList.get(b).getRequirementFeatureStatus().equals("") ? getValue(reqfeaList.get(b).getRequirementFeatureStatus(),"TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS"):"";
		        		value[i][5] = reqfeaList.get(b).getManageUserName();
		        		value[i][6] = reqfeaList.get(b).getExecuteUserName();
		        		value[i][7] = "";
		        		value[i][8] = "";
		        		i = i+1;
		        		List<TblDevTask> taskList = reqfeaList.get(b).getDevTaskList();
		        		if(taskList != null && taskList.size() > 0) {
		        			for(int c = 0; c < taskList.size(); c++) {
//		        				value = new String[i][];
				        		value[i] = new String[title.length+1];
				        		value[i][0] = a%2!=0?"1":"2";
				        		value[i][1] = "["+taskList.get(c).getDevTaskCode()+"]"+taskList.get(c).getDevTaskName();
				        		value[i][2] = taskList.get(c).getDevTaskOverview();
				        		value[i][3] = "";
				        		value[i][4] = taskList.get(c).getDevTaskStatus() != null ? getValue2(taskList.get(c).getDevTaskStatus(),"TBL_DEV_TASK_DEV_TASK_STATUS"):"";
				        		value[i][5] = "";
				        		value[i][6] = "";
				        		value[i][7] = taskList.get(c).getDevUserName();
				        		value[i][8] = getDevTaskScmFile(taskList.get(c).getId(),reqfeaList.get(b).getSystemId());
				        		i = i+1; 
		        			}
		        		}
					}
				}
			}
        }        
        if(feaList != null && feaList.size() > 0) {
        	for(int b = 0; b < feaList.size(); b++) {
//				value = new String[i][];
        		value[i] = new String[title.length+1];
        		value[i][0] = (list.size()+b)%2!=0?"1":"2";
        		value[i][1] = "["+feaList.get(b).getFeatureCode()+"]"+feaList.get(b).getFeatureName();
        		value[i][2] = feaList.get(b).getFeatureOverview();
        		value[i][3] = "";
        		value[i][4] = !feaList.get(b).getRequirementFeatureStatus().equals("") ? getValue(feaList.get(b).getRequirementFeatureStatus(),"TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS"):"";
        		value[i][5] = feaList.get(b).getManageUserName();
        		value[i][6] = feaList.get(b).getExecuteUserName();
        		value[i][7] = "";
        		value[i][8] = "";
        		i = i+1;
        		List<TblDevTask> taskList = feaList.get(b).getDevTaskList();
        		if(taskList != null && taskList.size() > 0) {
        			for(int c = 0; c < taskList.size(); c++) {
//        				value = new String[i][];
		        		value[i] = new String[title.length+1];
		        		value[i][0] = (list.size()+b)%2!=0?"1":"2";
		        		value[i][1] = "["+taskList.get(c).getDevTaskCode()+"]"+taskList.get(c).getDevTaskName();
		        		value[i][2] = taskList.get(c).getDevTaskOverview();
		        		value[i][3] = "";
		        		value[i][4] = taskList.get(c).getDevTaskStatus() != null ? getValue2(taskList.get(c).getDevTaskStatus(),"TBL_DEV_TASK_DEV_TASK_STATUS"):"";
		        		value[i][5] = "";
		        		value[i][6] = "";
		        		value[i][7] = taskList.get(c).getDevUserName();
		        		value[i][8] = getDevTaskScmFile(taskList.get(c).getId(),feaList.get(b).getSystemId());;
		        		i = i+1; 
        			}
        		}
			}
        }
        export(title, sheetName, filename, value, workbook, 0, headStyle, valueStyle, valueStyle1,request, response);
	}
	
	

	public static void export(String[] title, String sheetName, String filename, String[][] values, Workbook workbook,
			Integer headRowNum, CellStyle headStyle, CellStyle valueStyle, CellStyle valueStyle1,HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 创建头部
		Cell cell = null;
		Sheet sheet = workbook.createSheet(sheetName);

//		headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//		headStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());// 设置背景色
		headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		headStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		headStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		headStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		headStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

		Row row = sheet.createRow(headRowNum);
		for (int i = 0, len = title.length; i < len; i++) {
			cell = row.createCell(i);
			row.setHeightInPoints(25);
			cell.setCellValue(title[i]);
			cell.setCellStyle(headStyle);
			int colWidth = sheet.getColumnWidth(i) * 2;
			sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
		}

		valueStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 纯色使用前景颜色填充
		valueStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		valueStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		valueStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		valueStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		valueStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		valueStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		valueStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		valueStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		valueStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		
		valueStyle1.setFillPattern(HSSFCellStyle.VERTICAL_CENTER); 
        valueStyle1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		valueStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		valueStyle1.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		valueStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		valueStyle1.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		valueStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		valueStyle1.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		valueStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		valueStyle1.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        

		// 创建内容
		if (values != null && values.length > 0) {
			for (int i = 0, len = values.length; i < len; i++) {
				row = sheet.createRow(i + 1);
				for (int j = 1; j < values[i].length; j++) {
					Object val = values[i][j];
					if(val == null){
						val = "";
					} else {
						if(val instanceof String) {
							val = HtmlUtils.htmlUnescape(val.toString());
						}
					}
					// 将内容按顺序赋给对应的列对象
					cell = row.createCell(j-1);
					cell.setCellValue(val.toString());
					if(values[i][0] == "1") {
						cell.setCellStyle(valueStyle); // 白色
					} else if(values[i][0] == "2") {
						cell.setCellStyle(valueStyle1); // 灰色
					}
				}
				 setSizeColumn(sheet,values[i].length);
			}
		}

		// 导出 也就是下载功能， 使用输出流
		String useragent = request.getHeader("User-Agent");
		if (useragent.contains("Firefox")) {
			filename = "=?UTF-8?B?" + new BASE64Encoder().encode(filename.getBytes("utf-8")) + "?=";
		} else {
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+", " ");
		}
		OutputStream out = response.getOutputStream();

		// 导出 输出流 设置下载头信息 Content-Disposition 设置mime类型
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		workbook.write(out);
		out.flush();
		out.close();
	}
	
	// 自适应宽度(中文支持)
		private static void setSizeColumn(Sheet sheet, int size) {
			for (int columnNum = 0; columnNum < size; columnNum++) {
				int columnWidth = sheet.getColumnWidth(columnNum) / 256;
				for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
					Row currentRow;
					// 当前行未被使用过
					if (sheet.getRow(rowNum) == null) {
						currentRow = sheet.createRow(rowNum);
					} else {
						currentRow = sheet.getRow(rowNum);
					}
					if (currentRow.getCell(columnNum) != null) {
						Cell currentCell = currentRow.getCell(columnNum);
						if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							int length = currentCell.getStringCellValue().getBytes().length;
							if (columnWidth < length) {
								columnWidth = length;
							}
						}
					}
				}

				sheet.setColumnWidth(columnNum, columnWidth < 255 ? columnWidth * 256 : 256 * 50);
			}
		}

	
	
	//数据字典
	private String getValue(String key, String string) {
		String name = "";
		String redisStr = redisUtils.get(string).toString();
		JSONObject jsonObj = JSON.parseObject(redisStr);
		if(!key.equals("")) {
		name = jsonObj.get(key).toString();
		}
		return name;
	}
	
	private String getValue2(Integer key, String string) {
		String redisStr = redisUtils.get(string).toString();
		JSONObject jsonObj = JSON.parseObject(redisStr);
		String name = jsonObj.get(key).toString();
		return name;
	}
	
	private String getDevTaskScmFile(Long id, Long systemId) {
		String file = "";
		List<Integer> types = tblDevTaskMapper.getScmType(systemId);
		if(types.size() == 2) {
			List<TblDevTaskScmFile> files = tblDevTaskScmFileMapper.getFile(id);  //svn提交文件
			List<TblDevTaskScmGitFile> gitFiles = tblDevTaskScmGitFileMapper.getGitFile(id);  //git提交文件
			for (TblDevTaskScmFile tblDevTaskScmFile : files) {
					file += tblDevTaskScmFile.getOperateType()+" "+tblDevTaskScmFile.getCommitFile()+"\r\n";
			}
			for (TblDevTaskScmGitFile tblDevTaskScmGitFile : gitFiles) {
					file += tblDevTaskScmGitFile.getOperateType()+" "+tblDevTaskScmGitFile.getCommitFile()+"\r\n";
			}
		}
		if(types.size() == 1 && types.get(0) == 1) { //git
			List<TblDevTaskScmGitFile> gitFiles = tblDevTaskScmGitFileMapper.getGitFile(id);  //git提交文件
			for (TblDevTaskScmGitFile tblDevTaskScmGitFile : gitFiles) {
					file += tblDevTaskScmGitFile.getOperateType()+" "+tblDevTaskScmGitFile.getCommitFile()+"\r\n";
			}
		}
		if(types.size() == 1 && types.get(0) == 2) {  //svn
			List<TblDevTaskScmFile> files = tblDevTaskScmFileMapper.getFile(id);  //svn提交文件
			for (TblDevTaskScmFile tblDevTaskScmFile : files) {
					file += tblDevTaskScmFile.getOperateType()+" "+tblDevTaskScmFile.getCommitFile()+"\r\n";
			}
		}
		return file;
	}
	

}
