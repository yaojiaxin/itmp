package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.entity.ExtendedField;
import cn.pioneeruniverse.dev.service.CustomFieldTemplate.ICustomFieldTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.entity.TblCaseInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.service.testCaseManage.TestCaseManageService;

/*
 * 测试案例管理
 */
@RestController
@RequestMapping("testCase")
public class TestCaseManageController extends BaseController {

	@Autowired
	private TestCaseManageService testCaseManageService;
	@Autowired
	private ICustomFieldTemplateService iCustomFieldTemplateService;
	
	/**
	 * 测试案例管理列表分页展示
	 * @param page
	 * @param rows
	 * @return
	 * wjdz
	 * 2019年1月25日
	 * 下午2:40:43
	 */
	@RequestMapping(value="getCaselist",method=RequestMethod.POST)
	public Map<String, Object> getCaselist(TblCaseInfo tblCaseInfo , String filters, Integer page,Integer rows){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblCaseInfo> list = testCaseManageService.getCaselist(tblCaseInfo, filters, page, rows);
			List<TblCaseInfo> list2 = testCaseManageService.getCaselist(tblCaseInfo, filters ,1,Integer.MAX_VALUE);
			double total = Math.ceil(list2.size()*1.0/rows);
			map.put("records", list2.size());    //查询的总条目数
			map.put("page", page);					//当前页
			map.put("total", total);             //总页数
			map.put("data", list);				//每页数据
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 新建案例的涉及系统下拉显示
	 * @return
	 * wjdz
	 * 2019年1月25日
	 * 下午2:46:14
	 */
	@RequestMapping(value="getAllSystem",method=RequestMethod.POST)
	public Map<String, Object> getAllSystem(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblSystemInfo> list = testCaseManageService.getAllSystem();
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 新增案例
	 * @param tblCaseInfo
	 * @return
	 * wjdz
	 * 2019年1月28日
	 * 上午9:47:31
	 */
	@RequestMapping(value="insertCaseInfo",method=RequestMethod.POST)
	public Map<String, Object> insertCaseInfo(@RequestBody String tblCaseInfo, HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			Gson gson = new Gson();
			TblCaseInfo caseInfo = gson.fromJson(tblCaseInfo, TblCaseInfo.class);
			TblCaseInfo CaseInfo = testCaseManageService.insertCaseInfo(caseInfo, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	/**
	 * 删除案例
	 * @param ids
	 * @param request
	 * @return
	 * wjdz
	 * 2019年1月28日
	 * 下午2:53:17
	 */
	@RequestMapping(value="deleteCaseInfo",method=RequestMethod.POST)
	public Map<String, Object> deleteCaseInfo(@RequestParam("ids[]")List<Long> ids, HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			testCaseManageService.deleteCaseInfo(ids, request);
			testCaseManageService.deleteArchivedCase(ids, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 案例详情和案例编辑数据的回显
	 * @param id
	 * @return
	 * wjdz
	 * 2019年1月30日
	 * 下午5:16:26
	 */
	@RequestMapping(value="selectCaseInfoById",method=RequestMethod.POST)
	public Map<String, Object> selectCaseInfoById(Long id){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblCaseInfo tblCaseInfo = testCaseManageService.selectCaseInfoById(id);
			List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByTestCase(id);
			map.put("data", tblCaseInfo);
			map.put("field", extendedFields);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 导出案例
	 * @param request
	 * @param response
	 * @throws Exception
	 * wjdz
	 * 2019年2月15日
	 * 下午4:39:32
	 */
	@RequestMapping(value="exportExcel")
	public void exportExcel(TblCaseInfo tblCaseInfo,String filters, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String f = null;
		if(filters.equals("undefined")) {
			filters = null;
			f = filters;
		}else {
			f = filters.replaceAll("\\\\", "");
			f = f.substring(1,f.length()-1);
		}
//		Gson gson = new Gson();
//		TblCaseInfo tblCaseInfo = gson.fromJson(excelData, TblCaseInfo.class);
//		String ff = JSON.parseObject(filters, Map.class).toString();
		List<TblCaseInfo> list = testCaseManageService.getCaseAndSteps(tblCaseInfo, f);
		testCaseManageService.exportExcel(list, request, response);
	}
	
	/**
	 * 案例编辑
	 * @param tblCaseInfo
	 * @param request
	 * @return
	 * wjdz
	 * 2019年2月19日
	 * 上午11:26:25
	 */
	@RequestMapping(value="editCaseInfo",method=RequestMethod.POST)
	public Map<String, Object> editCaseInfo(@RequestBody String tblCaseInfo, HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("statsu", Constants.ITMP_RETURN_SUCCESS);
		try {
			Gson gson = new Gson();
			TblCaseInfo caseInfo = gson.fromJson(tblCaseInfo, TblCaseInfo.class);
			testCaseManageService.editCaseInfo(caseInfo, request);
//			testCaseManageService.editArchivedCase(caseInfo, request);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	//接口
	@RequestMapping(value="getCaseInfo",method=RequestMethod.POST)
	public Map<String, Object> getCaseInfo(Long testSetId,String caseCode,String caseName, Integer page, Integer rows){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			map = testCaseManageService.getCaseInfo(testSetId,caseCode,caseName, page, rows);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	//接口
	@RequestMapping(value="getIdByCaseNumber",method=RequestMethod.POST)
	public Map<String, Object> getIdByCaseNumber(String caseNumber){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long id = testCaseManageService.getIdByCaseNumber(caseNumber);
			map.put("data", id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}

	/**
	 * excel导入
	 * @param xlsPath
	 * @param request
	 * @param response
	 * @return
	 * wjdz
	 * 2019年2月21日
	 * 下午3:53:15
	 */
//	@RequestMapping(value="importExcel",method=RequestMethod.POST)
//	public Map<String, Object> importExcel(String xlsPath, HttpServletRequest request, HttpServletResponse response){
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("status", Constants.ITMP_RETURN_SUCCESS);
//		try {
//			List<TblCaseInfo> list = testCaseManageService.importExcel(xlsPath, request, response);
//			testCaseManageService.saveCaseInfo(list);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			logger.error("mes:" + e.getMessage(), e);
//		}
//		return map;
//	}
	
	/**
	 * 导入案例
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value="importExcel",method=RequestMethod.POST)
	public Map<String, Object> importExcel(MultipartFile file,HttpServletRequest request){
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> map = testCaseManageService.importExcel(file, request);
			result.putAll(map);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	//导出模板
	@RequestMapping(value="exportTemplet")
	public Map<String, Object> exportTemplet(HttpServletRequest request, HttpServletResponse response){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			testCaseManageService.exportTemplet(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 归档案例
	 * @param ids
	 * @return
	 * wjdz
	 * 2019年2月22日
	 * 上午10:15:48
	 */
	@RequestMapping(value="archivingCase",method=RequestMethod.POST)
	public Map<String, Object> archivingCase(@RequestParam("ids[]")List<Long> ids, HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			testCaseManageService.archivingCase(ids, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 获取归档案例的id
	 * @return
	 * wjdz
	 * 2019年2月22日
	 * 下午5:44:17
	 */
	@RequestMapping(value="getArchivedCaseIds",method=RequestMethod.POST)
	public Map<String, Object> getArchivedCaseIds(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<Long> ids = testCaseManageService.getArchivedCaseIds();
			map.put("data", ids);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	//接口
	@RequestMapping(value="updateCaseStep",method=RequestMethod.POST)
	public Map<String, Object> updateCaseStep(String testSetCase, String testCaseStep, HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			 Long testUserId=CommonUtil.getCurrentUserId(request);
			testCaseManageService.updateCaseStep(testSetCase, testCaseStep, testUserId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
}



