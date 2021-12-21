package cn.pioneeruniverse.system.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.system.entity.TblDeptInfo;
import cn.pioneeruniverse.system.service.dept.IDeptService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("dept")
public class DeptController {

	private final static Logger logger = LoggerFactory.getLogger(DeptController.class);
	
	@Autowired
	private IDeptService iDeptService;
	
	
	/*@RequestMapping(value="getAllDept",method=RequestMethod.POST)
	public Map<String,Object> getAllDept(@RequestBody TblDeptInfo dept) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			List<TblDeptInfo> depts = iDeptService.getAllDept(dept);
			result.put("data", JSONObject.toJSONString(depts));
		}catch(Exception e){
			this.handleException(e, "获取所有部门失败");
		}
		return result;
	}*/

	
	@RequestMapping(value="selectDeptById",method=RequestMethod.POST)
	public Map<String,Object> selectDeptById(Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			TblDeptInfo dept = iDeptService.selectDeptById(id);
			result.put("data", JSONObject.toJSONString(dept));
		}catch(Exception e){
			this.handleException(e, "获取部门详情失败");
		}
		return result;
	}

	
	@RequestMapping(value="selectDeptByParentId",method=RequestMethod.POST)
	public Map<String,Object> selectDeptByParentId(Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			List<TblDeptInfo> depts = iDeptService.selectDeptByParentId(id);
			result.put("data", JSONObject.toJSONString(depts));
		}catch(Exception e){
			this.handleException(e, "获取子部门失败");
		}
		return result;
	}

	@RequestMapping(value="insertDept",method=RequestMethod.POST)
	public Map<String,Object> insertDept(@RequestBody TblDeptInfo dept) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			iDeptService.insertDept(dept);
		}catch(Exception e){
			this.handleException(e, "新增部门失败");
		}
		return result;
	}

	@RequestMapping(value="updateDept",method=RequestMethod.POST)
	public Map<String,Object> updateDept(@RequestBody TblDeptInfo dept) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			iDeptService.updateDept(dept);
		}catch(Exception e){
			this.handleException(e, "更新部门失败");
		}
		return result;
	}
	
	@RequestMapping(value="getDeptByCompanyId",method=RequestMethod.POST)
	public Map<String,Object> getDeptByCompanyId(Long companyId) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			List<TblDeptInfo> depts = iDeptService.selectDeptByCompanyId(companyId);
			result.put("data", JSONObject.toJSONString(depts));
		}catch(Exception e){
			this.handleException(e, "获取部门失败");
		}
		return result;
	}

	/**
	 * Description: 修改部门信息
	 * @param deptData 接收部门信息
	 * @return
	 */
	@RequestMapping(value = "updateDeptData",method = RequestMethod.POST)
	public void updateDeptData(@RequestBody String deptData,HttpServletResponse response){
		Integer status=0;
		logger.info("开始同步部门数据"+deptData);
        try{
        	if (StringUtils.isNotBlank(deptData)){
				List<Map<String,Object>> listMap =iDeptService.getDeptData(deptData);
				for(Map<String,Object> result:listMap) {
					String dept= result.get("deptInfo").toString();
					TblDeptInfo deptInfo= JSON.parseObject(dept,TblDeptInfo.class); 
					status=iDeptService.tmpDeptData(deptInfo);
				}
				if(status>0) {
					Map<String,Object> head= new HashMap<>();
	         	   	Map<String,Object> map= new HashMap<>();
	                map.put("consumerSeqNo", "itmgr");	
	                map.put("status", 0);
	                map.put("seqNo", "");
	                map.put("providerSeqNo","");
	                map.put("esbCode", "");
	                map.put("esbMessage", "");
	                map.put("appCode", "0");
	                map.put("appMessage", "同步部门信息成功");	
	                head.put("responseHead", map);
												
					PrintWriter writer = response.getWriter();				
					writer.write(new JSONObject(head).toJSONString());
				}								
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("同步部门信息" + ":" + e.getMessage(), e);
		}
	}


	@RequestMapping(value="getAllDeptInfo",method = RequestMethod.POST)
	public List<TblDeptInfoDTO> getAllDeptInfo(){
		return iDeptService.getAllDeptInfo();
	}
	
	public  Map<String,Object> handleException(Exception e,String message){
		e.printStackTrace();
		logger.error(message+":"+e.getMessage(), e);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", Constants.ITMP_RETURN_FAILURE);
		map.put("errorMessage", message);
		return map;
	}
	
	@RequestMapping(value="getDeptByDeptName",method = RequestMethod.POST)
	public Map<String, Object> getDeptByDeptName(TblDeptInfo deptInfo){
		Map<String, Object> map = new HashMap<>();
		try {
			List<TblDeptInfo> deptInfos = iDeptService.getDeptByDeptName(deptInfo);
			map.put("depts", deptInfos);
		} catch (Exception e) {
			return handleException(e, "查询部门数据失败！");
		}
		return map;
				
		
	}

	@RequestMapping(value="selectDeptByDeptName",method = RequestMethod.POST)
	public Map<String, Object> selectDeptByDeptName(String deptName){
		Map<String, Object> map = new HashMap<>();
		try {
			TblDeptInfo deptInfo=new TblDeptInfo();
			deptInfo.setDeptName(deptName);
			List<TblDeptInfo> deptInfos = iDeptService.getDeptByDeptName(deptInfo);
			if(deptInfos!=null && deptInfos.size()>0){
				map.put("deptId", deptInfos.get(0).getId());
			}

		} catch (Exception e) {
			return handleException(e, "查询部门数据失败！");
		}
		return map;


	}
}
