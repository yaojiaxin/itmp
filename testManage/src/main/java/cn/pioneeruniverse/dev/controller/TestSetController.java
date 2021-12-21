package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.netflix.discovery.converters.Auto;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.entity.TblCaseInfo;
import cn.pioneeruniverse.dev.entity.TblCaseStep;
import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import cn.pioneeruniverse.dev.entity.TblTestSet;
import cn.pioneeruniverse.dev.entity.TblTestSetCase;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStep;
import cn.pioneeruniverse.dev.entity.TblTestSetExcuteRoundUser;
import cn.pioneeruniverse.dev.service.testCaseManage.TestCaseManageService;
import cn.pioneeruniverse.dev.service.testSet.ITestSetService;

@RestController
@RequestMapping("testSet")
public class TestSetController extends BaseController {

	@Autowired
	private ITestSetService testSetService;
	@Autowired
	private TestCaseManageService testCaseManageService;

	/**
	 * 获取所有的测试集
	 * 
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/getAllTestSet")
	public Map<String, Object> getAllTestSet(Integer page, Integer rows, TblTestSet testSet, String filters) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = testSetService.getTestSet(page, rows, testSet, 1);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 获取所有的测试集
	 * 
	 * @param testSet
	 * @param filters
	 * @return
	 */
	@RequestMapping("/getAllTestSet3")
	public Map<String, Object> getAllTestSet3(String nameOrNumber,String createBy,String testTaskId) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = testSetService.getAllTestSet(nameOrNumber,createBy,testTaskId);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 获取所有的测试集(bootstrap)
	 * 
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/getAllTestSet2")
	public Map<String, Object> getAllTestSet2(Integer page, Integer rows, TblTestSet testSet) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = testSetService.getTestSet(page, rows, testSet, 2);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 根据测试任务ID查询测试集
	 * 
	 * @param page
	 * @param rows
	 * @param testSet
	 * @return
	 */
	@RequestMapping("/getAllTestSetByFeatureId")
	public Map<String, Object> getAllTestSetByFeatureId(Long featureId) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<TblTestSet> list = testSetService.getTestSetByFeatureId(featureId);
			map.put("rows", list);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 获取关联的系统
	 * 
	 * @param testSetId
	 * @return
	 */
	@RequestMapping("/getRelateSystem")
	public Map<String, Object> getRelateSystem(Long testSetId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Map<String, Object> system = testSetService.getRelateSystem(testSetId);
			map.put("row", system);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 新增测试集
	 * 
	 * @param testSet
	 * @param request
	 * @return
	 */
	@RequestMapping("/addTestSet")
	public Map<String, Object> addTestSet(TblTestSet testSet, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = testSetService.insertTestSet(testSet, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 获取测试集案例
	 * 
	 * @param page
	 * @param rows
	 * @param testSetCase
	 * @return
	 */
	@RequestMapping("/getTestSetCase")
	public Map<String, Object> getTestSetCase(Integer page, Integer rows, TblTestSetCase testSetCase) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = testSetService.selectTestSetCaseByPage(page, rows, testSetCase);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 根据测试集Id查询测试集案例
	 * 
	 * @param testSetId
	 * @return
	 */
	@RequestMapping("/getTestSetCaseByTestSetId")
	public Map<String, Object> getTestSetCaseByTestSetId(Integer page, Integer pageSize, Long testSetId,
			Integer executeRound, String executeResult) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = testSetService.getCaseByTestSetId(page, pageSize, testSetId, executeRound, executeResult);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 移除关联的测试集案例
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/removeTestSetCase")
	public Map<String, Object> removeTestSetCase(TblTestSetCase testSet, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.updateCase(testSet, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 批量移除关联的测试集案例
	 * 
	 * @param ids
	 * @param request
	 * @return
	 */
	@RequestMapping("/removeManyTestSetCase")
	public Map<String, Object> removeManyTestSetCase(String ids, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.updateManyCase(ids, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 关联测试集案例
	 * 
	 * @param testSetId
	 * @param caseStr
	 * @param request
	 * @return
	 */
	@RequestMapping("relateTestSetCase")
	public Map<String, Object> relateTestSetCase(Long testSetId, String caseStr, String idStr,
			HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.batchInsertCase(testSetId, caseStr, request);
			testSetService.batchInsertCaseStep(testSetId, idStr, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 废弃测试集
	 * 
	 * @param testSetId
	 * @param caseStr
	 * @param request
	 * @return
	 */
	@RequestMapping("removeTestSet")
	public Map<String, Object> removeTestSet(Long id, Integer status, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.updateTestSetStatus(id, status, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 新增测试集案例(也需新增到案例表)
	 * 
	 * @param testSetCase
	 * @param testSetCaseStep
	 * @param request
	 * @return
	 */
	@RequestMapping("addTestCase")
	public Map<String, Object> addTestCase(String testSetCaseStr, String testSetCaseStepStr,
			HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			TblCaseInfo caseInfo = JSON.parseObject(testSetCaseStr, TblCaseInfo.class); // 新增案例
			List<TblCaseStep> list2 = JSON.parseArray(testSetCaseStepStr, TblCaseStep.class);
			caseInfo.setCaseSteps(list2);
			caseInfo = testCaseManageService.insertCaseInfo(caseInfo, request);

			TblTestSetCase testSetCase = JSON.parseObject(testSetCaseStr, TblTestSetCase.class); // 新增测试集案例
			testSetCase.setCaseNumber(caseInfo.getCaseNumber());
			List<TblTestSetCaseStep> list = JSON.parseArray(testSetCaseStepStr, TblTestSetCaseStep.class);
			testSetService.addTestSetCase(testSetCase, list, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 新增执行人
	 * 
	 * @param testSetExcuteRoundUser
	 * @param request
	 * @return
	 */
	@RequestMapping("addExcuteUser")
	public Map<String, Object> addExcuteUser(Long testSetId, Integer executeRound, String userIdStr,
			HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.addExecuteUser(testSetId, executeRound, userIdStr, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 移除执行人
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("removeExcuteUser")
	public Map<String, Object> removeExcuteUser(Long id, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.updateExcuteUserStatus(id, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 移除多个执行人
	 * 
	 * @param idStr
	 * @param request
	 * @return
	 */
	@RequestMapping("removeManyExcuteUser")
	public Map<String, Object> removeManyExcuteUser(String idStr, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.updateBatchStatus(idStr, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 根据执行轮次获取执行人
	 * 
	 * @param testSetId
	 * @param excuteRound
	 * @return
	 */
	@RequestMapping("getExcuteUserByRound")
	public Map<String, Object> getExcuteUserByRound(Long testSetId, Integer excuteRound) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> map = testSetService.getTestExcuteUser(testSetId, excuteRound);
			result.putAll(map);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * 获取用户列表(除去执行人)
	 * 
	 * @param page
	 * @param rows
	 * @param testSetId
	 * @param executeRound
	 * @return
	 */
	@RequestMapping("getUserTable")
	public Map<String, Object> getExcuteUserByRound(Integer pageNumber, Integer pageSize, Long testSetId,
			Integer executeRound, String userName, String companyName, String deptName) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> map = testSetService.getUserTable(pageNumber, pageSize, testSetId, executeRound,
					userName.equals("") ? null : userName, companyName.equals("") ? null : companyName,
					deptName.equals("") ? null : deptName);
			result.putAll(map);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * 导出模板
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("exportTemplet")
	public Map<String, Object> exportTemplet(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		try {
			testSetService.exportTemplet(request, response);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	/**
	 * 导入案例
	 * 
	 * @param testSetId
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping("importCase")
	public Map<String, Object> importCase(Long testSetId, MultipartFile file, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> map = testSetService.importTestCase(file, testSetId, request);
			result.putAll(map);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * 获取测试执行树
	 * 
	 * @param userId
	 * @param taskName
	 * @param testSetName
	 * @param requirementFeatureStatus
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getTaskTree")
	public Map<String, Object> getTaskTree(Long own, String taskName, String testSetName,
			Integer requirementFeatureStatus, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		try {
			Long userId = null;
			if (own == 1) {
				userId = CommonUtil.getCurrentUserId(request);
			}
			
			List<Map<String, Object>> list = testSetService.getTaskTree(userId, taskName.equals("") ? null : taskName,
					testSetName.equals("") ? null : testSetName, requirementFeatureStatus);
			result.put("rows", list);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 查询案例详情
	 * @param caseId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "showCase")
	public Map<String, Object> showCase(Long caseId) {
		Map<String, Object> result = new HashMap<>();
		try {
			List<TblTestSetCaseStep> list = testSetService.getTestSetCase(caseId);
			result.put("rows", list);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}
}
