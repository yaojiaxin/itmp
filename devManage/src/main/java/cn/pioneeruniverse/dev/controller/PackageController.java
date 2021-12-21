package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.nexus.NexusSearchVO;
import cn.pioneeruniverse.dev.service.packages.PackageService;
/**
 * 
 * @author fanwentao
 *
 */
@RestController
@RequestMapping("package")
public class PackageController extends BaseController {
	@Autowired
	private PackageService packageService;

	@RequestMapping(value = "getPackage", method = RequestMethod.POST)
	public Map<String, Object> getPackage(String workTask,Long systemId,Long projectId,String tagStr,Integer rows,Integer page) {
		NexusSearchVO nexusSearchVO = null;
		Map<String, Object> map = null;
		try {
			if (workTask != null &&!workTask.isEmpty()) {
				nexusSearchVO = JSON.parseObject(workTask, NexusSearchVO.class);
				map = packageService.findNexusType(nexusSearchVO,systemId, projectId,tagStr,rows,page);
			}
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 包件标记
	 * @param artifactInfoStr
	 * @param tagStr
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "addFeature", method = RequestMethod.POST)
	public Map<String, Object> addFeature(String artifactInfoStr,String tagStr,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			packageService.addArtifactFrature(artifactInfoStr,tagStr,request);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result=super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取Artifact信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "getArtifactInfo", method = RequestMethod.POST)
	public Map<String, Object> getArtifactInfo(Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = packageService.getArtifactInfo(id);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result=super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 关联任务
	 * @param requirementFeatureId
	 * @param artifactInfoId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "relateFeature", method = RequestMethod.POST)
	public Map<String, Object> relateFeature(Long requirementFeatureId,Long artifactInfoId,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			packageService.relateFeature(requirementFeatureId,artifactInfoId,request);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result=super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 去除关联
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "removeFeature", method = RequestMethod.POST)
	public Map<String, Object> removeFeature(Long id,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			packageService.removeFeature(id,request);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result=super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	/**
	  *   批量去除关联
	 * @param ids
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "removeManyFeature", method = RequestMethod.POST)
	public Map<String, Object> removeManyFeature(String ids,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			packageService.removeManyFeature(ids,request);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result=super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * 获取任务信息
	 * @param tblRequirementFeature
	 * @param pageNumber
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getFeatur", method = RequestMethod.POST)
	public Map<String, Object> getFeatur(TblRequirementFeature tblRequirementFeature, Integer pageNumber,
										 Integer pageSize, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			return packageService.findFrature(tblRequirementFeature, pageNumber, pageSize);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * 取消包件
	 * @param id
	 * @param
	 * @return
	 */
	@RequestMapping(value = "removeTag", method = RequestMethod.POST)
	public Map<String, Object> removeTag(Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			packageService.deleteTagByArtifactInfoId(id);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result=super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 下载制品包
	 * @param nexusPath
	 * @return
	 */
	@RequestMapping(value = "downPackage")
	public void downPackage(String nexusPath,String  repositoryName, HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//获取nexus
			packageService.downPackage(nexusPath,repositoryName,request,response);
//			packageService.addArtifactFrature(artifactInfoStr,tagStr,request);
//			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
//			result=super.handleException(e, e.getMessage());
		}
		
	}
	
	//获取该系统配置的环境
	@PostMapping("getEnvType")
	public Map<String, Object> getEnvType(Long id){
		Map<String,Object> map = new HashMap<>();
		try {
			map = packageService.getEnvType(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	
}
