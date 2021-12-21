package cn.pioneeruniverse.dev.service.testArchivedCase.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;

import cn.pioneeruniverse.common.bean.MergedRegionResult;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblArchivedCaseMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblArchivedCaseStepMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblCaseInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblCaseStepMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblUserInfoMapper;
import cn.pioneeruniverse.dev.entity.TblArchivedCase;
import cn.pioneeruniverse.dev.entity.TblArchivedCaseStep;
import cn.pioneeruniverse.dev.entity.TblCaseInfo;
import cn.pioneeruniverse.dev.entity.TblCaseStep;
import cn.pioneeruniverse.dev.service.testArchivedCase.TestArchivedCaseService;
import cn.pioneeruniverse.dev.service.testCaseManage.impl.TestCaseManageServiceImpl;
import sun.misc.BASE64Encoder;

@Service
public class TestArchivedCaseServiceImpl implements TestArchivedCaseService {
	
	@Autowired
	private TblArchivedCaseMapper tblArchivedCaseMapper;
	
	@Autowired
	private TblArchivedCaseStepMapper tblArchivedCaseStepMapper;
	
	@Autowired
	private TblSystemInfoMapper tblSystemInfoMapper;
	
	@Autowired
	private TblUserInfoMapper tblUserInfoMapper;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private TblCaseInfoMapper tblCaseInfoMapper;
	
	@Autowired
	private TblCaseStepMapper tblCaseStepMapper;

	@Override
	@Transactional(readOnly=true)
	public List<TblArchivedCase> getArchivedCases(TblArchivedCase tblArchivedCase, String filters, Integer page,
			Integer rows) {
		// TODO Auto-generated method stub
		Map<String,Object> configMap = new HashMap<>();
		if(filters != null&&!filters.equals("")) {
			Map filter = JSON.parseObject(filters, Map.class);
			List<Map<String, Object>> ruleList = JSON.parseObject(filter.get("rules").toString(), List.class);
			for (Map<String, Object> map2 : ruleList) {
				String key = map2.get("field").toString();
				Object value = map2.get("data");
				if(key.equals("caseType")) {
					if(value.equals("1")) {
						configMap.put("type", 1);
					}
					if(value.equals("2")) {
						configMap.put("type", 2);
					}
				}
				if(key.equals("systemName")) {
					List<Long> systemIds = tblSystemInfoMapper.getSystemIdBySystemName(value.toString());
					configMap.put("systemIds", systemIds);
				}
				if(key.equals("userName")) {
					List<Long> userIds = tblUserInfoMapper.getUserIdByUserName(value.toString());
					configMap.put("userIds", userIds);
				}
				configMap.put(key, value);
			}
		}
		Integer start = (page-1)*rows;
		HashMap<String, Object> map = new HashMap<>();
		map.put("tblArchivedCase", tblArchivedCase);
		map.put("filter", configMap);
		map.put("start", start);
		map.put("rows", rows);
		List<TblArchivedCase> list = tblArchivedCaseMapper.getCaseInfos(map);
		return list;
	}

	//案例详情
	@Override
	@Transactional(readOnly=true)
	public TblArchivedCase getArchivedCaseById(Long id) {
		// TODO Auto-generated method stub
		TblArchivedCase tblArchivedCase = tblArchivedCaseMapper.getArchivedCaseById(id);
		Long createById = tblArchivedCase.getCreateBy();
		Long lastUpdateById = tblArchivedCase.getLastUpdateBy();
		if(createById != null) {
			String creatUser = tblUserInfoMapper.getUserNameById(createById);
			tblArchivedCase.setUserName(creatUser);
		}
		if(lastUpdateById != null) {
			String lastUpdateUser = tblUserInfoMapper.getUserNameById(lastUpdateById);
			tblArchivedCase.setLastUpdateUser(lastUpdateUser);
		}
		tblArchivedCase.setCreateTime(tblArchivedCase.getCreateDate());
		tblArchivedCase.setLastUpdateTime(tblArchivedCase.getLastUpdateDate());
		Long systemId = tblArchivedCase.getSystemId().longValue();
		if(systemId != null) {
			String systemName = tblSystemInfoMapper.getSystemNameById(systemId);
			tblArchivedCase.setSystemName(systemName);
		}
		List<TblArchivedCaseStep> list = tblArchivedCaseStepMapper.findCaseStepByCaseId(id);
		if(list.size() != 0) {
			tblArchivedCase.setCaseSteps(list);
		}
		return tblArchivedCase;
	}

	//新增测试案例
	@Override
	@Transactional(readOnly=false)
	public void insertTestCase(TblCaseInfo tblCaseInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		tblCaseInfo.setStatus(1);  //状态
		tblCaseInfo.setArchiveStatus(2); //归档状态
		tblCaseInfo.setCreateDate(new Timestamp(new Date().getTime()));//创建时间
		tblCaseInfo.setCreateBy(CommonUtil.getCurrentUserId(request));//创建人
		tblCaseInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		tblCaseInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblCaseInfo.setCaseNumber(getCaseNumber());  //案例编号
		tblCaseInfoMapper.insertCaseInfo(tblCaseInfo);
		//获得这条插入数据的id
		Long id = tblCaseInfo.getId();
		tblCaseInfo.setId(id);
		tblArchivedCaseMapper.archivingCase(tblCaseInfo);
		List<TblCaseStep> steps = tblCaseInfo.getCaseSteps();
		if(steps != null && steps.size() != 0) {
			for (TblCaseStep tblCaseStep : steps) {
				tblCaseStep.setCaseId(id);
				tblCaseStep.setStatus(1);
				tblCaseStep.setCreateBy(CommonUtil.getCurrentUserId(request));
				tblCaseStep.setCreateDate(new Timestamp(new Date().getTime()));
				tblCaseStepMapper.insertCaseStep(tblCaseStep);
				tblCaseStep.setId(tblCaseStep.getId());
				tblArchivedCaseStepMapper.archivingCaseStep(tblCaseStep);
			}
		}
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
            String maxCaseNo = tblCaseInfoMapper.selectMaxCaseNumber();
            codeInt = 1;
            if (StringUtils.isNotBlank(maxCaseNo)) {
                codeInt = Integer.valueOf(maxCaseNo.substring(Constants.TMP_CASE_INFO_NUMBER.length())) + 1;
            }

        }
        featureCode = Constants.TMP_CASE_INFO_NUMBER + String.format("%08d", codeInt);
        redisUtils.set(Constants.TMP_CASE_INFO_NUMBER,String.format("%08d", codeInt) );
        return featureCode;
    }

    //移除归档案例(直接删除归档案例，同时把测试案例的归档状态改为未归档，之所以删除是因为移除之后还可以再归档回来不删除就不能实现这个操作)
	@Override
	@Transactional(readOnly=false)
	public void removeArchivedTest(List<Long> ids, HttpServletRequest request) {
		// TODO Auto-generated method stub
		tblArchivedCaseMapper.deleteArchivedCase(ids);
		tblArchivedCaseStepMapper.deleteArchivedCaseStep(ids);
		HashMap<String, Object> map = new HashMap<>();
		map.put("updateUser", CommonUtil.getCurrentUserId(request));
		map.put("updateTime", new Timestamp(new Date().getTime()));
		map.put("ids", ids);
		tblCaseInfoMapper.updateCaseArchivedStatus(map);
	}

	//下载模板
	@Override
	public void exportTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] title = {"案例名称", "案例描述", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
				"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };
		String sheetName = "归档案例";
		String fileName = "归档案例模板.xlsx";

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

		ExcelUtil.export(title, sheetName, fileName, new String[0][0], workbook, 0,
				headStyle, valueStyle, request,
				response);
	}
	

	//导入归档案例
	@Override
	@Transactional(readOnly=false)
	public Map<String, Object> importExcel(MultipartFile file, HttpServletRequest request) throws Exception {
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
			String[] title = { "案例名称", "案例描述", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
					"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };//标题验证
			for (int i = 0; i < title.length; i++) {    
				if (!titleRow.getCell(i).getStringCellValue().equals(title[i])) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "标题未对应");
					return map;
				}
			}
			int lastRowNum = sheet.getLastRowNum();
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
				TblCaseInfo caseInfo = new TblCaseInfo();
				int startRow = i;
				int endRow = i;
				if(row.getCell(0) == null || row.getCell(0).getStringCellValue().equals("")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"行案例名称为空");
					return map;
				}
				if(row.getCell(1) == null || row.getCell(2).getStringCellValue().equals("")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"行案例类型为空");
					return map;
				}
				if(row.getCell(2) == null || row.getCell(3).getStringCellValue().equals("")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"行所属系统为空");
					return map;
				}
				String caseNumber = getCaseNumber();
				String caseName = row.getCell(0).getStringCellValue();
				String caseDescription = row.getCell(1).getStringCellValue();
				String caseType = row.getCell(2).getStringCellValue();
				String systemName = row.getCell(3).getStringCellValue();
				String casePrecondition = row.getCell(4)==null?
					null:row.getCell(4).getStringCellValue();
				String inputData = row.getCell(5)==null?
					null:row.getCell(5).getStringCellValue();
				String testPoint = row.getCell(6)==null?
					null:row.getCell(6).getStringCellValue();
				String moduleName = row.getCell(7)==null?
					null:row.getCell(7).getStringCellValue();
				String businessType = row.getCell(8)==null?
					null:row.getCell(8).getStringCellValue();
				String expectResult = row.getCell(9)==null?
					null:row.getCell(9).getStringCellValue();

				MergedRegionResult result = ExcelUtil.isMergedRegion(sheet, i, 0);
				if (result.isMerged()) { // 如果第一个单元格是合并的
					startRow = result.getStartRow();
					endRow = result.getEndRow();
					caseName = result.getValue();
					caseDescription = ExcelUtil.isMergedRegion(sheet, i, 1).getValue();
					caseType = ExcelUtil.isMergedRegion(sheet, i, 2).getValue();
					systemName = ExcelUtil.isMergedRegion(sheet, i, 3).getValue();
					casePrecondition = ExcelUtil.isMergedRegion(sheet, i, 4).getValue();
					inputData = ExcelUtil.isMergedRegion(sheet, i, 5).getValue();
					testPoint = ExcelUtil.isMergedRegion(sheet, i, 6).getValue();
					moduleName = ExcelUtil.isMergedRegion(sheet, i, 7).getValue();
					businessType = ExcelUtil.isMergedRegion(sheet, i, 8).getValue();
					expectResult = ExcelUtil.isMergedRegion(sheet, i, 9).getValue();
				}
				List<Long> systemIds = tblSystemInfoMapper.getSystemIdBySystemName2(systemName); // 查询系统
				if (systemIds == null || systemIds.isEmpty()) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "无法找到对应案例系统");
					return map;
				}
				if(!caseType.equals("正面") && !caseType.equals("反面")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "案例类型只能为正面或反面");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return map;
				}
				caseInfo.setCaseNumber(caseNumber);
				caseInfo.setCaseName(caseName);
				caseInfo.setCaseDescription(caseDescription);
				caseInfo.setCaseType(caseType.equals("正面")?1:2);
				caseInfo.setSystemId(Integer.valueOf(systemIds.get(0).toString()));
				caseInfo.setCasePrecondition(casePrecondition);
				caseInfo.setInputData(inputData);
				caseInfo.setTestPoint(testPoint);
				caseInfo.setModuleName(moduleName);
				caseInfo.setBusinessType(businessType);
				caseInfo.setExpectResult(expectResult);
				caseInfo.setArchiveStatus(2);
				caseInfo.setStatus(1);
				caseInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
				caseInfo.setCreateDate(new Timestamp(new Date().getTime()));
				
				tblCaseInfoMapper.insertCaseInfo(caseInfo);
				Long id = caseInfo.getId();
				tblArchivedCaseMapper.archivingCase(caseInfo);

				//增加案例步骤
				for (int j = startRow; j <= endRow; j++) {      //案例步骤
					Row stepRow = sheet.getRow(j);
					TblCaseStep caseStep = new TblCaseStep();
					if(stepRow.getCell(10) != null) {
						stepRow.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
						if(!stepRow.getCell(10).getStringCellValue().equals("")) {
							String stepDescription = "";
							String stepExpectedResult = "";
							if(stepRow.getCell(11) != null) {
								stepRow.getCell(11).setCellType(Cell.CELL_TYPE_STRING);
								stepDescription = stepRow.getCell(11).getStringCellValue();
							}
							if(stepRow.getCell(12) != null) {
								stepRow.getCell(12).setCellType(Cell.CELL_TYPE_STRING);
								stepExpectedResult = stepRow.getCell(12).getStringCellValue();
							}
							stepRow.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
							if(!StringUtils.isNumeric(stepRow.getCell(10).getStringCellValue())) {
								map.put("status", Constants.ITMP_RETURN_FAILURE);
								map.put("errorMessage", "第"+(j+1)+"行步骤序号为非数字");
								return map;
							}
							caseStep.setStepOrder(Integer.valueOf(stepRow.getCell(10).getStringCellValue()));
							caseStep.setStepDescription(stepDescription);
							caseStep.setStepExpectedResult(stepExpectedResult);
							caseStep.setCaseId(id);
							CommonUtil.setBaseValue(caseStep, request);
						
							tblCaseStepMapper.insertCaseStep(caseStep);     //新增案例步骤
							tblArchivedCaseStepMapper.archivingCaseStep(caseStep);
						}else if(stepRow.getCell(11) != null || stepRow.getCell(12) != null) {
							map.put("status", Constants.ITMP_RETURN_FAILURE);
							map.put("errorMessage", "第"+(j+1)+"行步骤序号为空");
							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
							return map;
						}
					}else if(stepRow.getCell(11) != null || stepRow.getCell(12) != null) {
						map.put("status", Constants.ITMP_RETURN_FAILURE);
						map.put("errorMessage", "第"+(j+1)+"行步骤序号为空");
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return map;
					}
				}
				i = endRow;
			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			return map;
		} catch (Exception e) {
			throw e;
		}
	}

	//查询出要导出的归档案例和步骤
	@Override
	@Transactional(readOnly = false)
	public List<TblArchivedCase> getCaseAndSteps(TblArchivedCase tblArchivedCase, String filters) {
		// TODO Auto-generated method stub
		Map<String,Object> configMap = new HashMap<>();
		if(filters != null&&!filters.equals("")) {
			Map filter = JSON.parseObject(filters, Map.class);
			List<Map<String, Object>> ruleList = JSON.parseObject(filter.get("rules").toString(), List.class);
			for (Map<String, Object> map2 : ruleList) {
				String key = map2.get("field").toString();
				Object value = map2.get("data");
				if(key.equals("caseType")) {
					if(value.equals("1")) {
						configMap.put("type", 1);
					}
					if(value.equals("2")) {
						configMap.put("type", 2);
					}
				}
				if(key.equals("systemName")) {
					List<Long> systemIds = tblSystemInfoMapper.getSystemIdBySystemName(value.toString());
					configMap.put("systemIds", systemIds);
				}
				if(key.equals("userName")) {
					List<Long> userIds = tblUserInfoMapper.getUserIdByUserName(value.toString());
					configMap.put("userIds", userIds);
				}
				configMap.put(key, value);
			}
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("tblArchivedCase", tblArchivedCase);
		map.put("filter", configMap);
		map.put("start", 0);
		map.put("rows", Integer.MAX_VALUE);
		List<TblArchivedCase> list = tblArchivedCaseMapper.getCaseInfos(map);
		for (TblArchivedCase archivedCase: list) {
			Integer caseType = archivedCase.getCaseType();
			if(caseType != null && Integer.valueOf(caseType) == 1) {
				archivedCase.setType("正面");
			}
			if(caseType != null && Integer.valueOf(caseType) == 2) {
				archivedCase.setType("反面");
			}
			List<TblArchivedCaseStep> caseStepList = tblArchivedCaseStepMapper.findCaseStepByCaseId(archivedCase.getId());
			archivedCase.setCaseSteps(caseStepList);
		}
		return list;
	}

	//导出案例
	@Override
	public void exportExcel(List<TblArchivedCase> list, HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("归档案例管理");
        String[] title = {"案例编号","案例名称","案例描述","案例类型","所属系统","前置条件","输入数据","测试项",
				"模块","业务类型","预期结果","步骤序号","步骤描述","步骤预期结果","创建人"};
        String filename = "归档案例管理"+DateUtil.
				getDateString(new Timestamp(new Date().getTime()),"yyyyMMddHHmmss")+".xls";
        

        HSSFFont font = workbook.createFont();
        font.setFontName("微软雅黑"); 
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
        font.setItalic(false); 
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); 
        headStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        headStyle.setFont(font);

        HSSFCellStyle valueStyle = workbook.createCellStyle();
        valueStyle.setWrapText(true);
        int size = 0;
        for (int i = 0; i < list.size(); i++) {
        	List<TblArchivedCaseStep> caseStepList = list.get(i).getCaseSteps();
        	if(caseStepList != null && caseStepList.size() != 0) {
	        	size += caseStepList.size();
        	}
        	else {
        		size += 1;
        	}
        }
        
        int startLine = 0;
        int endLine = 0;
        String[][] value = new String[size][];
      	if(list != null && list.size() > 0){
          	for (int i = 0; i < list.size(); i++) {
				value[startLine] = new String[title.length];
				value[startLine][0] = list.get(i).getCaseNumber();
				value[startLine][1] = list.get(i).getCaseName();
				value[startLine][2] = list.get(i).getCaseDescription();
				value[startLine][3] = list.get(i).getType();
				value[startLine][4] = list.get(i).getSystemName();
				value[startLine][5] = list.get(i).getCasePrecondition();

			  	value[startLine][6] = list.get(i).getInputData();
			  	value[startLine][7] = list.get(i).getTestPoint();
			  	value[startLine][8] = list.get(i).getModuleName();
			  	value[startLine][9] = list.get(i).getBusinessType();
			  	value[startLine][10] = list.get(i).getExpectResult();
          		value[startLine][14] = list.get(i).getUserName()+" | "+list.get(i).getCreateDate();
          		List<TblArchivedCaseStep> caseStepList = list.get(i).getCaseSteps();
          		if(caseStepList != null && caseStepList.size() != 0) {
          			for (int j = 0; j < caseStepList.size(); j++) {
          	  			if(j==0){
							value[j+startLine][11] = caseStepList != null && caseStepList.size() > 0 ?
								caseStepList.get(j).getStepOrder().toString():"";
							value[j+startLine][12] = caseStepList != null && caseStepList.size() > 0 ?
								caseStepList.get(j).getStepDescription():"";
							value[j+startLine][13] = caseStepList != null && caseStepList.size() > 0 ?
								caseStepList.get(j).getStepExpectedResult():"";
          	   			}else{
          	   				value[j+startLine] = new String[title.length];
							value[j+startLine][11] = caseStepList != null && caseStepList.size() > 0 ?
								caseStepList.get(j).getStepOrder().toString():"";
							value[j+startLine][12] = caseStepList != null && caseStepList.size() > 0 ?
								caseStepList.get(j).getStepDescription():"";
							value[j+startLine][13] = caseStepList != null && caseStepList.size() > 0 ?
								caseStepList.get(j).getStepExpectedResult():"";
          	   			}
					}
					endLine += caseStepList.size();
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,0,0));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,1,1));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,2,2));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,3,3));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,4,4));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,5,5));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,6,6));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,7,7));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,8,8));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,9,9));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,10,10));
					sheet.addMergedRegion(new CellRangeAddress(startLine+1,endLine,14,14));
					startLine += caseStepList.size();
          		}else {
          			startLine += 1;
          			endLine += 1;
          		}
          	}
      	}
        exportExcel(title,sheet,filename,value,workbook,0,headStyle,valueStyle,request,response);
	}
	
	 public static void exportExcel(String []title,HSSFSheet sheet,String filename,String [][]values,HSSFWorkbook workbook,Integer headRowNum,HSSFCellStyle headStyle,HSSFCellStyle valueStyle, HttpServletRequest request, HttpServletResponse response) throws  Exception{
	        // 创建头部
	        HSSFCell cell = null;

	        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        headStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());// 设置背景色
	        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        headStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        headStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        headStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        headStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

	        HSSFRow row = sheet.createRow(headRowNum);
	        for(int i=0;i<title.length;i++){
	            cell = row.createCell(i);
	            row.setHeightInPoints(25);
	            cell.setCellValue(title[i]);
	            cell.setCellStyle(headStyle);
	            int colWidth = sheet.getColumnWidth(i)*2;
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

	        //创建内容
	        for(int i=0;i<values.length;i++){
	            row = sheet.createRow(i + 1);
	            for(int j=0;j<values[i].length;j++){
	                //将内容按顺序赋给对应的列对象
	                row.createCell(j).setCellValue(values[i][j]);
	                row.getCell(j).setCellStyle(valueStyle);
	            }
	        }

	        //导出 也就是下载功能， 使用输出流
	        String useragent = request.getHeader("User-Agent");
	        if (useragent.contains("Firefox")) {
	            filename = "=?UTF-8?B?" + new BASE64Encoder().encode(filename.getBytes("utf-8")) + "?=";
	        } else {
	            filename = URLEncoder.encode(filename, "utf-8");
	            filename = filename.replace("+", " ");
	        }
	        OutputStream out = response.getOutputStream();

	        //导出  输出流 设置下载头信息 Content-Disposition 设置mime类型
	        response.setContentType("application/vnd.ms-excel");
	        response.setHeader("Content-Disposition", "attachment;filename="+filename);
	        workbook.write(out);
	        out.flush();
	        out.close();
	    }
	 
	  // 自适应宽度(中文支持)
//	    private static void setSizeColumn(HSSFSheet sheet, int size) {
//	        for (int columnNum = 0; columnNum < size; columnNum++) {
//	            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
//	            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
//	                HSSFRow currentRow;
//	                //当前行未被使用过
//	                if (sheet.getRow(rowNum) == null) {
//	                    currentRow = sheet.createRow(rowNum);
//	                } else {
//	                    currentRow = sheet.getRow(rowNum);
//	                }
//	                if (currentRow.getCell(columnNum) != null) {
//	                    HSSFCell currentCell = currentRow.getCell(columnNum);
//	                    if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
//	                        int length = currentCell.getStringCellValue().getBytes().length;
//	                        if (columnWidth < length) {
//	                            columnWidth = length;
//	                        }
//	                    }
//	                }
//	            }
//	            sheet.setColumnWidth(columnNum, columnWidth * 256);
//	        }
//	    }

	//编辑案例
		@Override
		@Transactional(readOnly = false)
		public void editCaseInfo(TblCaseInfo caseInfo, HttpServletRequest request) {
			// TODO Auto-generated method stub
			Long id = caseInfo.getId();
			//根据caseId把之前的所有步骤解绑
			HashMap<String, Object> map = new HashMap<>();
			Long userId = CommonUtil.getCurrentUserId(request);
			Timestamp time = new Timestamp(new Date().getTime());
			map.put("id", id);
			map.put("userId", userId);
			map.put("time", time);
			tblCaseStepMapper.updateCaseStepByCaseId(map);
			caseInfo.setLastUpdateBy(userId);
			caseInfo.setLastUpdateDate(time);
			tblCaseInfoMapper.updateCaseInfo(caseInfo);
			List<TblCaseStep> steps = caseInfo.getCaseSteps();
			if(steps != null && steps.size() > 0) {
				for (TblCaseStep tblCaseStep : steps) {
					tblCaseStep.setCaseId(id);
					tblCaseStep.setStatus(1);
					tblCaseStep.setCreateBy(userId);
					tblCaseStep.setCreateDate(time);
					tblCaseStepMapper.insertCaseStep(tblCaseStep);
				}
			}
		}
		
		//编辑案例的同时也更新归档案例
		@Override
		@Transactional(readOnly = false)
		public void editArchivedCase(TblCaseInfo caseInfo, HttpServletRequest request) {
			// TODO Auto-generated method stub
			Long id = caseInfo.getId();
			HashMap<String, Object> map = new HashMap<>();
			Long userId = CommonUtil.getCurrentUserId(request);
			Timestamp time = new Timestamp(new Date().getTime());
			map.put("id", id);
			map.put("userId", userId);
			map.put("time", time);
			tblArchivedCaseStepMapper.updateArchivedCaseStepByCseId(map);
			caseInfo.setLastUpdateBy(userId);
			caseInfo.setLastUpdateDate(time);
			tblArchivedCaseMapper.updateArchivedCase(caseInfo);
			List<TblCaseStep> steps = caseInfo.getCaseSteps();
			if(steps != null && steps.size() > 0) {
				for (TblCaseStep tblCaseStep : steps) {
					tblCaseStep.setCaseId(id);
					tblCaseStep.setStatus(1);
					tblCaseStep.setCreateBy(userId);
					tblCaseStep.setCreateDate(time);
					tblArchivedCaseStepMapper.insertArchivedCaseStep(tblCaseStep);
				}
			}
			
		}

}
