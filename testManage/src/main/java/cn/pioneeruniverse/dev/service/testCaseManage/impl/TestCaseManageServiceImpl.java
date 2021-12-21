package cn.pioneeruniverse.dev.service.testCaseManage.impl;

import java.io.InputStream;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

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
import cn.pioneeruniverse.dev.entity.TblCaseInfo;
import cn.pioneeruniverse.dev.entity.TblCaseStep;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.service.testCaseManage.TestCaseManageService;
import sun.misc.BASE64Encoder;

@Service
public class TestCaseManageServiceImpl implements TestCaseManageService {
	
	@Autowired
	private TblCaseInfoMapper tblCaseInfoMapper;
	
	@Autowired
	private TblSystemInfoMapper tblSystemInfoMapper;
	
	@Autowired
	private TblUserInfoMapper tblUserInfoMapper;
	
	@Autowired
	private TblCaseStepMapper tblCaseStepMapper;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private TblArchivedCaseMapper tblArchivedCaseMapper;
	
	@Autowired
	private TblArchivedCaseStepMapper tblArchivedCaseStepMapper;
	
	@Autowired
	private TblSystemInfoMapper systemInfoMapper;
	//测试案例列表显示
	@Override
	@Transactional(readOnly = true)
	public List<TblCaseInfo> getCaselist(TblCaseInfo tblCaseInfo, String filters, Integer page, Integer rows) {
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
				if(key.equals("archiveStatus")) {
					if(value.equals("1")) {
						configMap.put("aStatus", 1);
					}
					if(value.equals("2")) {
						configMap.put("aStatus", 2);
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
//		List<Long> uIds = null;
//		if(tblCaseInfo.getUserName() != null) {
//			uIds = tblUserInfoMapper.getUserIdByUserName(tblCaseInfo.getUserName());
//		}
		Integer start = (page-1)*rows;
		HashMap<String, Object> map = new HashMap<>();
		map.put("tblCaseInfo", tblCaseInfo);
//		map.put("uIds", uIds);
		map.put("filter", configMap);
		map.put("start", start);
		map.put("rows", rows);
		List<TblCaseInfo> list = tblCaseInfoMapper.getCaseInfos(map);
		return list;
	}

	//新增案例涉及系统的下拉框
	@Override
	@Transactional(readOnly = true)
	public List<TblSystemInfo> getAllSystem() {
		// TODO Auto-generated method stub
		return tblSystemInfoMapper.getSystems();
	}

	//新增案例
	@Override
	@Transactional(readOnly = false)
	public TblCaseInfo insertCaseInfo(TblCaseInfo tblCaseInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		tblCaseInfo.setStatus(1);  //状态
		tblCaseInfo.setArchiveStatus(1); //归档状态
		tblCaseInfo.setCreateDate(new Timestamp(new Date().getTime()));//创建时间
		tblCaseInfo.setCreateBy(CommonUtil.getCurrentUserId(request));//创建人
		tblCaseInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		tblCaseInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		tblCaseInfo.setCaseNumber(getCaseNumber());  //案例编号
		tblCaseInfoMapper.insertCaseInfo(tblCaseInfo);
		//获得这条插入数据的id
		Long id = tblCaseInfo.getId();
		List<TblCaseStep> steps = tblCaseInfo.getCaseSteps();
		if(steps != null && steps.size() != 0) {
			for (TblCaseStep tblCaseStep : steps) {
				tblCaseStep.setCaseId(id);
				tblCaseStep.setStatus(1);
				tblCaseStep.setCreateBy(CommonUtil.getCurrentUserId(request));
				tblCaseStep.setCreateDate(new Timestamp(new Date().getTime()));
				tblCaseStepMapper.insertCaseStep(tblCaseStep);
			}
		}
		return tblCaseInfo;
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

    //案例删除
	@Override
	@Transactional(readOnly = false)
	public void deleteCaseInfo(List<Long> ids, HttpServletRequest request) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		map.put("currentUser", CommonUtil.getCurrentUserId(request));
		map.put("time", new Timestamp(new Date().getTime()));
		map.put("ids", ids);
		tblCaseInfoMapper.deleteCaseInfo(map);
	}

	//删除案例的同时删除归档案例
	@Override
	@Transactional(readOnly = false)
	public void deleteArchivedCase(List<Long> ids, HttpServletRequest request) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		map.put("currentUser", CommonUtil.getCurrentUserId(request));
		map.put("time", new Timestamp(new Date().getTime()));
		map.put("ids", ids);
		tblArchivedCaseMapper.deleteArchivedCase(map);
	}
	
	//案例详情及案例编辑数据回显
	@Override
	@Transactional(readOnly = true)
	public TblCaseInfo selectCaseInfoById(Long id) {
		// TODO Auto-generated method stub
		TblCaseInfo tblCaseInfo = tblCaseInfoMapper.findCaseInfoById(id);
		Long createById = tblCaseInfo.getCreateBy();
		Long lastUpdateById = tblCaseInfo.getLastUpdateBy();
		if(createById != null) {
			String creatUser = tblUserInfoMapper.getUserNameById(createById);
			tblCaseInfo.setUserName(creatUser);
		}
		if(lastUpdateById != null) {
			String lastUpdateUser = tblUserInfoMapper.getUserNameById(lastUpdateById);
			tblCaseInfo.setLastUpdateUser(lastUpdateUser);
		}
		tblCaseInfo.setCreateTime(tblCaseInfo.getCreateDate());
		tblCaseInfo.setLastUpdateTime(tblCaseInfo.getLastUpdateDate());
		Long systemId = tblCaseInfo.getSystemId().longValue();
		if(systemId != null) {
			String systemName = tblSystemInfoMapper.getSystemNameById(systemId);
			tblCaseInfo.setSystemName(systemName);
		}
		List<TblCaseStep> list = tblCaseStepMapper.findCaseStepByCaseId(id);
		if(list.size() != 0) {
			tblCaseInfo.setCaseSteps(list);
		}
		return tblCaseInfo;
	}
	
	
	/**
	 * 查询要导出的案例和案例步骤
	 */
	@Override
	@Transactional(readOnly = false)
	public List<TblCaseInfo> getCaseAndSteps(TblCaseInfo tblCaseInfo, String filters) {
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
				if(key.equals("archiveStatus")) {
					if(value.equals("未归档")) {
						configMap.put("type", 1);
					}
					if(value.equals("已归档")) {
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
//		List<Long> uIds = null;
//		if(tblCaseInfo.getUserName() != null&&!tblCaseInfo.getUserName().equals("")) {
//			uIds = tblUserInfoMapper.getUserIdByUserName(tblCaseInfo.getUserName());
//		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("tblCaseInfo", tblCaseInfo);
//		map.put("uIds", uIds);
		map.put("filter", configMap);
		map.put("start", 0);
		map.put("rows", Integer.MAX_VALUE);
		List<TblCaseInfo> list = tblCaseInfoMapper.getCaseInfos(map);
		for (TblCaseInfo caseInfo : list) {
			Integer caseType = caseInfo.getCaseType();
			if(caseType != null && Integer.valueOf(caseType) == 1) {
				caseInfo.setType("正面");
			}
			if(caseType != null && Integer.valueOf(caseType) == 2) {
				caseInfo.setType("反面");
			}
			List<TblCaseStep> caseStepList = tblCaseStepMapper.findCaseStepByCaseId(caseInfo.getId());
			caseInfo.setCaseSteps(caseStepList);
		}
		return list;
	}
	

	//导出案例
	@Override
	public void exportExcel(List<TblCaseInfo> list, HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("案例管理");
        String[] title = {"案例编号","案例名称","案例描述","案例类型","所属系统","前置条件","输入数据","测试项",
				"模块","业务类型","预期结果","步骤序号","步骤描述","步骤预期结果","创建人"};

        String filename = "案例管理"+DateUtil.
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
        	List<TblCaseStep> caseStepList = list.get(i).getCaseSteps();
        	if(caseStepList != null && caseStepList.size() != 0) {
	        	size += caseStepList.size();
        	}else {
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
				List<TblCaseStep> caseStepList = list.get(i).getCaseSteps();
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
        TestCaseManageServiceImpl.exportExcel(title,sheet,filename,
			value,workbook,0,headStyle,valueStyle,request,response);
	}
	
	 public static void exportExcel(String []title,HSSFSheet sheet,String filename,String [][]values,
			HSSFWorkbook workbook,Integer headRowNum,HSSFCellStyle headStyle,HSSFCellStyle valueStyle,
			HttpServletRequest request, HttpServletResponse response) throws  Exception{

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
//	@Override
//	@Transactional(readOnly = false)
//	public void editCaseInfo(TblCaseInfo caseInfo, HttpServletRequest request) {
//		// TODO Auto-generated method stub
//		Long id = caseInfo.getId();
//		//根据caseId把之前的所有步骤解绑
//		HashMap<String, Object> map = new HashMap<>();
//		Long userId = CommonUtil.getCurrentUserId(request);
//		Timestamp time = new Timestamp(new Date().getTime());
//		map.put("id", id);
//		map.put("userId", userId);
//		map.put("time", time);
//		tblCaseStepMapper.updateCaseStepByCaseId(map);
//		caseInfo.setLastUpdateBy(userId);
//		caseInfo.setLastUpdateDate(time);
//		tblCaseInfoMapper.updateCaseInfo(caseInfo);
//		List<TblCaseStep> steps = caseInfo.getCaseSteps();
//		if(steps != null && steps.size() > 0) {
//			for (TblCaseStep tblCaseStep : steps) {
//				tblCaseStep.setCaseId(id);
//				tblCaseStep.setStatus(1);
//				tblCaseStep.setCreateBy(userId);
//				tblCaseStep.setCreateDate(time);
//				tblCaseStepMapper.insertCaseStep(tblCaseStep);
//			}
//		}
//	}
	
	//编辑案例
	@Override
	@Transactional(readOnly = false)
	public void editCaseInfo(TblCaseInfo caseInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		Long id = caseInfo.getId();
		Long userId = CommonUtil.getCurrentUserId(request);
		Timestamp time = new Timestamp(new Date().getTime());
		caseInfo.setLastUpdateBy(userId);
		caseInfo.setLastUpdateDate(time);
		tblCaseInfoMapper.updateCaseInfo(caseInfo);
		List<Long> stepIds = tblCaseStepMapper.getStepIdsByCaseId(id);
		List<TblCaseStep> steps = caseInfo.getCaseSteps();
		List<Long> list = new ArrayList<>();
		if(steps != null && steps.size() > 0) {
			for (TblCaseStep tblCaseStep : steps) {
				if(tblCaseStep.getId()==null) {  //新增
					tblCaseStep.setCaseId(id);
					tblCaseStep.setStatus(1);
					tblCaseStep.setCreateBy(userId);
					tblCaseStep.setCreateDate(time);
					tblCaseStepMapper.insertCaseStep(tblCaseStep);
				}else {          //修改
					Long stepId = tblCaseStep.getId();
					tblCaseStep.setLastUpdateBy(userId);
					tblCaseStep.setLastUpdateDate(time);
					tblCaseStepMapper.updateStep(tblCaseStep);
					list.add(stepId);
				}
			}
			List<Long> listAll = new ArrayList<>();
			List<Long> resultList= new ArrayList<>();
			listAll.addAll(stepIds);
			listAll.addAll(list);
			for (int i = 0; i < listAll.size(); i++) {
				if(stepIds.contains(listAll.get(i)) && list.contains(listAll.get(i))){
					continue;
				}else{
					resultList.add(listAll.get(i));
				}
			}
			if(resultList.size() != 0) {
				Map<String, Object> map = new HashMap<>();
				map.put("userId", userId);
				map.put("time", time);
				map.put("resultList", resultList);
				tblCaseStepMapper.deleteCaseSteps(map);
			}
		}
	}
	
	//编辑案例的同时也更新归档案例
//	@Override
//	@Transactional(readOnly = false)
//	public void editArchivedCase(TblCaseInfo caseInfo, HttpServletRequest request) {
//		// TODO Auto-generated method stub
//		Long id = caseInfo.getId();
//		HashMap<String, Object> map = new HashMap<>();
//		Long userId = CommonUtil.getCurrentUserId(request);
//		Timestamp time = new Timestamp(new Date().getTime());
//		map.put("id", id);
//		map.put("userId", userId);
//		map.put("time", time);
//		tblArchivedCaseStepMapper.updateArchivedCaseStepByCseId(map);
//		caseInfo.setLastUpdateBy(userId);
//		caseInfo.setLastUpdateDate(time);
//		tblArchivedCaseMapper.updateArchivedCase(caseInfo);
//		List<TblCaseStep> steps = caseInfo.getCaseSteps();
//		if(steps != null && steps.size() > 0) {
//			for (TblCaseStep tblCaseStep : steps) {
//				tblCaseStep.setCaseId(id);
//				tblCaseStep.setStatus(1);
//				tblCaseStep.setCreateBy(userId);
//				tblCaseStep.setCreateDate(time);
//				tblArchivedCaseStepMapper.insertArchivedCaseStep(tblCaseStep);
//			}
//		}
//		
//	}

	//接口
	@Override
	@Transactional(readOnly = true)
	public HashMap<String, Object> getCaseInfo(Long testSetId,String caseCode,String caseName, Integer page, Integer rows) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		PageHelper.startPage(page, rows);
		List<Map<String, Object>> list = tblCaseInfoMapper.getCaseInfo(testSetId,caseCode,caseName);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
		map.put("total", pageInfo.getTotal());
		map.put("rows", pageInfo.getList());
		return map;
	}
	
	//接口
	@Override
	@Transactional(readOnly = true)
	public Long getIdByCaseNumber(String caseNumber) {
		// TODO Auto-generated method stub
		return tblCaseInfoMapper.getIdByCaseNumber(caseNumber);
	}

	
	//excel导入2
//	@Override
//	@Transactional(readOnly = false)
//	public Map<String, Object> importExcel(MultipartFile file, HttpServletRequest request) throws Exception {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			String fileName = file.getOriginalFilename();
//			if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//				map.put("status", Constants.ITMP_RETURN_FAILURE);
//				map.put("errorMessage", "上传文件格式出错");
//				return map;
//			}
//			boolean isExcel2003 = true;
//			if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
//				isExcel2003 = false;
//			}
//			InputStream is = file.getInputStream();
//			Workbook wb = null;
//			if (isExcel2003) {
//				wb = new HSSFWorkbook(is);
//			} else {
//				wb = new XSSFWorkbook(is);
//			}
//			Sheet sheet = wb.getSheetAt(0);
//			if (sheet == null) {
//				map.put("status", Constants.ITMP_RETURN_FAILURE);
//				map.put("errorMessage", "标题未对应");
//				return map;
//			}
//			Row titleRow = sheet.getRow(0);
//			String[] title = { "案例名称", "案例描述", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
//					"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };//标题验证
//
//			for (int i = 0; i < title.length; i++) {    
//				if (!titleRow.getCell(i).getStringCellValue().equals(title[i])) {
//					map.put("status", Constants.ITMP_RETURN_FAILURE);
//					map.put("errorMessage", "标题未对应");
//					return map;
//				}
//			}
//			int lastRowNum = sheet.getLastRowNum();
//			for (int i = 1; i <= lastRowNum; i++) {   //循环表格行
//				Row row = sheet.getRow(i);
//				if (row == null) {
//					continue;
//				}
//				for (int j = 0; j < 9; j++) {
//					if(row.getCell(j) != null) {
//						row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
//					}
//				}
//				TblCaseInfo caseInfo = new TblCaseInfo();
//				int startRow = i;
//				int endRow = i;
//				if(row.getCell(0) == null || row.getCell(0).getStringCellValue().equals("")) {
//					map.put("status", Constants.ITMP_RETURN_FAILURE);
//					map.put("errorMessage", "第"+(i+1)+"行案例名称为空");
//					return map;
//				}
//				if(row.getCell(1) == null || row.getCell(2).getStringCellValue().equals("")) {
//					map.put("status", Constants.ITMP_RETURN_FAILURE);
//					map.put("errorMessage", "第"+(i+1)+"行案例类型为空");
//					return map;
//				}
//				if(row.getCell(2) == null || row.getCell(3).getStringCellValue().equals("")) {
//					map.put("status", Constants.ITMP_RETURN_FAILURE);
//					map.put("errorMessage", "第"+(i+1)+"行所属系统为空");
//					return map;
//				}
//
//				String caseNumber = getCaseNumber();
//				String caseName = row.getCell(0).getStringCellValue();
//				String caseDescription = row.getCell(1).getStringCellValue();
//				String caseType = row.getCell(2).getStringCellValue();
//				String systemName = row.getCell(3).getStringCellValue();
//				String casePrecondition = row.getCell(4)==null?
//					null:row.getCell(4).getStringCellValue();
//
//				String inputData = row.getCell(5)==null?
//					null:row.getCell(5).getStringCellValue();
//				String testPoint = row.getCell(6)==null?
//					null:row.getCell(6).getStringCellValue();
//				String moduleName = row.getCell(7)==null?
//					null:row.getCell(7).getStringCellValue();
//				String businessType = row.getCell(8)==null?
//					null:row.getCell(8).getStringCellValue();
//				String expectResult = row.getCell(9)==null?
//					null:row.getCell(9).getStringCellValue();
//
//				MergedRegionResult result = ExcelUtil.isMergedRegion(sheet, i, 0);
//				if (result.isMerged()) { // 如果第一个单元格是合并的
//					startRow = result.getStartRow();
//					endRow = result.getEndRow();
//					caseName = result.getValue();
//					caseDescription = ExcelUtil.isMergedRegion(sheet, i, 1).getValue();
//					caseType = ExcelUtil.isMergedRegion(sheet, i, 2).getValue();
//					systemName = ExcelUtil.isMergedRegion(sheet, i, 3).getValue();
//					casePrecondition = ExcelUtil.isMergedRegion(sheet, i, 4).getValue();
//					inputData = ExcelUtil.isMergedRegion(sheet, i, 5).getValue();
//					testPoint = ExcelUtil.isMergedRegion(sheet, i, 6).getValue();
//					moduleName = ExcelUtil.isMergedRegion(sheet, i, 7).getValue();
//					businessType = ExcelUtil.isMergedRegion(sheet, i, 8).getValue();
//					expectResult = ExcelUtil.isMergedRegion(sheet, i, 9).getValue();
//				}
//				List<Long> systemIds = systemInfoMapper.getSystemIdBySystemName2(systemName); // 查询系统
//				if (systemIds == null || systemIds.isEmpty()) {
//					map.put("status", Constants.ITMP_RETURN_FAILURE);
//					map.put("errorMessage", "无法找到对应案例系统");
//					return map;
//				}
//				if(!caseType.equals("正面") && !caseType.equals("反面")) {
//					map.put("status", Constants.ITMP_RETURN_FAILURE);
//					map.put("errorMessage", "案例类型只能为正面或反面");
//					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//					return map;
//				}
//				caseInfo.setCaseNumber(caseNumber);
//				caseInfo.setCaseName(caseName);
//				caseInfo.setCaseDescription(caseDescription);
//				caseInfo.setCaseType(caseType.equals("正面")?1:2);
//				caseInfo.setSystemId(Integer.valueOf(systemIds.get(0).toString()));
//				caseInfo.setCasePrecondition(casePrecondition);
//				caseInfo.setInputData(inputData);
//				caseInfo.setTestPoint(testPoint);
//				caseInfo.setModuleName(moduleName);
//				caseInfo.setBusinessType(businessType);
//				caseInfo.setExpectResult(expectResult);
//				caseInfo.setArchiveStatus(1);
//				caseInfo.setStatus(1);
//				caseInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
//				caseInfo.setCreateDate(new Timestamp(new Date().getTime()));
//				tblCaseInfoMapper.insertCaseInfo(caseInfo);
//				Long id = caseInfo.getId();
//
//				//增加案例步骤
//				for (int j = startRow; j <= endRow; j++) {      //案例步骤
//					Row stepRow = sheet.getRow(j);
//					TblCaseStep caseStep = new TblCaseStep();
//					if(stepRow.getCell(10) != null) {
//						stepRow.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
//						if(!stepRow.getCell(10).getStringCellValue().equals("")) {
//							String stepDescription = "";
//							String stepExpectedResult = "";
//							if(stepRow.getCell(11) != null) {
//								stepRow.getCell(11).setCellType(Cell.CELL_TYPE_STRING);
//								stepDescription = stepRow.getCell(11).getStringCellValue();
//							}
//							if(stepRow.getCell(12) != null) {
//								stepRow.getCell(12).setCellType(Cell.CELL_TYPE_STRING);
//								stepExpectedResult = stepRow.getCell(12).getStringCellValue();
//							}
//							stepRow.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
//							if(!StringUtils.isNumeric(stepRow.getCell(10).getStringCellValue())) {
//								map.put("status", Constants.ITMP_RETURN_FAILURE);
//								map.put("errorMessage", "第"+(j+1)+"行步骤序号为非数字");
//								return map;
//							}
//							caseStep.setStepOrder(Integer.valueOf(stepRow.getCell(10).getStringCellValue()));
//							caseStep.setStepDescription(stepDescription);
//							caseStep.setStepExpectedResult(stepExpectedResult);
//							caseStep.setCaseId(id);
//							CommonUtil.setBaseValue(caseStep, request);
//							tblCaseStepMapper.insertCaseStep(caseStep);     //新增案例步骤
//						}else if(stepRow.getCell(11) != null || stepRow.getCell(12) != null) {
//							map.put("status", Constants.ITMP_RETURN_FAILURE);
//							map.put("errorMessage", "第"+(j+1)+"行步骤序号为空");
//							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//							return map;
//						}
//					}else if(stepRow.getCell(11) != null || stepRow.getCell(12) != null) {
//						map.put("status", Constants.ITMP_RETURN_FAILURE);
//						map.put("errorMessage", "第"+(j+1)+"行步骤序号为空");
//						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//						return map;
//					}
//				}
//				i = endRow;
//			}
//			map.put("status", Constants.ITMP_RETURN_SUCCESS);
//			return map;
//		} catch (Exception e) {
//			throw e;
//		}
//	}
	
	//excel导入2
	@Override
	@Transactional(readOnly = false)
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
//			String[] title = { "案例名称", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
//					"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };//标题验证
			String[] title = { "Test #", "案例名称", "案例描述", "系统","模块","Type","步骤描述","步骤预期结果"};//标题验证
			
			for (int i = 0; i < title.length; i++) {    
				if (!titleRow.getCell(i).getStringCellValue().equals(title[i])) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("errorMessage", "第"+(i+1)+"列标题未对应");
					return map;
				}
			}
			int count = 0;
			Long caseId = null;
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
//				int startRow = i;
//				int endRow = i;
				
				if(row.getCell(5).getStringCellValue().equals("TestCase")) {
					count = 0;
					if(row.getCell(1) == null || row.getCell(1).getStringCellValue().equals("")) {
						map.put("status", Constants.ITMP_RETURN_FAILURE);
						map.put("errorMessage", "第"+(i+1)+"行案例名称为空");
						return map;
					}
					if(row.getCell(3) == null || row.getCell(3).getStringCellValue().equals("")) {
						map.put("status", Constants.ITMP_RETURN_FAILURE);
						map.put("errorMessage", "第"+(i+1)+"行所属系统为空");
						return map;
					}
					String systemName = row.getCell(3).getStringCellValue();
					List<Long> systemIds = systemInfoMapper.getSystemIdBySystemName2(systemName); // 查询系统
					if (systemIds == null || systemIds.isEmpty()) {
						map.put("status", Constants.ITMP_RETURN_FAILURE);
						map.put("errorMessage", "第"+(i+1)+"行系统不存在!");
						return map;
					}
					caseInfo.setCaseNumber(getCaseNumber());
					caseInfo.setCaseName(row.getCell(1).getStringCellValue());
					caseInfo.setCaseDescription(row.getCell(2).getStringCellValue());
					caseInfo.setSystemId(Integer.valueOf(systemIds.get(0).toString()));
					caseInfo.setModuleName(row.getCell(4).getStringCellValue());
					caseInfo.setCaseType(1);
					caseInfo.setArchiveStatus(1);
					caseInfo.setStatus(1);
					caseInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
					caseInfo.setCreateDate(new Timestamp(new Date().getTime()));
					tblCaseInfoMapper.insertCaseInfo(caseInfo);
					caseId = caseInfo.getId();
				}
				if(row.getCell(5).getStringCellValue().equals(">TestStep")) {
					TblCaseStep caseStep = new TblCaseStep();
					count = count + 1;
					caseStep.setCaseId(caseId);
					caseStep.setStepOrder(count);
					caseStep.setStepDescription(row.getCell(6).getStringCellValue());
					caseStep.setStepExpectedResult(row.getCell(7).getStringCellValue());
					caseStep.setCreateBy(CommonUtil.getCurrentUserId(request));
					caseStep.setCreateDate(new Timestamp(new Date().getTime()));
					caseStep.setStatus(1);
					tblCaseStepMapper.insertCaseStep(caseStep);
				}
				
				

			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			return map;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//导出模板
	@Override
	public void exportTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] title = {"案例名称", "案例描述", "案例类型(正面/反面)", "所属系统(已有系统)", "前置条件","输入数据",
				"测试项","模块","业务类型","预期结果","步骤序号", "步骤描述", "步骤预期结果" };
		String sheetName = "案例管理";
		String fileName = "案例模板.xlsx";

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

		ExcelUtil.export(title, sheetName, fileName, new String[0][0], workbook, 0, headStyle,
				valueStyle, request,response);
	}
			

	//归档案例
	@Override
	@Transactional(readOnly = false)
	public void archivingCase(List<Long> ids, HttpServletRequest request) {
		// TODO Auto-generated method stub
		//修改归档状态
		HashMap<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		map.put("userId", CommonUtil.getCurrentUserId(request));
		map.put("time", new Timestamp(new Date().getTime()));
		tblCaseInfoMapper.updateArchiveStatus(map);
		//查询出需要归档的案例和案例步骤
		List<TblCaseInfo> caseList = tblCaseInfoMapper.getCaseByIds(ids);
		List<TblCaseStep> caseStepList = tblCaseStepMapper.getCaseStepsByCaseIds(ids);
		//把查询出来的案例和案例步骤同步到归档案例和归档案例步骤表中
		if(caseList != null && caseList.size() != 0) {
			for (TblCaseInfo tblCaseInfo : caseList) {
				tblArchivedCaseMapper.archivingCase(tblCaseInfo);
			}
		}
		if(caseStepList != null && caseStepList.size() != 0) {
			for (TblCaseStep tblCaseStep : caseStepList) {
				tblArchivedCaseStepMapper.archivingCaseStep(tblCaseStep);
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Long> getArchivedCaseIds() {
		// TODO Auto-generated method stub
		return tblArchivedCaseMapper.getArchivedCaseIds();
		
	}

	//接口
	@Override
	@Transactional(readOnly=false)
	public void updateCaseStep(String testSetCase, String testCaseStep, Long testUserId) throws Exception {
		// TODO Auto-generated method stub
//		Map<String, Object> result=null;
//		result = (Map)JSON.parse(testSetCase); 
//		Gson gson = new Gson();
//		TblTestSetCase tblTestSetCase = gson.fromJson(testSetCase, TblTestSetCase.class);
//		Long id = tblCaseInfoMapper.getIdByCaseNumber(tblTestSetCase.getCaseNumber());
//		
//		List<TblTestSetCaseStep> tblCaseStep = JsonUtil.fromJson(testCaseStep,
//				JsonUtil.createCollectionType(ArrayList.class, TblTestSetCaseStep.class));
		try {
			Map<String, Object> result=null;
			Map<String, Object> map = new HashMap<>();
			result = (Map)JSON.parse(testSetCase); 
			List<Map<String,Object>> listObjectSec = JSONArray.parseObject(testCaseStep,List.class);
		
			result.put("testUserId",testUserId);
			Long id = tblCaseInfoMapper.getIdByCaseNumber(result.get("caseNumber").toString());
			result.put("cid", id);
			tblCaseInfoMapper.updateCase(result);
			tblCaseStepMapper.deleteCaseStep(id);
			map.put("list", listObjectSec);
			map.put("caseId", id);
			map.put("createBy",testUserId);
			tblCaseStepMapper.insertStep(map);
			/*List<Long> beforeIds = CollectionUtil.extractToList(beforeList, "id");
			if(caseSepList.size()!=0) {
				caseSepList=new ArrayList<Map<String,Object>>();
			}*/
			/*List<Long> afterIds = CollectionUtil.extractToList(caseSepList, "id");
			List<Long> deleteIds = (List<Long>) CollectionUtil.getDiffent(beforeIds,afterIds);
			if (deleteIds.size()>0) {
				tblCaseStepMapper.updateStatus(deleteIds);
			}
			for(Map<String, Object> i:caseSepList) {
				if(i.get("id")!=null) {
					i.put("lastUpdateBy",testUserId);
					tblCaseStepMapper.updateCaseStep(i);
				}else {
					i.put("createBy",testUserId);
					i.put("caseId",id);
					i.put("status",1);
					tblCaseStepMapper.insert(i);
				}
			}*/
			}catch (Exception e) {
				throw e;
			}
	}

	

	


	



	

	

	
}
