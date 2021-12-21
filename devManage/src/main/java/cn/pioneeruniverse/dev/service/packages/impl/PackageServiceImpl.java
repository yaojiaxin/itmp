package cn.pioneeruniverse.dev.service.packages.impl;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.entity.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.pioneeruniverse.common.nexus.NexusAssetBO;
import cn.pioneeruniverse.common.nexus.NexusSearchVO;
import cn.pioneeruniverse.common.nexus.NexusUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblArtifactInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblArtifactRequirementFeatureMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblArtifactTagMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblToolInfoMapper;
import cn.pioneeruniverse.dev.service.packages.PackageService;

@Service
@Transactional(readOnly = true)
public class PackageServiceImpl implements PackageService {
	@Autowired
	private TblRequirementFeatureMapper requirementFeatureMapper;
	@Autowired
	private TblToolInfoMapper tblToolInfoMapper;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private TblSystemInfoMapper tblSystemInfoMapper;
	@Autowired
	private TblArtifactInfoMapper artifactInfoMapper;
	@Autowired
	private TblArtifactTagMapper artifactTagMapper;
	@Autowired
	private TblArtifactRequirementFeatureMapper artifactRequirementFeatureMapper;
	@Autowired
	private TblSystemInfoMapper systemInfoMapper;


	/**
	 * 查询开发任务
	 */
	@Override
	public Map<String, Object> findFrature(TblRequirementFeature tblRequirementFeature, Integer page, Integer rows) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<TblRequirementFeature> list = null;
		if (page != null && rows != null) {
			PageHelper.startPage(page, rows);
			list = requirementFeatureMapper.findFrature(tblRequirementFeature);
			Object status = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS");
			Map<String, Object> mapstatus = JSON.parseObject(status.toString());
			for (int i = 0; i < list.size(); i++) {
				for (String key : mapstatus.keySet()) {
					if (key.equals(list.get(i).getRequirementFeatureStatus())) {
						list.get(i).setRequirementFeatureStatus(mapstatus.get(key).toString());
					}
				}
			}
			PageInfo<TblRequirementFeature> pageInfo = new PageInfo<TblRequirementFeature>(list);
			result.put("rows", list);
			result.put("total", pageInfo.getTotal());
			return result;
		} else {
			result.put("rows", requirementFeatureMapper.findFrature(tblRequirementFeature));
		}

		return result;

	}

	/**
	 * 包件标记
	 */
	@Override
	@Transactional(readOnly = false)
	public void addArtifactFrature(String artifactInfoStr, String tagStr, HttpServletRequest request) {
		TblArtifactInfo tblArtifactInfo = JSON.parseObject(artifactInfoStr, TblArtifactInfo.class);
		Long artifactId = tblArtifactInfo.getId();
		if (artifactId != null) {
			artifactTagMapper.deleteByArtifactId(artifactId);
			tblArtifactInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			tblArtifactInfo.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
			artifactInfoMapper.updateByPrimaryKeySelective(tblArtifactInfo);
		} else {
			CommonUtil.setBaseValue(tblArtifactInfo, request);
			artifactInfoMapper.insert(tblArtifactInfo);
		}
		String[] tagArr = tagStr.split(",");
		for (String env : tagArr) {
			TblArtifactTag artifactTag = new TblArtifactTag();
			artifactTag.setEnvironmentType(Integer.valueOf(env));
			artifactTag.setArtifactId(tblArtifactInfo.getId());
			CommonUtil.setBaseValue(artifactTag, request);
			artifactTag.setTagUserId(artifactTag.getCreateBy());
			artifactTag.setTagTime(artifactTag.getCreateDate());
			artifactTagMapper.insert(artifactTag);
		}
	}


	/**
	 * 包件标记(构建调用)
	 */
	@Override
	@Transactional(readOnly = false)
	public void addArtifactFrature(TblArtifactInfo tblArtifactInfo, String tagStr,long userId,String artType) {

		if(artType.equals("2")){
			//判断是否已存在数据库
			Map<String,Object> param=new HashMap<>();
			param.put("GROUP_ID",tblArtifactInfo.getGroupId());
			param.put("ARTIFACT_ID",tblArtifactInfo.getArtifactId());
			param.put("VERSION",tblArtifactInfo.getVersion());
			param.put("NEXUS_PATH",tblArtifactInfo.getNexusPath());
			param.put("STATUS",1);
			List<TblArtifactInfo> ts=artifactInfoMapper.selectByMap(param);
			if(ts.size()>0){
				    TblArtifactInfo tblArtifactInfo1=ts.get(0);

//					tblArtifactInfo.setLastUpdateBy(userId);
//					tblArtifactInfo.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
				    tblArtifactInfo1.setLastUpdateBy(userId);
				    tblArtifactInfo1.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
					artifactInfoMapper.updateByPrimaryKeySelective(tblArtifactInfo1);
				    tblArtifactInfo.setId(tblArtifactInfo1.getId());


			}else{
				tblArtifactInfo.setCreateBy(userId);
				tblArtifactInfo.setCreateDate(new Timestamp(new Date().getTime()));
				tblArtifactInfo.setLastUpdateBy(userId);
				tblArtifactInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
				tblArtifactInfo.setStatus(1);
				artifactInfoMapper.insert(tblArtifactInfo);
			}


			//判断是否存在
			Map<String,Object> paramTag=new HashMap<>();
			paramTag.put("artifactId",tblArtifactInfo.getId());
			paramTag.put("env",tagStr);
			List<TblArtifactTag> tagFlags=	artifactTagMapper.selectByArtifactIdAndEnv(paramTag);
			if(tagFlags.size()>0){
				TblArtifactTag updateArtifactTag = new TblArtifactTag();
				updateArtifactTag.setId(tagFlags.get(0).getId());
				updateArtifactTag.setLastUpdateBy(userId);
				updateArtifactTag.setLastUpdateDate(new Timestamp(new Date().getTime()));
				artifactTagMapper.updateByPrimaryKeySelective(updateArtifactTag);

			}else{
				addTag(tagStr,userId,tblArtifactInfo);
			}


		} else {

			tblArtifactInfo.setCreateBy(userId);
			tblArtifactInfo.setCreateDate(new Timestamp(new Date().getTime()));
			tblArtifactInfo.setLastUpdateBy(userId);
			tblArtifactInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
			tblArtifactInfo.setStatus(1);
			artifactInfoMapper.insert(tblArtifactInfo);
			addTag(tagStr,userId,tblArtifactInfo);
		}



	}

	private void addTag(String tagStr,Long userId,TblArtifactInfo tblArtifactInfo){
		TblArtifactTag artifactTag = new TblArtifactTag();
		artifactTag.setEnvironmentType(Integer.valueOf(tagStr));
		artifactTag.setArtifactId(tblArtifactInfo.getId());
		artifactTag.setCreateBy(userId);
		artifactTag.setCreateDate(new Timestamp(new Date().getTime()));
		artifactTag.setLastUpdateBy(userId);
		artifactTag.setLastUpdateDate(new Timestamp(new Date().getTime()));
		artifactTag.setStatus(1);
		artifactTag.setTagUserId(artifactTag.getCreateBy());
		artifactTag.setTagTime(artifactTag.getCreateDate());
		artifactTagMapper.insert(artifactTag);
	}



	/**
	 * 查询包件信息
	 */
	@Override
	public Map<String, Object> findNexusType(NexusSearchVO nexusSearchVO, Long systemId, Long projectId, String tagStr,
			Integer rows, Integer page) throws Exception {
		Map<String, Object> jqGridPage = new HashMap<>();
		Integer startRow = rows * (page - 1); // 开始记录
		Integer endRow = rows * page; // 结束记录
		List<NexusAssetBO> listBo = null;
		List<Map<String, Object>> packageList = new ArrayList<>();
		TblSystemInfo systemInfo = new TblSystemInfo();
		try {
			List<TblToolInfo> list = tblToolInfoMapper.findNexusType();
			NexusUtil NexusUtil = new NexusUtil(list.get(0));
			listBo = NexusUtil.searchAssetList(nexusSearchVO);
			Integer pages = (int) Math.ceil((double) listBo.size() / rows); // 总页数
			if(listBo.size()>0 && listBo.get(0).getCreateTime()==null){
				Collections.sort(listBo, new Comparator<NexusAssetBO>() {

					@Override
					public int compare(NexusAssetBO o1, NexusAssetBO o2) {
						return o2.getVersion().compareToIgnoreCase(o1.getVersion());
					}
				});
			}else {

				Collections.sort(listBo, new Comparator<NexusAssetBO>() {

					@Override
					public int compare(NexusAssetBO o1, NexusAssetBO o2) {
						return o2.getCreateTime().compareTo(o1.getCreateTime());
					}
				});
			}


			/*
			 * List<TblSystemInfo> parameter= new ArrayList<TblSystemInfo>(listBo.size());
			 */
			for (int i = startRow; i < endRow && i < listBo.size(); i++) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				systemInfo.setArtifactId(listBo.get(i).getArtifactId());
				systemInfo.setId(systemId);
				systemInfo.setGroupId((listBo.get(i).getGroup()));
				//systemInfo.setProjectId(projectId);
				systemInfo.setSystemType(tagStr.length() == 0 ? null : tagStr);
				paramMap.put("system", systemInfo);
				paramMap.put("projectId", projectId);
				/*
				 * String KeyID =listBo.get(i).getArtifactId()+""+listBo.get(i).getGroup(); Set
				 * keys = result.keySet(); Iterator it = keys.iterator(); while(it.hasNext()){
				 * String key = (String)it.next(); if(!key.equals(KeyID)) {
				 * 
				 * systemInfo = tblSystemInfoMapper.FindSystemPackage(systemInfo);
				 * result.put(KeyID,systemInfo.getId()); } }
				 */
				List<Map<String, Object>> systemPackage = tblSystemInfoMapper.FindSystemPackage(paramMap);
				Map<String, Object> map = new HashMap<>();
				if (systemPackage.isEmpty()) {
					map.put("id", "");
					map.put("systemCode", "");
					map.put("systemName", "");
					map.put("moduleId", "");
					map.put("moduleName", "");
					map.put("artifactInfoId", "");
					map.put("environmentType", "");
				} else {
					map.putAll(systemPackage.get(0)); // 将获取的map所有值放入新的map中
					for (Map<String, Object> map2 : systemPackage) {
						if (!map2.containsValue(listBo.get(i).getVersion())) {
							map.put("environmentType", "");
							map.put("artifactInfoId", "");
						} else {
							String envType = "";
							String envName = "";
							if(map2.get("environmentType") != null) {
								envType = map2.get("environmentType").toString();
								String[] envTypeArr = envType.split(",");
								Map<String,String> envMap = JsonUtil.fromJson((String) redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE"), Map.class);
								for (String envStr : envTypeArr) {
									envName +=  StringUtils.isEmpty(envMap.get(envStr))?"":(envMap.get(envStr) + ",");
								}
							}
							map.put("environmentType", envType);
							map.put("environmentName", StringUtils.isEmpty(envName)?envName : envName.substring(0,envName.length()-1));
							map.put("artifactInfoId", map2.get("artifactInfoId"));
							break;
						}
					}

				}
				map.put("repository", listBo.get(i).getRepository());
				map.put("group", listBo.get(i).getGroup());
				map.put("artifactId", listBo.get(i).getArtifactId());
				map.put("version", listBo.get(i).getVersion());
				map.put("path", listBo.get(i).getPath());
				map.put("createTime",
						listBo.get(i).getCreateTime() != null
								? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(listBo.get(i).getCreateTime())
								: "");
				packageList.add(map);
			}
			jqGridPage.put("page", page);
			jqGridPage.put("records", listBo.size());
			jqGridPage.put("total", pages);
			jqGridPage.put("rows", packageList);

		} catch (Exception e) {
			throw e;
		}
		return jqGridPage;
	}

	/**
	 * 获取Artifact信息
	 */
	@Override
	public Map<String, Object> getArtifactInfo(Long id) {
		Map<String, Object> map = new HashMap<>();
		TblArtifactInfo artifactInfo = artifactInfoMapper.selectByPrimaryKey(id);
		map.put("artifactInfo", artifactInfo);
		List<TblRequirementFeature> requirementFeatures = artifactRequirementFeatureMapper.selectByArtifactId(id);
		for (TblRequirementFeature tblRequirementFeature : requirementFeatures) {
			String status = tblRequirementFeature.getRequirementFeatureStatus();
			Map<String, Object> dictMap = JSON.parseObject(
					(String) redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS"), Map.class);
			tblRequirementFeature.setRequirementFeatureStatus((String) dictMap.get(status));
		}
		map.put("requirementFeatures", requirementFeatures);
		return map;
	}

	/**
	 * 关联任务
	 */
	@Override
	@Transactional(readOnly = false)
	public void relateFeature(Long requirementFeatureId, Long artifactInfoId, HttpServletRequest request) {
		TblArtifactRequirementFeature artifactRequirementFeature = new TblArtifactRequirementFeature();
		artifactRequirementFeature.setRequirementFeatureId(requirementFeatureId);
		artifactRequirementFeature.setArtifactId(artifactInfoId);
		CommonUtil.setBaseValue(artifactRequirementFeature, request);
		artifactRequirementFeatureMapper.insert(artifactRequirementFeature);
	}

	/**
	 * 去除关联
	 */
	@Override
	@Transactional(readOnly = false)
	public void removeFeature(Long id, HttpServletRequest request) {
		TblArtifactRequirementFeature artifactRequirementFeature = new TblArtifactRequirementFeature();
		artifactRequirementFeature.setId(id);
		artifactRequirementFeature.setStatus(2);
		artifactRequirementFeature.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
		artifactRequirementFeature.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		artifactRequirementFeatureMapper.updateByPrimaryKeySelective(artifactRequirementFeature);
	}

	/**
	 * 批量去除关联
	 */
	@Override
	@Transactional(readOnly = false)
	public void removeManyFeature(String ids, HttpServletRequest request) {
		if (StringUtils.isNotBlank(ids)) {
			List<Integer> idList = JSON.parseArray(ids, Integer.class);
			for (Integer id : idList) {
				removeFeature((long) id, request);
			}
		}
	}

	/**
	 * 根据ArtifactInfoId删除tag
	 */
	@Override
	@Transactional(readOnly = false)
	public void deleteTagByArtifactInfoId(Long artifactInfoId) {
		artifactTagMapper.deleteByArtifactId(artifactInfoId);
	}

	/**
	 * 查找最新的jar包
	 */
	@Override
	public List<TblArtifactInfo> findNewPackage(Long systemId, Long systemModuleId, Integer env) {
		List<TblArtifactInfo> artifactInfoList = artifactInfoMapper.findNewPackage(systemId, systemModuleId, env);
		if (artifactInfoList != null) {
			for (TblArtifactInfo tblArtifactInfo : artifactInfoList) {
				//if (tblArtifactInfo.getRepository().equals("maven-snapshots")) {
//					tblArtifactInfo.setVersion(tblArtifactInfo.getVersion().substring(0, 5));
					tblArtifactInfo.setVersion(tblArtifactInfo.getVersion());
					//获取此jar包所打的标签
					detailTagEnv(tblArtifactInfo);

				//}
			}
		}
		return artifactInfoList;
	}

	private void  detailTagEnv(TblArtifactInfo tblArtifactInfo){
		//获取此jar包所打的标签
		List<TblArtifactTag> tags= artifactTagMapper.selectTagByArtifactId(tblArtifactInfo.getId());
		StringBuffer buffer=new StringBuffer();
		for(TblArtifactTag tblArtifactTag:tags){
			Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
			Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
			String envName = envMap.get(tblArtifactTag.getEnvironmentType() + "").toString();
			buffer.append(envName+",");

		}
		tblArtifactInfo.setTags(buffer.deleteCharAt(buffer.length() - 1).toString());
	}


	/**
	 * 查找jar包d对应的开发任务
	 */
	@Override
	public List<String> findRequidsByartId(String artifactids) {
		Map<String, Object> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		String[] ids = artifactids.split(",");
		for (String s : ids) {
			list.add(s);
		}
		map.put("artIds", list);
		List<String> aList = artifactInfoMapper.findRequidsByartId(map);
		return aList;
	}
	@Override
	public void downPackage(String nexusPath, String repositoryName,HttpServletRequest request,HttpServletResponse response) throws Exception {
		// 获取nexustool类
		 try {
		TblToolInfo nexusToolInfo = new TblToolInfo();
		Map<String, Object> param = new HashMap<>();
		param.put("status", 1);
		param.put("TOOL_TYPE", 6);
		List<TblToolInfo> toolList =tblToolInfoMapper.selectByMap(param);
		if (toolList != null && toolList.size() > 0) {
			nexusToolInfo=toolList.get(0);
		}	  
		NexusUtil nexusUtil=new NexusUtil(nexusToolInfo);
		nexusUtil.downloadPackage(request,response,nexusToolInfo,repositoryName, nexusPath );
	   } catch (URISyntaxException e) {
		
		e.printStackTrace();
	}
		
	}

	@Override
	public Map<String, Object> getEnvType(Long id) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<>();
		String types = systemInfoMapper.getEnvTypeById(id);
		if(types!=null) {
			String[] strings = types.split(",");
			for(String str : strings) {
				String s = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE").toString();
				JSONObject jsonObject = JSONObject.parseObject(s);
				Object object = jsonObject.get(str);
				map.put(str, object);
			}
		}
		
		return map;
	}
}
