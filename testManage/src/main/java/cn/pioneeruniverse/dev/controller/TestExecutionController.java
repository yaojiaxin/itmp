package cn.pioneeruniverse.dev.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.entity.CaseStepVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.common.ExportExcel;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseExecute;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStep;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseVo;
import cn.pioneeruniverse.dev.service.testExecute.testExecuteService;
import cn.pioneeruniverse.dev.service.testSet.ITestSetService;
/**
 * Description: 测试执行管理 前台
 * Author:xukai
 * Date: 2019/1/21 下午 4:15
 */
@RestController
@RequestMapping("testExecution")
public class TestExecutionController {
	 @Autowired
	 private testExecuteService testExecuteService;
	 @Autowired
	 private ITestSetService iTestSetService;
	 @Autowired
	 private RedisUtils redisUtils;
	/**
	 * 批量执行查看
	 * @param request
	 * @return
	 */
	@PostMapping(value="getTestCaseExecute")
	public Map<String, Object> getTestExecute(String testSetId,int excuteRound,String caseExecuteResultList,Integer pageNumber, Integer pageSize,HttpServletRequest request) {
		 Map<String, Object> result = new HashMap<String, Object>();
		 result.put("status", Constants.ITMP_RETURN_SUCCESS);
		 try {
			 List<Map<String, Object>> list= testExecuteService.selectByPrimaryKey(testSetId,excuteRound,caseExecuteResultList,pageNumber,pageSize);
			 List<Map<String, Object>> list2= testExecuteService.selectByPrimaryKey(testSetId,excuteRound,caseExecuteResultList,null,null);
			 result.put("rows",list );
	         result.put("total", list2.size());
	         
		} catch (Exception e) {
			 e.printStackTrace();
			 result.put("status", 2);
		}
		
		return result;
	}
	/**
	 * 批量执行
	 * @param allExecuteDate
	 * @param request
	 * @return
	 */
	@PostMapping(value="updateTestCaseExecute")
	public Map<String, Object> uodateTestCaseExecute(String allExecuteDate,HttpServletRequest request) {
		 Map<String, Object> result = new HashMap<String, Object>();
		 result.put("status", Constants.ITMP_RETURN_SUCCESS);
		 try {
			 Long userId=CommonUtil.getCurrentUserId(request);
			 testExecuteService.uodateTestCaseExecute(allExecuteDate,userId);
		} catch (Exception e) {
			 e.printStackTrace();
			 result.put("status", 2);
		}
		
		return result;
	}
	
	@PostMapping(value="getTestCaseRun")
	public List<TblTestSetCaseStep> getTestCaseRun(Long id,HttpServletRequest request) {
		 List<TblTestSetCaseStep> list=new ArrayList<TblTestSetCaseStep>();
		 try {
			 list= testExecuteService.selectTestCaseRun(id);
		} catch (Exception e) {
			 e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 测试案例执行
	 * @param rows
	 * @param type
	 * @return
	 */
	@PostMapping(value="insetSetStepExecute")
	public Map<String, Object> insetSetStepExecute(Long testSetId,int excuteRound,String rows,String enforcementResults,String type,String files,String excuteRemark,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		 result.put("status", Constants.ITMP_RETURN_SUCCESS);
		 try {
			 Long testUserId=CommonUtil.getCurrentUserId(request);
			String testCaseExecuteId =testExecuteService.insertSelective(rows,type,enforcementResults,excuteRemark,excuteRound,testSetId,testUserId,files);
			result.put("testCaseExecuteId", testCaseExecuteId);
		} catch (Exception e) {
			result.put("status", "2");
		}
		
		return result;
	}
	/**
	 * 查看所有执行案例
	 * @return
	 */
	@PostMapping(value="getAllExecuteCase")
	public List<TblTestSetCaseExecute> getAllExecuteCase(String myData) {
		 List<TblTestSetCaseExecute> list=new ArrayList<TblTestSetCaseExecute>();
		 TblTestSetCaseExecute tblTestSetCaseExecute=new TblTestSetCaseExecute();
		 try {
			 //tblTestSetCaseExecute=JSONObject.parseObject(caseNumber,TblTestSetCaseExecute.class);
			 list= testExecuteService.selectTestCaseExecute(myData);
		} catch (Exception e) {
			 e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 查看详情
	 * @param testSetCaseExecuteId
	 * @return
	 */
	@PostMapping(value="getExecuteCaseDetails")
	public Map<String, Object> getExecuteCase(Long testSetCaseExecuteId,Long testSetCaseId) {
		Map<String, Object> result=new HashMap<String,Object>();
		 try {
			 result= testExecuteService.selectTestExecute(testSetCaseExecuteId,testSetCaseId);
		} catch (Exception e) {
			 e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 删除附件
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="delFile",method=RequestMethod.POST)
	public Map delFile(Long id,HttpServletRequest request) {
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			if(id!=null) {
				Long userId=CommonUtil.getCurrentUserId(request);
				testExecuteService.delecteFile(id,userId);
				result.put("status", "1");
			}
		} catch (Exception e) {
			result.put("status", "2");
		}
		
		
		return result;
		
	}
	
	/**
	 * 修改案例查看
	 * @param testSetCaseId
	 * @return
	 */
	@PostMapping(value="getUpdateCase")
	public Map<String, Object> getUpdateCase(Long testSetCaseId) {
		Map<String, Object> result=new HashMap<String,Object>();
		 try {
			result= testExecuteService.selectUpdateCase(testSetCaseId);
		} catch (Exception e) {
			 e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 修改案例
	 * @param testSetCase
	 * @param testCaseStep
	 * @param request
	 * @return
	 */
	@PostMapping(value="UpdateCaseStep")
	public Map<String, Object> UpdateCaseStep(String testSetCase,String testCaseStep,HttpServletRequest request) {
		Map<String, Object> result= new HashMap<String,Object>();
		result.put("status",1);
		 try {
			 Long testUserId=CommonUtil.getCurrentUserId(request);
			 testExecuteService.updateCaseStep(testSetCase, testCaseStep,testUserId);
			// iTestSetService.updateCaseStep(testSetCase, testCaseStep,testUserId);
		} catch (Exception e) {
			result.put("status", 2);
			 e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 导出测试集案例
	 * @param response
	 * @param request
	 */
	@GetMapping(value = "getExcelByExcuteRound")
	public void getExcelByExcuteRound(Long testSetId,int excuteRound,HttpServletRequest request, HttpServletResponse response){
		List<TblTestSetCaseVo> list = new ArrayList<TblTestSetCaseVo>();
		try {
			SimpleDateFormat sformat=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
			long now=System.currentTimeMillis();
			String day=sformat.format(now);
			String fileName = "测试案例信息"+day+".xls";
			list=testExecuteService.findExeTestSetByexcuteRound(testSetId,excuteRound);

			//new ExportExcel("", TblTestSetCaseVo.class).setDataList(list).write(response, fileName).dispose();
			new ExportExcel().setWorkHead(TblTestSetCaseVo.class,CaseStepVo.class,"测试执行表",10,
			12,"案例步骤").setDataListWithList(list).write(response, fileName).dispose();
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("mes:" + e.getMessage(), e);
		}
	}
	
	@GetMapping(value = "getExcelAllTestSet")
	public void getExcelAllTestSet(Long testSetId,int excuteRound[],HttpServletRequest request, HttpServletResponse response){
		List<TblTestSetCaseVo> list = new ArrayList<TblTestSetCaseVo>();
		
		try {
			SimpleDateFormat sformat=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
			long now=System.currentTimeMillis();
			String day=sformat.format(now);
			String fileName = "测试案例信息"+day+".xls";
			list=testExecuteService.findExeTestSet(testSetId,excuteRound);
			//new ExportExcel("", TblTestSetCaseVo.class).setDataList(list).write(response, fileName).dispose();
			new ExportExcel().setWorkHead(TblTestSetCaseVo.class,CaseStepVo.class,"测试执行表",10,
			12,"案例步骤").setDataListWithList(list).write(response, fileName).dispose();
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("mes:" + e.getMessage(), e);
		}
	}
	
	@PostMapping(value="seeTestCaseExecute")
	public Map<String, Object> seeTestCaseExecute(Long testSetCaseExecuteId) {
		Map<String, Object> result=new HashMap<String,Object>();
		boolean testSize=false;
		result.put("status",1);
		 try {
			 List<TblTestSetCaseExecute> list= testExecuteService.selectByTestSet(testSetCaseExecuteId);
			 if(list.size()>0) {
				 testSize=true;
			 }
			 result.put("size",testSize); 
		} catch (Exception e) {
			result.put("status",2);
			 e.printStackTrace();
		}
		
		return result;
	}
	

	/**
	 * 获取案例状态
	 * @param testSetId
	 * @param caseNumber
	 * @return
	 */
	@RequestMapping(value="getCaseExecuteResult",method=RequestMethod.POST)
	public Map<String, Object> getCaseExecuteResult(Long testSetId, String caseNumber){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long result = testExecuteService.getCaseExecuteResult(testSetId, caseNumber);
			String str = redisUtils.get("TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT").toString();
			JSONObject jsonObject = JSON.parseObject(str);
			String executeResult = jsonObject.get((result).toString()).toString();
			map.put("executeResult", executeResult);
			
		} catch (Exception e) {
			map.put("status",2);
			e.printStackTrace();
		}
		return map;
	}
    
	/**
	 * 获取撤销和挂起的执行记录
	 * @param testSetId
	 * @param caseNumber
	 * @return
	 */
	@RequestMapping(value="getCaseExecute",method=RequestMethod.POST)
	public Map<String, Object> getCaseExecute(Long testSetId, String caseNumber){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblTestSetCaseExecute> list = testExecuteService.getCaseExecute(testSetId, caseNumber);
			map.put("data", list);
		} catch (Exception e) {
			map.put("status",2);
			e.printStackTrace();
		}
		return map;
	}
}
