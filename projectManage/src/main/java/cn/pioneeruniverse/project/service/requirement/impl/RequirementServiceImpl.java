package cn.pioneeruniverse.project.service.requirement.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.project.common.BrowserUtil;
import cn.pioneeruniverse.project.common.SynRequirementSystemUtil;
import cn.pioneeruniverse.project.common.SynRequirementUtil;
import cn.pioneeruniverse.project.dao.mybatis.RequirementAttachementMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirmentSystemMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblRequirementAttentionMapper;
import cn.pioneeruniverse.project.entity.ExtendedField;
import cn.pioneeruniverse.project.entity.TblRequirementAttachement;
import cn.pioneeruniverse.project.entity.TblRequirementAttention;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.entity.TblRequirementSystem;
import cn.pioneeruniverse.project.feignInterface.ProjectToSystemInterface;
import cn.pioneeruniverse.project.service.requirement.RequirementService;
import cn.pioneeruniverse.project.vo.SynRequirement;
import cn.pioneeruniverse.project.vo.SynRequirementSystem;

@Transactional(readOnly = true)
@Service("requirementService")
public class RequirementServiceImpl implements RequirementService {

	@Autowired
	private RequirementMapper tblRequirementInfoMapper;
	@Autowired
	private RequirementAttachementMapper attaMapper;	
	@Autowired
	private TblRequirementAttentionMapper tblRequirementAttentionMapper;	
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private S3Util s3Util;
	@Autowired
	private RequirmentSystemMapper tblRequirementSystemMapper;
	@Autowired
	private ProjectToSystemInterface projectToSystemInterface;
	@Autowired
	private RequirementMapper requirementMapper;
	
	@Override
	public int getCountRequirement(TblRequirementInfo requirement,List<String> roleCodes) {
		Map<String, Object> map = getRequirementMap(requirement);

		map.put("req", requirement);
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			return tblRequirementInfoMapper.getCountRequirement1(map);
		}else {
			return tblRequirementInfoMapper.getCountRequirement(map);
		}

	}
	
	@Override
	public List<TblRequirementInfo> getAllRequirement(TblRequirementInfo requirement, Integer pageIndex,Integer pageSize,List<String> roleCodes) {
		Map<String, Object> map = getRequirementMap(requirement);
		List<TblRequirementInfo> reqList = new ArrayList<TblRequirementInfo>();
		if (pageIndex != null && pageSize != null) {			
			//PageHelper.startPage(pageIndex, pageSize);						
			map.put("pageIndex", (pageIndex - 1) * pageSize);
			map.put("pageSize", pageSize);	
			map.put("req", requirement);
			if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
				reqList = tblRequirementInfoMapper.getAllRequirement1(map);
			}else {
				reqList = tblRequirementInfoMapper.getAllRequirement(map);
			}
//			for (TblRequirementInfo tblRequirementInfo : reqList) {
//				String requirement_ids = tblRequirementInfo.getRequirementIds();
//				if(requirement_ids != null) {
//					List<String> requirementNames = tblRequirementInfoMapper.getRelationrequirementNames(requirement_ids);
//					tblRequirementInfo.setRequirementNames(requirementNames);
//				}
//			}

		} else {
			map.put("req", requirement);
			if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
				reqList = tblRequirementInfoMapper.excelRequirement1(map);
			}else {
				reqList = tblRequirementInfoMapper.excelRequirement(map);
			}
		}		
		return reqList;
	}
		
	//查看需求详情
	@Override
	public Map<String, Object> findRequirementById(Long rId,Long parentId) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuilder triSons= new StringBuilder();
		
		TblRequirementInfo tri = tblRequirementInfoMapper.findRequirementById(rId);
		String requirement_ids = tri.getRequirementIds();
		if(requirement_ids != null) {
			List<String> requirementNames = tblRequirementInfoMapper.getRelationrequirementNames(requirement_ids);
			tri.setRequirementNames(requirementNames);
		}
		if(tri.getParentId() != null) {
			String code = tblRequirementInfoMapper.getCodeById(tri.getParentId());
			tri.setParentCode(code);
		}
		//查询需求系统模块
		List<TblRequirementSystem> reqSysList = tblRequirementSystemMapper.getReqSystemByReqId(rId);
		if(reqSysList != null && reqSysList.size() > 0) {
			for (TblRequirementSystem requirementSystem : reqSysList) {
				String assetSystemName = tblRequirementSystemMapper.getAssetSystemNameByAssetSystemId(requirementSystem.getAssetSystemTreeId());
				String systemName = tblRequirementSystemMapper.getSystemNameBySystemId(requirementSystem.getSystemId());
				requirementSystem.setAssetSystemName(assetSystemName);
				requirementSystem.setSystemName(systemName);
			}
		}
		tri.setList(reqSysList);
//		List<Long> sysIds = tblRequirementSystemMapper.getSystemByrequirementId(rId);
//		if(sysIds.size()>0) {
//			List<String> sysNames = tblRequirementSystemMapper.getSystemNameByIds(sysIds);
//			tri.setSystemIds(StringUtils.join(sysIds, ","));
//			tri.setSystemNames(StringUtils.join(sysNames, ","));
//		}
//		Long assetSystemId = tblRequirementSystemMapper.getAssetSystemIdByRequirementId(rId);
//		String assetSystemName = tblRequirementSystemMapper.getAssetSystemNameByAssetSystemId(assetSystemId);
//		tri.setModuleId(assetSystemId);
//		tri.setModuleName(assetSystemName);
		
		
		TblRequirementInfo triFather = tblRequirementInfoMapper.findFatherReq(parentId);
		List<TblRequirementInfo> triSon = tblRequirementInfoMapper.findSonReq(rId);			
		List<TblRequirementAttachement> attachements = attaMapper.getRequirementAttachement(rId);
		for(int i=0;i< triSon.size();i++) {
			if(i<triSon.size()-1) {
				triSons.append(triSon.get(i).getRequirementCode()+" | "+triSon.get(i).getRequirementName()+", ");
			}else {
				triSons.append(triSon.get(i).getRequirementCode()+" | "+triSon.get(i).getRequirementName());
			}
		}
		result.put("data", tri);
		result.put("triFather", triFather);			
		result.put("triSon", triSons);
		result.put("attachements", attachements);
		return result;
	}
	@Override
	public List<TblRequirementInfo> findRequirementByName(TblRequirementInfo requirementInfo){		
		return tblRequirementInfoMapper.findRequirementByName(requirementInfo);		
	}
	//编辑需求
	@Override
	public Map<String, Object> toEditRequirementById(Long rId) {
		Map<String, Object> result = new HashMap<String, Object>();		
		TblRequirementInfo tri = tblRequirementInfoMapper.findRequirementById(rId);
		String requirement_ids = tri.getRequirementIds();
		if(requirement_ids != null) {
			List<String> requirementNames = tblRequirementInfoMapper.getRelationrequirementNames(requirement_ids);
			tri.setRequirementNames(requirementNames);
		}
		if(tri.getParentId() != null) {
			String code = tblRequirementInfoMapper.getCodeById(tri.getParentId());
			tri.setParentCode(code);
		}
		//查询需求系统模块
		List<TblRequirementSystem> reqSysList = tblRequirementSystemMapper.getReqSystemByReqId(rId);
		if(reqSysList != null && reqSysList.size() > 0) {
			for (TblRequirementSystem requirementSystem : reqSysList) {
				String assetSystemName = tblRequirementSystemMapper.getAssetSystemNameByAssetSystemId(requirementSystem.getAssetSystemTreeId());
				String systemName = tblRequirementSystemMapper.getSystemNameBySystemId(requirementSystem.getSystemId());
				String systemCode = tblRequirementSystemMapper.getSystemCodeBySystemId(requirementSystem.getSystemId());
				requirementSystem.setAssetSystemName(assetSystemName);
				requirementSystem.setSystemName(systemName);
				requirementSystem.setSystemCode(systemCode);
			}
		}
		tri.setList(reqSysList);
		result.put("data", tri);		
		return result;
	}
	
	 /**
     * 	上传需求附件至itmp_db
     * @param files
     * @param reqId
     * @param request
     */
    @Override
    @Transactional(readOnly = false)
    public Map<String,Object> uploadFileItmp(MultipartFile[] files, Long reqId, HttpServletRequest request) throws Exception{
    	Map<String, Object> map = new HashMap<>();
    	List<TblRequirementAttachement> listAtta= new ArrayList<TblRequirementAttachement>();   	
        if (files.length > 0 && files !=null) {
            for (MultipartFile file : files) {
                if (Objects.isNull(file) || file.isEmpty()) {
                	map.put("message", "NULL");
                	return map;                    
                }
                TblRequirementAttachement atta = putFile(file,request);
                atta = (TblRequirementAttachement) CommonUtil.setBaseValue(atta,request);
                atta.setRequirementId(reqId);
                
                attaMapper.insertSelective(atta);
                listAtta.add(atta);           
            }
            map.put("message", "SECCESS");
            map.put("listAtta", JSONObject.toJSONString(listAtta));
        }
        return map;
    }
    
    /**
     * 	上传需求附件至tmp_db
     */
    @Override
    @DataSource(name="tmpDataSource")
    @Transactional(readOnly = false)
    public void uploadFileTmp(String listAtta) throws Exception{  
    	List<TblRequirementAttachement> resultList = JSONObject.parseArray(listAtta, TblRequirementAttachement.class);
        for (TblRequirementAttachement atta : resultList) {                
            attaMapper.insert(atta);
        }      
    }
    
    /**
     * 上传单个文件
     * @param file
     * @return
     * @throws Exception
     */
	@Transactional(readOnly = false)
	public TblRequirementAttachement putFile(MultipartFile file, HttpServletRequest request) throws Exception {
		TblRequirementAttachement atta = new TblRequirementAttachement();
		if (!file.isEmpty()) {
			InputStream inputStream = file.getInputStream();
			String extension = file.getOriginalFilename()
					.substring(file.getOriginalFilename().lastIndexOf(".") + 1);// 后缀名
			String fileNameOld = file.getOriginalFilename();
			if (BrowserUtil.isMSBrowser(request)) {
				fileNameOld = fileNameOld.substring(fileNameOld.lastIndexOf("\\") + 1);
			}

			String keyname = s3Util.putObject(s3Util.getRequirementBucket(), fileNameOld, inputStream);
			atta.setFileS3Bucket(s3Util.getRequirementBucket());
			atta.setFileS3Key(keyname);
			atta.setFileNameOld(fileNameOld);
			atta.setFileType(extension);
		}		
		return atta;
	}
    
	//删除itmp_db需求附件
	@Override
	@Transactional(readOnly = false)
	public TblRequirementAttachement removeAttItmp(TblRequirementAttachement atta, HttpServletRequest request) {
		atta.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		atta.setLastUpdateDate(new Timestamp(new Date().getTime()));
		attaMapper.removeAttachement(atta);
		return atta;		
	}
	
	//删除tmp_db需求附件
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly = false)
	public void removeAttTmp(TblRequirementAttachement atta) {
		attaMapper.removeAttachement(atta);		
	}
	
	//新增需求至itmp_db
	@Override
	@Transactional(readOnly = false)
	public Map<String, Object> insertRequirementItmp(TblRequirementInfo requirementInfo,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		requirementInfo = (TblRequirementInfo) CommonUtil.setBaseValue(requirementInfo,request);
		try {
			requirementInfo.setRequirementCode(getRequirementCode1());
		}catch (Exception e) {
			
		}
		tblRequirementInfoMapper.insertRequirement(requirementInfo);		
		
		
		
        map.put("reqId",requirementInfo.getId());
        map.put("requirementInfo", JSONObject.toJSONString(requirementInfo));
        //新增需求系统模块
        List<TblRequirementSystem> reqsysList = requirementInfo.getList();
        if(reqsysList !=null && reqsysList.size()>0) {
        	for (TblRequirementSystem requirementSystem : reqsysList) {
        		requirementSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
            	requirementSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
            	requirementSystem.setCreateDate(new Timestamp(new Date().getTime()));
            	requirementSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
            	requirementSystem.setStatus(1);
            	requirementSystem.setRequirementId(requirementInfo.getId());
            	tblRequirementSystemMapper.insertReqSystem(requirementSystem);
			}
        }
//        String systemIds = requirementInfo.getSystemIds();
//        Long moduleId = requirementInfo.getModuleId();
//        if(!systemIds.equals("") || moduleId != null) {
//        	TblRequirementSystem requirementSystem = new TblRequirementSystem();
//        	requirementSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
//        	requirementSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
//        	requirementSystem.setCreateDate(new Timestamp(new Date().getTime()));
//        	requirementSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
//        	requirementSystem.setAssetSystemTreeId(moduleId);
//        	requirementSystem.setStatus(1);
//        	requirementSystem.setRequirementId(requirementInfo.getId());
//        	if(!systemIds.equals("")) {
//        		String[] systems = systemIds.split(",");
//        		for (String systemId : systems) {
//					requirementSystem.setSystemId(Long.valueOf(systemId));
//					tblRequirementSystemMapper.insertReqSystem(requirementSystem);
//				}
//        	}else {
//        		tblRequirementSystemMapper.insertReqSystem(requirementSystem);
//        	}
//        }
        
    	//给项目管理岗发送邮件和微信 --ztt
  		Map<String,Object> emWeMap = new HashMap<String, Object>();
  		emWeMap.put("messageTitle", "【IT开发测试管理系统】- 收到一个新分配的需求");
  		emWeMap.put("messageContent","您收到一个新的需求：“"+ requirementInfo.getRequirementCode()+" | "+requirementInfo.getRequirementName()+"，请及时处理。”");
  		String userIds = requirementMapper.getProManageUserIds(requirementInfo.getId());//获取该需求所在系统所在项目的项目管理岗 
  		emWeMap.put("messageReceiver",userIds);//接收人 项目管理岗
  		emWeMap.put("sendMethod", 3);//发送方式 3 邮件和微信
  		projectToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
        return map;
	}
	
	//新增需求至tmp_db
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly = false)
	public void insertRequirementTmp(String requirementInfo) {
		TblRequirementInfo requirementInfo1 = JSONObject.parseObject(requirementInfo, TblRequirementInfo.class);
		
		TblRequirementInfo requirement = tblRequirementInfoMapper.findRequirementById1(requirementInfo1.getId());				
		if(requirement==null) {
			requirementInfo1.setRequirementCode(getRequirementCode1());
			tblRequirementInfoMapper.insertRequirement(requirementInfo1);	
		}else {
			tblRequirementInfoMapper.updateRequirementData(requirementInfo1);
		}			    
	}
	
	//发送消息
	@Override
	public void sendAddMessage(HttpServletRequest request, TblRequirementInfo requirementInfo) {
		// TODO Auto-generated method stub
		String userIds = "";
		if(requirementInfo.getApplyUserId()!=null) {
			userIds += requirementInfo.getApplyUserId() + ",";
		}
		if(requirementInfo.getDevelopmentManageUserId()!=null) {
			userIds += requirementInfo.getDevelopmentManageUserId() + ",";
		}
		if(requirementInfo.getRequirementManageUserId()!=null) {
			userIds += requirementInfo.getRequirementManageUserId() + ",";
		}
		if(requirementInfo.getRequirementAcceptanceUserId()!=null) {
			userIds += requirementInfo.getRequirementAcceptanceUserId() + ",";
		}
		List<TblRequirementSystem> list = requirementInfo.getList();
		String systemIds = "";
		if(list != null && list.size()>0) {
			for (TblRequirementSystem tblRequirementSystem : list) {
				systemIds += tblRequirementSystem.getSystemId() + ",";
			}
		}
		if(!systemIds.equals("")) {
			List<Long> ids = tblRequirementInfoMapper.getUserIdsBySystemIds(systemIds);
			for (Long id : ids) {
				userIds += id+",";
			}
		}
		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","收到一个新需求");
		map.put("messageContent",requirementInfo.getRequirementCode()+"|"+requirementInfo.getRequirementName());
		map.put("messageReceiverScope",2);
		map.put("messageReceiver",userIds);
		projectToSystemInterface.insertMessage(JSON.toJSONString(map));
	}

	//编辑需求至itmp_db
	@Override
	@Transactional(readOnly = false)
	public Map<String, Object> updateRequirementItmp(TblRequirementInfo requirementInfo,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		requirementInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		requirementInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		
		tblRequirementInfoMapper.updateRequirement(requirementInfo);		
        map.put("reqId",requirementInfo.getId());
        map.put("requirementInfo", JSONObject.toJSONString(requirementInfo));
        //编辑需求模块
        tblRequirementSystemMapper.updateReqAssSystemTree(requirementInfo);
        List<TblRequirementSystem> list = requirementInfo.getList();
        if(list != null && list.size() > 0) {
        	for (TblRequirementSystem requirementSystem : list) {
        		requirementSystem.setCreateBy(CommonUtil.getCurrentUserId(request));
            	requirementSystem.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
            	requirementSystem.setCreateDate(new Timestamp(new Date().getTime()));
            	requirementSystem.setLastUpdateDate(new Timestamp(new Date().getTime()));
            	requirementSystem.setStatus(1);
            	requirementSystem.setRequirementId(requirementInfo.getId());
        		tblRequirementSystemMapper.insertReqSystem(requirementSystem);
			}
        }
		return map;
	}
	
	//编辑需求至tmp_db
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly = false)
	public void updateRequirementTmp(String requirementInfo) {
		TblRequirementInfo requirementInfo1 = JSONObject.parseObject(requirementInfo, TblRequirementInfo.class);
		TblRequirementInfo requirement = tblRequirementInfoMapper.findRequirementById1(requirementInfo1.getId());				
		if(requirement==null) {
			requirementInfo1.setRequirementCode(getRequirementCode1());
			tblRequirementInfoMapper.insertRequirement(requirementInfo1);	
		}else {
			tblRequirementInfoMapper.updateRequirement(requirementInfo1);
		}		
	}
	
	//编辑发送消息
	@Override
	public void sendEditMessage(HttpServletRequest request, TblRequirementInfo requirementInfo, List<TblRequirementAttention> attentionList) {
		String userIds = "";
		for (TblRequirementAttention attention : attentionList) {
			userIds += attention.getUserId() + ",";
		}
		userIds = userIds.substring(0, userIds.length() - 1);
		String code = tblRequirementInfoMapper.getCodeById(requirementInfo.getId());
		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","你关注的需求有更新");
		map.put("messageContent",code+"|"+requirementInfo.getRequirementName());
		map.put("messageReceiverScope",2);
		map.put("messageReceiver",userIds);
		projectToSystemInterface.insertMessage(JSON.toJSONString(map));
	}
	
	@Override
	public List<TblRequirementAttention> getAttentionList(TblRequirementAttention attention) {
		List<TblRequirementAttention> attentionList = tblRequirementAttentionMapper.selectList(new EntityWrapper<TblRequirementAttention>(attention));
		return attentionList;
	}
	
	//更新需求关注功能
	@Override
	@Transactional(readOnly = false)
	public void changeAttention(Long id, Integer attentionStatus, HttpServletRequest request) {
		Long userId = CommonUtil.getCurrentUserId(request);
		Timestamp stamp = new Timestamp(new Date().getTime());
		TblRequirementAttention attention = new TblRequirementAttention();
		attention.setRequirementId(id);
		attention.setUserId(userId);
		TblRequirementAttention attentionOld = tblRequirementAttentionMapper.selectOne(attention);
		if (attentionStatus == 1) { //关注
			if (attentionOld == null) {
				CommonUtil.setBaseValue(attention, request);
				tblRequirementAttentionMapper.insert(attention);
			} else if (attentionOld.getStatus() == 2) {
				attentionOld.setStatus(1);
				attentionOld.setLastUpdateBy(userId);
				attentionOld.setLastUpdateDate(stamp);
				tblRequirementAttentionMapper.updateByPrimaryKey(attentionOld);
			}
		} else {
			if (attentionOld != null) {
				attentionOld.setStatus(2);
				attentionOld.setLastUpdateBy(userId);
				attentionOld.setLastUpdateDate(stamp);
				tblRequirementAttentionMapper.updateByPrimaryKey(attentionOld);
			}
		}
	}
	
	
	/*同步需求至itmp_db*/
	@Override
	@Transactional(readOnly = false)
	public Map<String,Object> updateRequirementDataItmp(String requirementList) {
		Map<String, Object> map = new HashMap<>();	
		List<TblRequirementInfo> reqList=new ArrayList<>();
		//把字符串转换成对象
		List<SynRequirement> synReqList = JSONObject.parseArray(requirementList, SynRequirement.class);
		for(SynRequirement synReq:synReqList) {
				//将对象SynRequirement转成对象TblRequirementInfo
				TblRequirementInfo tri =SynRequirementUtil.SynTblRequirementInfo(synReq);
				TblRequirementInfo requirement = tblRequirementInfoMapper.getRequirementInByCode(tri.getRequirementCode());
				if(requirement==null) {
					if(!tri.getRequirementStatus().toUpperCase(Locale.ENGLISH).equals("REQ_CANCELED")) {
						tblRequirementInfoMapper.insertRequirement(tri);
						map.put("reqId", tri.getId());
						map.put("reqStatus", tri.getRequirementStatus());
						reqList.add(tri);
					}
					map.put("synStatus", "insert");
	//				sendAddMessage(null, tri);
					
				}else {
					map.put("reqId", requirement.getId());
					map.put("reqStatus", tri.getRequirementStatus());
					tri.setId(requirement.getId());
					tri.setCreateBy(null);
					tri.setCreateDate(null);
					tblRequirementInfoMapper.updateRequirementData(tri);
					reqList.add(tri);

					map.put("synStatus", "update");
	//				sendAddMessage(null, tri);
				}
			
		}
		if(reqList!=null&&reqList.size()>0) {
			map.put("status", 0);
			map.put("reqList", JSONObject.toJSONString(reqList));
		}else {
			map.put("status", 1);
		}
		return map; 
	}
		
	/*同步需求至tmp_db*/
	@Override
	@DataSource(name="tmpDataSource")
	@Transactional(readOnly = false)
	public int updateRequirementDataTmp(String reqList) {												
		List<TblRequirementInfo> resultList = JSONObject.parseArray(reqList, TblRequirementInfo.class); //把字符串转换成list
		Integer status = 0;
		for(TblRequirementInfo tri : resultList) {
			TblRequirementInfo requirement = tblRequirementInfoMapper.findRequirementById1(tri.getId());				
			if(requirement==null) {
				if(!tri.getRequirementStatus().toUpperCase(Locale.ENGLISH).equals("REQ_CANCELED")) {
					status = tblRequirementInfoMapper.insert(tri);
				}
			}else {
				status = tblRequirementInfoMapper.updateRequirementData(tri);
			}				
		}
		return status;
	}
	
	@Override
	@Transactional(readOnly = false)
	public int deleteRequirement(Long id) {	
		return tblRequirementInfoMapper.deleteRequirement(id);
	}
	
	//查询需求附件
	@Override
	public List<TblRequirementAttachement> getRequirementAttachement(Long id) {		
		return attaMapper.getRequirementAttachement(id);
	}

	@Override
	public List<Map<String, Object>> getAllRequirement2(TblRequirementInfo requirement, int pageNumber, int pageSize) {
		Map<String, Object> map = new HashMap<>();
		int start = (pageNumber - 1) * pageSize;
		map.put("start", start);
		map.put("pageSize", pageSize);
		map.put("requirement", requirement);
		return tblRequirementInfoMapper.getAllReq(map);		
	}
	
	//获取最大的需求编号
	@Override
	public String selectMaxRqeuirementCode() {
		return tblRequirementInfoMapper.selectMaxRqeuirementCode();
	}

	// 新增需求赋值
	private String getRequirementCode1() {
		String featureCode = "";
		Integer codeInt = 0;
		Object object = redisUtils.get(Constants.TMP_TEST_REQ_CODE);
		if (object != null && !"".equals(object)) { // redis有直接从redis中取			
			String code = object.toString();
			if (!StringUtils.isBlank(code)) {
				codeInt = Integer.parseInt(code) + 1;
			}
		} else {									// redis中没有查询数据库中最大的任务编号			
			String maxDefectNo = this.selectMaxRqeuirementCode();
			codeInt = 1;
			if (StringUtils.isNotBlank(maxDefectNo)) {
				codeInt = Integer.valueOf(maxDefectNo.substring(Constants.TMP_TEST_REQ_CODE.length())) + 1;
			}
		}
		featureCode = Constants.TMP_TEST_REQ_CODE + String.format("%08d", codeInt);
		redisUtils.set(Constants.TMP_TEST_REQ_CODE, String.format("%08d", codeInt));
		return featureCode;
	}

	@Override
	public int getAllRequirementCount(TblRequirementInfo requirement) {
		return tblRequirementInfoMapper.getAllRequirementCount(requirement);
	}

	private Map<String,Object> getRequirementMap(TblRequirementInfo requirement){
		Map<String, Object> map = new HashMap<>();
		if(requirement.getRequirementStatus()!=null&&!requirement.getRequirementStatus().equals("")) {
			String [] struts=requirement.getRequirementStatus().split(",");
			if(struts.length>0){
				map.put("struts", struts);
			}
		}
		if(requirement.getSystemId()!=null&&!requirement.getSystemId().equals("")) {
			String [] systemIds=requirement.getSystemId().split(",");
			if(systemIds.length>0){
				map.put("systemIds", systemIds);
			}
		}
		if(requirement.getFeatureId()!=null&&!requirement.getFeatureId().equals("")) {
			String [] featureIds=requirement.getFeatureId().split(",");
			if(featureIds.length>0){
				map.put("featureIds", featureIds);
			}
		}
		if(requirement.getDevManageIds()!=null&&!requirement.getDevManageIds().equals("")) {
			String [] devManageIds=requirement.getDevManageIds().split(",");
			if(devManageIds.length>0){
				map.put("devManageIds", devManageIds);
			}
		}
		if(requirement.getReqManageIds()!=null&&!requirement.getReqManageIds().equals("")) {
			String [] reqManageIds=requirement.getReqManageIds().split(",");
			if(reqManageIds.length>0){
				map.put("reqManageIds", reqManageIds);
			}
		}
		return map;
	}

	@Override
	public List<ExtendedField> findRequirementField(Long id) {
		// TODO Auto-generated method stub
		 Map<String,Object> map=projectToSystemInterface.findFieldByTableName("tbl_requirement_info");
         String listTxt = JSONArray.toJSONString(map.get("field"));
         List<ExtendedField> extendedFields = JSONArray.parseArray(listTxt, ExtendedField.class);
         if(id!=null) {
             if (extendedFields != null) {
                 Iterator<ExtendedField> it = extendedFields.iterator();
                 while (it.hasNext()) {
                     ExtendedField extendedField = it.next();
                     if (extendedField.getStatus().equals("2")) {
                         it.remove();
                     } else {
                         String fieldName=extendedField.getFieldName();
                         String valueName = tblRequirementInfoMapper.getDevTaskFieldTemplateById(id, fieldName);
                         extendedField.setValueName(valueName == null ? "" : valueName);
                     }
                 }
             }
         }else {
        	 if (extendedFields != null) {
                 Iterator<ExtendedField> it = extendedFields.iterator();
                 while (it.hasNext()) {
                     ExtendedField extendedField = it.next();
                     if (extendedField.getStatus().equals("2")) {
                         it.remove();
                     }
                 }
        	 }
         }
         return extendedFields;
	}

	@Override
	public List<String> getRequirementsByIds(String reqIds) {
		// TODO Auto-generated method stub
		List<String> list = tblRequirementInfoMapper.getRelationrequirementNames(reqIds);
		return list;
	}

	@Override
	public List<String> getsystems(Long id) {
		// TODO Auto-generated method stub
		List<String> list = tblRequirementSystemMapper.getsystems(id);
		return list;
	}

	

	
}
