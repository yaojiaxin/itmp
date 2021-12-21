package cn.pioneeruniverse.dev.service.testSet.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.apache.bcel.generic.IINC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thoughtworks.xstream.mapper.Mapper.Null;

import cn.pioneeruniverse.common.bean.MergedRegionResult;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.constants.DicConstants;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblCaseInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblCaseStepMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseStepMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetExcuteRoundUserMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblUserInfoMapper;
import cn.pioneeruniverse.dev.entity.TblCaseInfo;
import cn.pioneeruniverse.dev.entity.TblCaseStep;
import cn.pioneeruniverse.dev.entity.TblDefectInfo;
import cn.pioneeruniverse.dev.entity.TblTestSet;
import cn.pioneeruniverse.dev.entity.TblTestSetCase;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseStep;
import cn.pioneeruniverse.dev.entity.TblTestSetExcuteRoundUser;
import cn.pioneeruniverse.dev.entity.TblUserInfo;
import cn.pioneeruniverse.dev.service.testSet.ITestSetService;

@Service
@Transactional(readOnly = true)
public class TestSetServiceImpl implements ITestSetService {
	@Autowired
	private TblTestSetMapper testSetMapper;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private TblTestSetCaseMapper testSetCaseMapper;
	@Autowired
	private TblCaseInfoMapper caseInfoMapper;
	@Autowired
	private TblTestSetCaseStepMapper testSetCaseStepMapper;
	@Autowired
	private TblCaseStepMapper caseStepMapper;
	@Autowired
	private TblTestSetExcuteRoundUserMapper testSetExcuteRoundUserMapper;
	@Autowired
	private TblSystemInfoMapper systemInfoMapper;
	@Autowired
	private TblUserInfoMapper userInfoMapper;

	/**
	 * 查询测试集(分页)
	 * 
	 */
	@Override
	public Map<String, Object> getTestSet(Integer page, Integer rows, TblTestSet testSet, Integer tableType) {
		Map<String, Object> map = new HashMap<>();
		PageHelper.startPage(page, rows);
		List<Map<String, Object>> testSetList = testSetMapper.selectTestSet(testSet);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(testSetList);
		if (tableType == 2) {
			map.put("total", pageInfo.getTotal());
			map.put("rows", pageInfo.getList());
		} else if(tableType == 1){
			map.put("rows", pageInfo.getList());
			map.put("total", pageInfo.getPages());
			map.put("record", pageInfo.getTotal());
			map.put("page", page);
		} else {
			map.put("rows", testSetList);
		}
		return map;
	}
	
	/**
	 * 根据条件查询所有的测试集
	 */
	@Override
	public Map<String, Object> getAllTestSet(String nameOrNumber,String createBy,String testTaskId) {
		Map<String, Object> map = new HashMap<>();
		List<Long> userIds = JSON.parseArray(createBy, Long.class);
		map.put("rows", testSetMapper.selectTestByCon(nameOrNumber,userIds,testTaskId));
		return map;
	}

	/**
	 * 导入案例
	 */
	@Override
	@Transactional(readOnly = false)
	public Map<String, Object> importTestCase(MultipartFile file, Long testSetId, HttpServletRequest request)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String fileName = file.getOriginalFilename();
			if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
				map.put("status", Constants.ITMP_RETURN_FAILURE);
				map.put("errorMessage", "上传文件格式出错");
				return map;
			}
			boolean isExcel2003 = true;
			if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
				isExcel2003 = false;
			}
			InputStream is = file.getInputStream();
			Workbook wb = null;
			if (isExcel2003) {
				wb = new HSSFWorkbook(is);
			} else {
				wb = new XSSFWorkbook(is);
			}
			Sheet sheet = wb.getSheetAt(0);
			if (sheet == null) {
				map.put("status", Constants.ITMP_RETURN_FAILURE);
				map.put("errorMessage", "标题未对应");
				return map;
			}
			Row titleRow = sheet.getRow(0);
			String[] title = { "案例名称", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
					"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };//标题验证

			for (int i = 0; i < title.length; i++) {
				if (!StringUtils.contains(titleRow.getCell(i).getStringCellValue(), title[i])) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "标题未对应");
					return map;
				}
			}
			int lastRowNum = sheet.getLastRowNum();
			List<TblCaseStep> caseSteps = new ArrayList<>();
			List<TblTestSetCaseStep> testSetCaseSteps = new ArrayList<>();
			for (int i = 1; i <= lastRowNum; i++) {   //循环表格行
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				for (int j = 0; j < 9; j++) {
					if(row.getCell(j) != null) {
						row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					}
				}
				TblTestSetCase testSetCase = new TblTestSetCase();
				TblCaseInfo caseInfo = new TblCaseInfo();
				int startRow = i;
				int endRow = i;
				if(row.getCell(0) == null || row.getCell(0).getStringCellValue().length() == 0) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"行案例名称为空");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return map;
				}
				if(row.getCell(1) == null  || row.getCell(1).getStringCellValue().length() == 0) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"行案例类型为空");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return map;
				}
				if(row.getCell(2) == null  || row.getCell(2).getStringCellValue().length() == 0) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"行所属系统为空");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return map;
				}
				String caseName = row.getCell(0).getStringCellValue();
				String caseType = row.getCell(1).getStringCellValue();
				String systemName = row.getCell(2).getStringCellValue();
				String casePrecondition = "";
				if(row.getCell(3) != null) {
					casePrecondition = row.getCell(3).getStringCellValue();
				}
				String inputData = row.getCell(4)==null?
						null:row.getCell(4).getStringCellValue();
				String testPoint = row.getCell(5)==null?
						null:row.getCell(5).getStringCellValue();
				String moduleName = row.getCell(6)==null?
						null:row.getCell(6).getStringCellValue();
				String businessType = row.getCell(7)==null?
						null:row.getCell(7).getStringCellValue();
				String expectResult = row.getCell(8)==null?
						null:row.getCell(8).getStringCellValue();

				MergedRegionResult result = ExcelUtil.isMergedRegion(sheet, i, 0);
				if (result.isMerged()) { // 如果第一个单元格是合并的
					startRow = result.getStartRow();
					endRow = result.getEndRow();
					caseName = result.getValue();
					caseType = ExcelUtil.isMergedRegion(sheet, i, 1).getValue();
					systemName = ExcelUtil.isMergedRegion(sheet, i, 2).getValue();
					casePrecondition = ExcelUtil.isMergedRegion(sheet, i, 3).getValue();
					inputData = ExcelUtil.isMergedRegion(sheet, i, 4).getValue();
					testPoint = ExcelUtil.isMergedRegion(sheet, i, 5).getValue();
					moduleName = ExcelUtil.isMergedRegion(sheet, i, 6).getValue();
					businessType = ExcelUtil.isMergedRegion(sheet, i, 7).getValue();
					expectResult = ExcelUtil.isMergedRegion(sheet, i, 8).getValue();
				}
				List<Long> systemIdList = systemInfoMapper.getSystemIdBySystemName2(systemName); // 查询系统
				if (CollectionUtil.isEmpty(systemIdList)) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "无法找到对应案例系统");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return map;
				}
				Long systemId = systemIdList.get(0);
				if(!caseType.equals("正面") && !caseType.equals("反面")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "案例类型只能为正面或反面");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return map;
				}
				//测试案例
				String caseNumber = getCaseNumber();
				caseInfo.setCaseNumber(caseNumber);
				caseInfo.setCaseName(caseName);
				caseInfo.setCaseType(caseType.equals("正面")?1:2);
				caseInfo.setSystemId(Integer.valueOf(systemId.toString()));
				caseInfo.setCasePrecondition(casePrecondition);
				caseInfo.setInputData(inputData);
				caseInfo.setTestPoint(testPoint);
				caseInfo.setModuleName(moduleName);
				caseInfo.setBusinessType(businessType);
				caseInfo.setExpectResult(expectResult);
				caseInfo.setArchiveStatus(1);
				//测试集案例
				testSetCase.setCaseNumber(caseNumber);
				testSetCase.setCaseName(caseName);
				testSetCase.setCaseType(caseType.equals("正面")?1:2);
				testSetCase.setSystemId(systemId);
				testSetCase.setCasePrecondition(casePrecondition);
				testSetCase.setInputData(inputData);
				testSetCase.setTestPoint(testPoint);
				testSetCase.setModuleName(moduleName);
				testSetCase.setBusinessType(businessType);
				testSetCase.setExpectResult(expectResult);
				testSetCase.setCaseExecuteResult(1);
				testSetCase.setTestSetId(testSetId);
				CommonUtil.setBaseValue(testSetCase, request);
				CommonUtil.setBaseValue(caseInfo, request);
				testSetCaseMapper.insert(testSetCase);
				Long testSetCaseId = testSetCase.getId();
				caseInfoMapper.insertCaseInfo(caseInfo);
				Long caseId = caseInfo.getId();

				for (int j = startRow; j <= endRow; j++) {      //案例步骤
					Row stepRow = sheet.getRow(j);
					TblCaseStep caseStep = new TblCaseStep();
					TblTestSetCaseStep testSetCaseStep = new TblTestSetCaseStep();
					testSetCaseStep.setTestSetCaseId(testSetCaseId);
					if(stepRow.getCell(9) != null) {
						stepRow.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
						if(stepRow.getCell(9).getStringCellValue().length() != 0) {
							String stepDescription = "";
							String stepExpectedResult = "";
							if(stepRow.getCell(10) != null) {
								stepRow.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
								stepDescription = stepRow.getCell(10).getStringCellValue();
							}
							if(stepRow.getCell(11) != null) {
								stepRow.getCell(11).setCellType(Cell.CELL_TYPE_STRING);
								stepExpectedResult = stepRow.getCell(11).getStringCellValue();
							}
							stepRow.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
							if(!StringUtils.isNumeric(stepRow.getCell(9).getStringCellValue())) {
								map.put("status", Constants.ITMP_RETURN_FAILURE);
								map.put("errorMessage", "第"+(j+1)+"行步骤序号为非数字");
								TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
								return map;
							}
							caseStep.setStepOrder(Integer.valueOf(stepRow.getCell(9).getStringCellValue()));
							caseStep.setStepDescription(stepDescription);
							caseStep.setStepExpectedResult(stepExpectedResult);
							caseStep.setCaseId(caseId);
							CommonUtil.setBaseValue(caseStep, request);
							
							testSetCaseStep.setStepOrder(Integer.valueOf(stepRow.getCell(9).getStringCellValue()));
							testSetCaseStep.setStepDescription(stepDescription);
							testSetCaseStep.setStepExpectedResult(stepExpectedResult);
							CommonUtil.setBaseValue(testSetCaseStep, request);
							caseSteps.add(caseStep);
							testSetCaseSteps.add(testSetCaseStep);
						}else if(stepRow.getCell(10) != null || stepRow.getCell(11) != null) {
							map.put("status", Constants.ITMP_RETURN_FAILURE);
							map.put("errorMessage", "第"+(j+1)+"行步骤序号为空");
							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
							return map;
						}
					}else if(stepRow.getCell(10) != null || stepRow.getCell(11) != null) {
						map.put("status", Constants.ITMP_RETURN_FAILURE);
						map.put("errorMessage", "第"+(j+1)+"行步骤序号为空");
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return map;
					}
				}
				i = endRow;
			}
			caseStepMapper.batchInsert(caseSteps);
			testSetCaseStepMapper.batchInsert(testSetCaseSteps);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			return map;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 导出案例模板
	 */
	@Override
	public void exportTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String[] title = {"案例名称", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
					"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };

			String sheetName = "测试集案例模板";
			String fileName = "测试集案例模板.xlsx";

			Font font = workbook.createFont();
			font.setFontName("微软雅黑"); // 微软雅黑
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 宽度
			font.setItalic(false); // 是否使用斜体
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示

			CellStyle headStyle = workbook.createCellStyle();
			headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
			headStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());// 设置背景色221,235,247
			headStyle.setFont(font);

			CellStyle valueStyle = workbook.createCellStyle();
			valueStyle.setWrapText(true);

			ExcelUtil.export(title, sheetName, fileName, new String[0][0], workbook, 0, headStyle, valueStyle, request,
					response);
		} catch (Exception e) {
			throw e;
		}
	}



	/**
	 * 新增测试集
	 */
	@Override
	@Transactional(readOnly = false)
	public Map<String, Object> insertTestSet(TblTestSet tblTestSet, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		if(tblTestSet.getId() == null) {
			tblTestSet.setTestSetNumber(getTestSetCode());
			CommonUtil.setBaseValue(tblTestSet, request);
			testSetMapper.insert(tblTestSet); // 插入数据
			Long id = tblTestSet.getId();
			map.put("id", id);
		}else {
			tblTestSet.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			tblTestSet.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
			testSetMapper.updateByPrimaryKeySelective(tblTestSet);
		}
		return map;
	}

	/**
	 * 获取编号
	 * 
	 * @return
	 */
	public String getTestSetCode() {
		String testSetCode = "";
		int codeInt = 0;
		Object object = redisUtils.get("TBL_TEST_SET_TEST_SET_CODE");
		if (object != null && !"".equals(object)) {// redis有直接从redis中取
			String code = object.toString();
			if (!StringUtils.isBlank(code)) {
				codeInt = Integer.parseInt(code) + 1;
			}
		} else {// redis中没有查询数据库中最大的任务编号
			int length = Constants.ITMP_TESTSET_SET_CODE.length() + 1;
			String maxCod = testSetMapper.findMaxId();
			if (!StringUtils.isBlank(maxCod)) {
				String cod = maxCod.substring(length);
				codeInt = Integer.parseInt(cod) + 1;
			} else {
				codeInt = 1;
			}

		}
		DecimalFormat mat = new DecimalFormat("00000000");
		String codeString = mat.format(codeInt);

		testSetCode = Constants.ITMP_TESTSET_SET_CODE + codeString;
		redisUtils.set("TBL_TEST_SET_TEST_SET_CODE", codeString);
		return testSetCode;
	}

	/**
	 * 查询测试集案例(bootstrap)
	 */
	@Override
	public Map<String, Object> selectTestSetCaseByPage(Integer page, Integer rows, TblTestSetCase testSetCase) {
		Map<String, Object> map = new HashMap<>();
		PageHelper.startPage(page, rows);
		List<Map<String, Object>> list = testSetCaseMapper.selectByCon(testSetCase);
		Map<String, Object> caseType = JSON.parseObject(redisUtils.get("TBL_TEST_SET_CASE_TYPE").toString(), Map.class);
		Map<String, Object> caseExecuteResult = JSON
				.parseObject(redisUtils.get("TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT").toString(), Map.class);
		for (Map<String, Object> map2 : list) {
			map2.put("caseTypeName", caseType.get(map2.get("caseType").toString()));
			map2.put("caseExecuteResultName", caseType.get(map2.get("caseExecuteResult").toString()));
		}
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
		map.put("rows", pageInfo.getList());
		map.put("total", pageInfo.getPages());
		map.put("page", page);
		map.put("records", pageInfo.getTotal());
		return map;
	}
	
	

	/**
	 * 修改测试集案例
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateCase(TblTestSetCase testSetCase, HttpServletRequest request) {
		testSetCase.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		testSetCase.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
		testSetCaseMapper.updateCase(testSetCase);
	}

	/**
	 * 批量移除测试集案例
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateManyCase(String ids, HttpServletRequest request) {
		if (ids != null && !ids.isEmpty()) {
			List<Long> idList = JSON.parseArray(ids, Long.class);
			TblTestSetCase tblTestSetCase = new TblTestSetCase();
			tblTestSetCase.setStatus(2);
			tblTestSetCase.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			tblTestSetCase.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
			Map<String, Object> map = new HashMap<>();
			map.put("list", idList);
			map.put("testSetCase", tblTestSetCase);
			testSetCaseMapper.updateManyStatus(map);
		}
	}

	/**
	 * 批量添加案例
	 */
	@Override
	@Transactional(readOnly = false)
	public void batchInsertCase(Long testSetId, String caseStr, HttpServletRequest request) {
		if (!caseStr.isEmpty()) {
			List<TblTestSetCase> testSetCaseListBefore = JSON.parseArray(caseStr, TblTestSetCase.class);
			List<TblTestSetCase> testsetcaseListAdd = new ArrayList<>();
			List<Long> caseIdList = new ArrayList<>();
			
			TblTestSetCase tblTestSetCaseParam = new TblTestSetCase();
			tblTestSetCaseParam.setStatus(1);
			tblTestSetCaseParam.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			tblTestSetCaseParam.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
			
			for (TblTestSetCase tblTestSetCase : testSetCaseListBefore) {
				Long caseId = testSetCaseMapper.judgeTestSetCase(testSetId, tblTestSetCase.getCaseNumber());
				tblTestSetCase.setTestSetId(testSetId);
				tblTestSetCase.setCaseExecuteResult(1);
				if(caseId == null) {    //在测试案例表中没有则增加
					CommonUtil.setBaseValue(tblTestSetCase, request);
					testsetcaseListAdd.add(tblTestSetCase);
				}else {    //在测试案例表中有则修改状态
					caseIdList.add(caseId);
				}
			}
			if(CollectionUtil.isNotEmpty(testsetcaseListAdd)) {
				testSetCaseMapper.batchInsert(testsetcaseListAdd);
			}
			if(CollectionUtil.isNotEmpty(caseIdList)) {
				Map<String, Object> map = new HashMap<>();
				map.put("list", caseIdList);
				map.put("testSetCase", tblTestSetCaseParam);
				testSetCaseMapper.updateManyStatus(map);
			}
		}
		

	}
	
	/**
	 * 批量添加案例步骤
	 */
	@Override
	@Transactional(readOnly = false)
	public void batchInsertCaseStep(Long testSetId,String idStr, HttpServletRequest request) {
		if(!idStr.isEmpty()) {
			List<String> caseNumberlist = JSON.parseArray(idStr, String.class);
			List<TblTestSetCaseStep> caseStepList = testSetCaseStepMapper.getCaseStepByCaseNumber(caseNumberlist,testSetId);
			for (TblTestSetCaseStep tblTestSetCaseStep : caseStepList) {
				CommonUtil.setBaseValue(tblTestSetCaseStep, request);
			}
			if(!caseStepList.isEmpty()) {
				testSetCaseStepMapper.batchInsert(caseStepList);
			}
		}
	}

	/**
	 * 修改测试集状态
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateTestSetStatus(Long id, Integer status, HttpServletRequest request) {
		TblTestSet testSet = new TblTestSet();
		testSet.setId(id);
		testSet.setStatus(status);
		testSet.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		testSet.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
		testSetMapper.updateByPrimaryKeySelective(testSet);
	}

	/**
	 * 增加测试集案例
	 */
	@Override
	@Transactional(readOnly = false)
	public void addTestSetCase(TblTestSetCase testSetCase, List<TblTestSetCaseStep> testSetCaseStepList,
			HttpServletRequest request) {
		testSetCase.setCaseExecuteResult(1);
		CommonUtil.setBaseValue(testSetCase, request);
		testSetCaseMapper.insert(testSetCase);
		testSetCaseStepMapper.deleteByCaseId(testSetCase.getId());
		for (TblTestSetCaseStep tblTestSetCaseStep : testSetCaseStepList) {
			tblTestSetCaseStep.setTestSetCaseId(testSetCase.getId());
			CommonUtil.setBaseValue(tblTestSetCaseStep, request);
			testSetCaseStepMapper.insert(tblTestSetCaseStep);
		}
	}

	/**
	 * 新增执行人
	 */
	@Override
	@Transactional(readOnly = false)
	public void addExecuteUser(Long testSetId,Integer executeRound,String userIdStr, HttpServletRequest request) {
		if(!userIdStr.isEmpty()) {
			List<TblTestSetExcuteRoundUser> testSetExcuteRoundUserList = new ArrayList<>();
			List<Long> list = JSON.parseArray(userIdStr, Long.class);
			for (Long id : list) {
				TblTestSetExcuteRoundUser testSetExcuteRoundUser = new TblTestSetExcuteRoundUser();
				testSetExcuteRoundUser.setTestSetId(testSetId);
				testSetExcuteRoundUser.setExcuteRound(executeRound);
				testSetExcuteRoundUser.setExcuteUserId(id);
				CommonUtil.setBaseValue(testSetExcuteRoundUser, request);
				testSetExcuteRoundUserList.add(testSetExcuteRoundUser);
			}
			testSetExcuteRoundUserMapper.batchInsert(testSetExcuteRoundUserList);
		}
	}

	/**
	 * 移除执行人
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateExcuteUserStatus(Long id, HttpServletRequest request) {
		TblTestSetExcuteRoundUser testSetExcuteRoundUser = new TblTestSetExcuteRoundUser();
		testSetExcuteRoundUser.setId(id);
		testSetExcuteRoundUser.setStatus(2);
		testSetExcuteRoundUser.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		testSetExcuteRoundUser.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
		testSetExcuteRoundUserMapper.updateByPrimaryKeySelective(testSetExcuteRoundUser);
	}

	/**
	 * 批量移除执行人
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateBatchStatus(String ids, HttpServletRequest request) {
		List<Long> idList = JSON.parseArray(ids, Long.class);
		testSetExcuteRoundUserMapper.updateBatchStatus(idList);
	}

	/**
	 * 根据轮次获取执行人
	 */
	@Override
	public Map<String, Object> getTestExcuteUser(Long testSetId, Integer excuteRound) {
		Map<String, Object> map = new HashMap<>();
		List<List<Map<String, Object>>> list = new ArrayList<>();
		Map<String, Object> param = new HashMap<>();
		TblTestSet testSet = testSetMapper.selectById(testSetId);
		for (int i = 1; i <= excuteRound; i++) {
			TblTestSetExcuteRoundUser testSetExcuteRoundUser = new TblTestSetExcuteRoundUser();
			testSetExcuteRoundUser.setTestSetId(testSetId);
			testSetExcuteRoundUser.setExcuteRound(i);
			list.add(testSetExcuteRoundUserMapper.selectByCon(testSetExcuteRoundUser));
		}
		map.put("excuteRoundList", list);
		map.put("testSet", testSet);
		return map;
	}

	/**
	 * 根据测试集id查询测试集案例
	 */
	@Override
	public Map<String, Object> getCaseByTestSetId(Integer page,Integer pageSize,Long testSetId,Integer executeRound,String executeResult) {
		Long executeResultCode = null;
		String string = redisUtils.get("TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT").toString();
		JSONObject jsonObject = JSON.parseObject(string);
		for (String key : jsonObject.keySet()) {
			if(jsonObject.get(key).equals(executeResult)) {
				executeResultCode = Long.valueOf(key);
			}
		}
		Map<String, Object> map = new HashMap<>();
		PageHelper.startPage(page, pageSize);
		List<Map<String, Object>> list = testSetCaseMapper.selectCaseTree(testSetId,executeRound,executeResultCode);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
		map.put("total", pageInfo.getTotal());
		map.put("rows", list);
		return map;
	}

	/**
	 * 根据测试任务id查询测试集
	 */
	@Override
	public List<TblTestSet> getTestSetByFeatureId(Long featureId) {
		return testSetMapper.getTestSetByFeatureId(featureId);
	}

	/**
	 * 获取用户列表(除去执行人)
	 */
	@Override
	public Map<String, Object> getUserTable(Integer page,Integer rows,Long testSetId, Integer executeRound,String userName,String companyName,String deptName) {
		Map<String, Object> map = new HashMap<>();
		PageHelper.startPage(page, rows);
		List<Map<String, Object>> testSetList = userInfoMapper.getUserTable(testSetId, executeRound,userName,companyName,deptName);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(testSetList);
		map.put("total", pageInfo.getTotal());
		map.put("rows", pageInfo.getList());
		return map;
	}

	/**
	 * 获取测试执行树(测试任务+测试集)
	 */
	@Override
	public List<Map<String, Object>> getTaskTree(Long userId,String taskName,String testSetName,Integer requirementFeatureStatus) {
		List<Map<String,Object>> taskTree = new ArrayList<>();
		List<Map<String,Object>> testTree = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("taskName", taskName);
		paramMap.put("testSetName", testSetName);
		paramMap.put("requirementFeatureStatus", requirementFeatureStatus);
		paramMap.put("userId", userId);
		taskTree = testSetMapper.getTaskTree(paramMap);
		testTree = testSetMapper.getTestTree(paramMap);
		for (Map<String, Object> map : testTree) {
			String[] strings = map.get("executeRound").toString().split(",");
			List<String> list = Arrays.asList(strings);
			Set<String> set = new HashSet<>();
			set.addAll(list);
			String str = StringUtils.join(set.toArray(), ",");
			map.put("executeRound", str);
		}
		for (Map<String, Object> map : taskTree) {
			List<Map<String, Object>> testSetList = new ArrayList<>();
			for (Map<String, Object> map2 : testTree) {
				if(map.get("featureId").equals(map2.get("featureId"))) {
					if(map2.get("executeRound") != null && map2.get("executeRound").toString().split(",").length > 1) {
						String[] str = map2.get("executeRound").toString().split(",");
						Integer[] rounds = (Integer[])ConvertUtils.convert(str, Integer.class);
						Arrays.sort(rounds);
						String roundStr = Arrays.toString(rounds);
						map2.put("executeRound", roundStr.substring(1, roundStr.length()-1));
					}
					testSetList.add(map2);
					map.put("testSetList", testSetList);
				}
			}
		}
		return taskTree;
	}
	
	//案例编号
    private String getCaseNumber() {
        String featureCode = "";
        Integer codeInt =0;
        Object object = redisUtils.get(Constants.TMP_CASE_INFO_NUMBER);
        if (object != null &&!"".equals( object)) {
            // redis有直接从redis中取
            String code = object.toString();
            if (!StringUtils.isBlank(code)) {
                codeInt =Integer.parseInt(code)+1;
            }
        }else {
            // redis中没有查询数据库中最大的任务编号
            String maxCaseNo = caseInfoMapper.selectMaxCaseNumber();
            codeInt = 1;
            if (StringUtils.isNotBlank(maxCaseNo)) {
                codeInt = Integer.valueOf(maxCaseNo.substring(Constants.TMP_CASE_INFO_NUMBER.length())) + 1;
            }

        }
        featureCode = Constants.TMP_CASE_INFO_NUMBER + String.format("%08d", codeInt);
        redisUtils.set(Constants.TMP_CASE_INFO_NUMBER,String.format("%08d", codeInt) );
        return featureCode;
    }

    /**
     * 获取关联系统
     */
	@Override
	public Map<String, Object> getRelateSystem(Long testSetId) {
		return testSetMapper.getRelateSystem(testSetId);
	}

	@Override
	public List<TblTestSetCaseStep> getTestSetCase(Long caseId) {
		return testSetCaseStepMapper.getAllCaseStepByCaseId(caseId);
	}


}
