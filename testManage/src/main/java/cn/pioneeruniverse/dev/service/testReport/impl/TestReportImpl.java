package cn.pioneeruniverse.dev.service.testReport.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.fabric.xmlrpc.base.Array;
import com.thoughtworks.xstream.mapper.Mapper.Null;

import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblDefectInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import cn.pioneeruniverse.dev.service.testReport.ITestReport;
import cn.pioneeruniverse.dev.vo.TestReportParamVo;

@Transactional(readOnly = true)
@Service
public class TestReportImpl implements ITestReport {

	@Autowired
	private TblRequirementFeatureMapper requirementFeatureMapper;
	@Autowired
	private TblDefectInfoMapper defectInfoMapper;
	@Autowired
	private RedisUtils redisUtils;

	
	public void export1(TestReportParamVo testReportParamVo,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Workbook workbook = new XSSFWorkbook();
		String fileName = "????????????????????????????????????????????????.xlsx";
		String startDate = testReportParamVo.getStartDate();
		String endDate = testReportParamVo.getEndDate();
		String startYearDate = endDate.split("-")[0] + "-01-01";
		String time = startYearDate + "~" + endDate;
		Long currentUserId = CommonUtil.getCurrentUserId(request);
		Object devVertionObject = redisUtils.get("tmp."+currentUserId+".devVertion");
		Object defectProObject = redisUtils.get("tmp."+currentUserId+".defectPro");
		Object defectMonthObject = redisUtils.get("tmp."+currentUserId+".defectMonth");
		Object defectYearObject = redisUtils.get("tmp."+currentUserId+".defectYear");
		Object defectProjectMonthObject = redisUtils.get("tmp."+currentUserId+".defectProjectMonth");
		Object defectProjectYearObject = redisUtils.get("tmp."+currentUserId+".defectProjectYear");
		Object defectLevelObject = redisUtils.get("tmp."+currentUserId+".defectLevel");
		Object worseProjectObject = redisUtils.get("tmp."+currentUserId+".worseProject");
		
		List<Map> devVertionList = new ArrayList<>();
		List<Map> defectProList = new ArrayList<>();
		List<Map> defectMonthList = new ArrayList<>();
		List<Map> defectYearList = new ArrayList<>();
		List<Map> defectProjectMonthList = new ArrayList<>();
		List<Map> defectProjectYearList = new ArrayList<>();
		List<Map> defectLevelList = new ArrayList<>();
		List<Map> worseProjectList = new ArrayList<>();
		
		if(defectProObject != null) {
			defectProList = JSONArray.parseArray(defectProObject.toString(), Map.class);
		}
		if(devVertionObject != null) {
			devVertionList = JSONArray.parseArray(devVertionObject.toString(), Map.class);
		}
		if(defectMonthObject != null) {
			defectMonthList = JSONArray.parseArray(defectMonthObject.toString(), Map.class);
		}
		if(defectYearObject != null) {
			defectYearList = JSONArray.parseArray(defectYearObject.toString(), Map.class);
		}
		if(defectProjectMonthObject != null) {
			defectProjectMonthList = JSONArray.parseArray(defectProjectMonthObject.toString(), Map.class);
		}
		if(defectProjectYearObject != null) {
			defectProjectYearList = JSONArray.parseArray(defectProjectYearObject.toString(), Map.class);
		}
		if(defectLevelObject != null) {
			defectLevelList = JSONArray.parseArray(defectLevelObject.toString(), Map.class);
		}
		if(worseProjectObject != null) {
			worseProjectList = JSONArray.parseArray(worseProjectObject.toString(), Map.class);
		}
		String monthStartDate = "";
		if (!startDate.split("-")[1].equals(endDate.split("-")[1])
				|| !startDate.split("-")[0].equals(endDate.split("-")[0])) {
			monthStartDate = endDate.substring(0, 8) + "01";
		} else {
			monthStartDate = startDate;
		}
		String monthTime = monthStartDate + "~" + endDate;
		String curYear = endDate.split("-")[0];
		String curMonth = endDate.split("-")[1];
		List<String> yearMonthList = new ArrayList<>();
		for (int i = 12; i >= 1; i--) {
			yearMonthList.add(curYear + "???" + (i<10?"0":"") + i + "???");
		}
		/*if (Integer.valueOf(curMonth) >= 4) {       //???????????????4????????????????????????1?????????
			for (int i = Integer.valueOf(curMonth); i >= 1; i--) {
				yearMonthList.add(curYear + "???" + (i<10?"0":"") + i + "???");
			}
		} else {  			//???????????????4????????????????????????3??????
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
			for (int i = 1; i <= 3; i++) {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
				yearMonthList.add(cal.get(Calendar.YEAR)+"???"+(cal.get(Calendar.MONTH)<10?"0":"")+cal.get(Calendar.MONTH)+"???");
			}

		}*/
		catalogue(workbook);
		summary(workbook,time);
		devVersion(workbook, devVertionList, time);
		defectPro(workbook, defectProList, time,testReportParamVo.getDefectProImg());
		defectProBySystem(workbook, defectMonthList, monthTime,testReportParamVo.getDefectProMonthImg());
		defectProBySystemAllDate(workbook, defectYearList, time,testReportParamVo.getDefectProYearImg());
		defectProByProject(workbook, defectProjectMonthList, monthTime, 1,testReportParamVo.getDefectProProjectMonthImg());
		defectProByProject(workbook, defectProjectYearList, time, 2,testReportParamVo.getDefectProProjectYearImg());
		defectLevel(workbook, defectLevelList, monthTime,testReportParamVo.getDefectLevelImg());
		defectProByWorseProject(workbook, worseProjectList,yearMonthList,curMonth);
		ExcelUtil.download(fileName, workbook, request, response);
	}
	
	/**
	 * ???????????????
	 * @param startDate
	 * @param endDate
	 * @param systemIdStr
	 * @return
	 */
	public Map<String, Object> getChartData(String startDate,String endDate,String systemIdStr,HttpServletRequest request){
		String monthStartDate = "";
		if (!startDate.split("-")[1].equals(endDate.split("-")[1])
				|| !startDate.split("-")[0].equals(endDate.split("-")[0])) {
			monthStartDate = endDate.substring(0, 8) + "01";
		} else {
			monthStartDate = startDate;
		}
		String curYearFirstDay = endDate.split("-")[0] + "-01-01";
		String yearStartDate = startDate.split("-")[0] + "-01-01";
		String projectStartDate = "";
		String projectEndDate = "";
		projectStartDate = curYearFirstDay;
		projectEndDate = endDate;
		/*if (Integer.valueOf(curMonth) >= 4) {       //???????????????4????????????????????????1?????????
			projectStartDate = curYearFirstDay;
			projectEndDate = curDate;
		} else {  			//???????????????4????????????????????????3??????
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
			for (int i = 1; i <= 3; i++) {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
				if(i == 3) {
					projectStartDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)<10?"0":"") +cal.get(Calendar.MONTH) + "-01";
					projectEndDate = curDate;
				}
			}

		}*/
		Long currentUserId = CommonUtil.getCurrentUserId(request);
		List<String> list = null;
		if (!systemIdStr.equals("") && !systemIdStr.equals("null") ) {
			list = JSONArray.parseArray(systemIdStr, String.class);
		}
		List<Map<String, Object>> devVertionReportList = requirementFeatureMapper.selectDevVertionReport(yearStartDate,
				endDate);
		List<Map<String, Object>> defectList = defectInfoMapper.selectDefectPro(yearStartDate, endDate);
		List<Map<String, Object>> defectProMonthList = defectInfoMapper.selectDefectProBySystem(monthStartDate, endDate,
				list,-1);
		List<Map<String, Object>> defectYearList = defectInfoMapper.selectDefectProBySystem(yearStartDate, endDate, list,-1);
		for (Map<String, Object> map : defectYearList) {
			map.put("checkPro", "100.00%");
		}
		List<Map<String, Object>> defectProjectMonthList = defectInfoMapper.selectDefectProByProject(monthStartDate,
				endDate, list,-1);
		List<Map<String, Object>> defectProjectYearList = defectInfoMapper.selectDefectProByProject(yearStartDate, endDate,
				list,-1);
		List<Map<String, Object>> defectLevelList = defectInfoMapper.selectDefectLevel(monthStartDate, endDate);
		List<Map<String, Object>> worseProjectList = defectInfoMapper.selectWorseProject(projectStartDate, projectEndDate);
		Map<String, Object> severityLevelMap = JSON
				.parseObject(redisUtils.get("TBL_DEFECT_INFO_SEVERITY_LEVEL").toString(), Map.class);
		List<String> levels = new ArrayList<>();
		for (Map<String, Object> map : defectLevelList) {
			map.put("severityLevelName",
					severityLevelMap.get(map.get("severityLevel") == null ? "" : map.get("severityLevel").toString()));
			levels.add(map.get("severityLevel") == null ? "" : map.get("severityLevel").toString());
		}
		for (Map.Entry<String, Object> entry : severityLevelMap.entrySet()) {
			if (!levels.contains(entry.getKey())) {
				Map<String, Object> map = new HashMap<>();
				map.put("severityLevel", entry.getKey());
				map.put("severityLevelName", entry.getValue());
				map.put("defectCount", 0);
				defectLevelList.add(map);
			}
		}
		Collections.sort(defectLevelList, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				// TODO Auto-generated method stub
				return Integer.valueOf(o2.get("severityLevel").toString()).compareTo(Integer.valueOf(o1.get("severityLevel").toString()));
			}
		});
		redisUtils.removeList("tmp.*");
		redisUtils.set("tmp."+currentUserId+".devVertion", JSON.toJSONString(devVertionReportList),300l);
		redisUtils.set("tmp."+currentUserId+".defectPro", JSON.toJSONString(defectList),300l);
		redisUtils.set("tmp."+currentUserId+".defectMonth", JSON.toJSONString(defectProMonthList),300l);
		redisUtils.set("tmp."+currentUserId+".defectYear", JSON.toJSONString(defectYearList),300l);
		redisUtils.set("tmp."+currentUserId+".defectProjectMonth", JSON.toJSONString(defectProjectMonthList),300l);
		redisUtils.set("tmp."+currentUserId+".defectProjectYear", JSON.toJSONString(defectProjectYearList),300l);
		redisUtils.set("tmp."+currentUserId+".defectLevel", JSON.toJSONString(defectLevelList),300l);
		redisUtils.set("tmp."+currentUserId+".worseProject", JSON.toJSONString(worseProjectList),300l);
		Map<String, Object> map = new HashMap<>();
		/*????????????????????????(??????)*/
		Map<String, Object> defectProMap = new HashMap<>();
		List<String> timeList = new ArrayList<>();
		List<String> defectProList1 = new ArrayList<>();
		for(Map<String, Object> map2 : defectList) {
			timeList.add(map2.get("time").toString());
			String defectProStr = map2.get("defectPro").toString();
			defectProList1.add(String.format("%.2f",
					Double.valueOf(defectProStr.substring(0, defectProStr.length()-1))));
		}
		
		defectProMap.put("time", timeList);
		defectProMap.put("defectPro", defectProList1);
		
		/*?????????????????????(?????????)*/
		Map<String, Object> defectProMonthMap = new HashMap<>();
		List<String> systemList1 = new ArrayList<>();
		List<String> defectProList2 = new ArrayList<>();
		for (Map<String, Object> map2 : defectProMonthList) {
			systemList1.add(map2.get("systemName") == null?" ":map2.get("systemName").toString());
			String defectProStr = map2.get("defectPro").toString();
			defectProList2.add(String.format("%.2f",
					Double.valueOf(defectProStr.substring(0, defectProStr.length()-1))));
		}
		defectProMonthMap.put("systemName", systemList1);
		defectProMonthMap.put("defectPro",defectProList2);
		
		/*?????????????????????(?????????)*/
		Map<String, Object> defectProYearMap = new HashMap<>();
		List<String> systemList2 = new ArrayList<>();
		List<String> defectProList3 = new ArrayList<>();
		for (Map<String, Object> map2 : defectYearList) {
			systemList2.add(map2.get("systemName") == null?" ":map2.get("systemName").toString());
			String defectProStr = map2.get("defectPro").toString();
			defectProList3.add(String.format("%.2f",
					Double.valueOf(defectProStr.substring(0, defectProStr.length()-1))));
		}
		defectProYearMap.put("systemName", systemList2);
		defectProYearMap.put("defectPro",defectProList3);
		
		/*?????????????????????(?????????)*/
		Map<String, Object> defectProProjectMonthMap = new HashMap<>();
		List<String> projectList1 = new ArrayList<>();
		List<String> defectProList4 = new ArrayList<>();
		for (Map<String, Object> map2 : defectProjectMonthList) {
			projectList1.add(map2.get("projectName") == null?" ":map2.get("projectName").toString());
			String defectProStr = map2.get("defectPro").toString();
			defectProList4.add(String.format("%.2f",
					Double.valueOf(defectProStr.substring(0, defectProStr.length()-1))));
		}
		defectProProjectMonthMap.put("projectName", projectList1);
		defectProProjectMonthMap.put("defectPro",defectProList4);
		
		/*?????????????????????(?????????)*/
		Map<String, Object> defectProProjectYearMap = new HashMap<>();
		List<String> projectList2 = new ArrayList<>();
		List<String> defectProList5 = new ArrayList<>();
		for (Map<String, Object> map2 : defectProjectYearList) {
			projectList2.add(map2.get("projectName") == null?" ":map2.get("projectName").toString());
			String defectProStr = map2.get("defectPro").toString();
			defectProList5.add(String.format("%.2f",
					Double.valueOf(defectProStr.substring(0, defectProStr.length()-1))));
		}
		defectProProjectYearMap.put("projectName", projectList2);
		defectProProjectYearMap.put("defectPro",defectProList5);
		
		/*??????????????????*/
		Map<String, Object> defectProLevelMap = new HashMap<>();
		List<String> levelList = new ArrayList<>();
		List<Map<String, Object>> defectCountList = new ArrayList<>();
		for (Map<String, Object> map2 : defectLevelList) {
			Object severityLevelObj = map2.get("severityLevelName");
			levelList.add(severityLevelObj == null?" ":severityLevelObj.toString());
			Map<String, Object> defectLevelMap = new HashMap<>();
			defectLevelMap.put("name", severityLevelObj == null?" ":severityLevelObj.toString());
			defectLevelMap.put("value", map2.get("defectCount").toString());
			defectCountList.add(defectLevelMap);
		}
		defectProLevelMap.put("level", levelList);
		defectProLevelMap.put("defectCount",defectCountList);
		map.put("defectPro", defectProMap);
		map.put("defectProMonth", defectProMonthMap);
		map.put("defectProYear", defectProYearMap);
		map.put("defectProProjectMonth", defectProProjectMonthMap);
		map.put("defectProProjectYear", defectProProjectYearMap);
		map.put("defectLevel", defectProLevelMap);
		return map;
	}

	/**
	 * ??????
	 * 
	 * @param workbook
	 */
	public void catalogue(Workbook workbook) {
		String sheetName = "??????";
		CreationHelper createHelper = workbook.getCreationHelper();
		CellStyle boldStyle = workbook.createCellStyle();
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle centerStyle = workbook.createCellStyle();
		CellStyle bgStyle = workbook.createCellStyle();
		centerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		bgStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		bgStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		bgStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		Font whiteFont = workbook.createFont();
		whiteFont.setColor(IndexedColors.WHITE.getIndex());
		bgStyle.setFont(whiteFont);
		CellStyle[] borderStyles = getBorderStyle(workbook);
		Sheet sheet = workbook.createSheet(sheetName);
		sheet.setDisplayGridlines(false);
		Font boldFont = workbook.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD); // ??????
		boldStyle.setFont(boldFont);
		Font titleFont = workbook.createFont();
		titleFont.setFontName("????????????"); // ????????????
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD); // ??????
		titleFont.setFontHeight((short) 400);
		titleStyle.setFont(titleFont);
		titleStyle.setWrapText(true);
		for (int i = 0; i <= 32; i++) {
			Row row = sheet.createRow(i);
			for (int j = 0; j <= 17; j++) {
				Cell cell = row.createCell(j);
				if (i == 0 && j > 0 && j < 17) {
					cell.setCellStyle(borderStyles[2]);
				}
				if (i == 32 && j > 0 && j < 17) {
					cell.setCellStyle(borderStyles[0]);
				}
				if (j == 0 && i > 0 && i < 32) {
					cell.setCellStyle(borderStyles[3]);
				}
				if (j == 17 && i > 0 && i < 32) {
					cell.setCellStyle(borderStyles[1]);
				}
				if (j == 9 && i > 0 && i < 32) {
					cell.setCellStyle(borderStyles[4]);
				}
				/* ?????? */
				if (i == 7 && j == 3) {
					cell.setCellStyle(titleStyle);
					cell.setCellValue("????????????????????????\r\n(???????????????)");
				}
				/* ?????? */
				if (i == 28 && j == 4) {
					cell.setCellValue("???????????????");
				}
				/* ?????? */
				if (i == 4 && j == 12) {
					cell.setCellStyle(boldStyle);
					cell.setCellValue("??????");
				}
				if (i == 22 && j == 3) {
					borderStyles[5].setFont(boldFont);
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("????????????");
				}
				if (i == 23 && j == 3) {
					borderStyles[5].setFont(boldFont);
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("????????????");
				}
				if (i == 24 && j == 3) {
					borderStyles[5].setFont(boldFont);
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("??????");
				}
				if (i == 25 && j == 3) {
					borderStyles[5].setFont(boldFont);
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("????????????");
				}
				if (i == 22 && j == 4) {
					cell.setCellStyle(borderStyles[6]);
					cell.setCellValue("????????????");
				}
				if (i == 23 && j == 4) {
					cell.setCellStyle(borderStyles[6]);
					cell.setCellValue("??????????????????");
				}
				if (i == 24 && j == 4) {
					cell.setCellStyle(borderStyles[6]);
					cell.setCellValue("???????????????");
				}
				if (i == 25 && j == 4) {
					cell.setCellStyle(borderStyles[6]);
					String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					cell.setCellValue(date);
				}
				if (i == 8 && j == 10) {
					cell.setCellStyle(bgStyle);
					cell.setCellValue("???");
				}
				if (i == 8 && j == 11) {
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("2018???12????????????????????????????????????");
				}
				if (i == 9 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("1???");
				}
				if (i == 9 && j == 11) {
					cell.setCellValue("????????????????????????");
				}
				if (i == 12 && j == 10) {
					cell.setCellStyle(bgStyle);
					cell.setCellValue("???");
				}
				if (i == 12 && j == 11) {
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("2018???12????????????????????????????????????");
				}
				if (i == 13 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("1???");
				}
				if (i == 13 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#??????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("??????????????????");
				}
				if (i == 14 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("2???");
				}
				if (i == 14 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#????????????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("????????????????????????");
				}
				if (i == 15 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("3???");
				}
				if (i == 15 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#??????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("??????????????????");
				}
				if (i == 16 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("4???");
				}
				if (i == 16 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#?????????????????????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("?????????????????????????????????");
				}
				if (i == 19 && j == 10) {
					cell.setCellStyle(bgStyle);
					cell.setCellValue("???");
				}
				if (i == 19 && j == 11) {
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("2018???12????????????????????????????????????");
				}
				if (i == 20 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("1???");
				}
				if (i == 20 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#???????????????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("???????????????????????????");
				}
				if (i == 21 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("2???");
				}
				if (i == 21 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#???????????????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("???????????????????????????");
				}
				if (i == 25 && j == 10) {
					cell.setCellStyle(bgStyle);
					cell.setCellValue("???");
				}
				if (i == 25 && j == 11) {
					cell.setCellStyle(borderStyles[5]);
					cell.setCellValue("????????????");
				}
				if (i == 26 && j == 10) {
					cell.setCellStyle(centerStyle);
					cell.setCellValue("1???");
				}
				if (i == 26 && j == 11) {
					Hyperlink hyperlink = createHelper.createHyperlink(XSSFHyperlink.LINK_DOCUMENT);
					hyperlink.setAddress("#?????????????????????!A1");
					cell.setHyperlink(hyperlink);
					cell.setCellValue("?????????????????????");
				}
			}
		}
		CellRangeAddress titleCell = new CellRangeAddress(7, 11, 3, 6);
		sheet.addMergedRegion(titleCell);
		CellRangeAddress authorCell = new CellRangeAddress(28, 28, 4, 5);
		sheet.addMergedRegion(authorCell);
		for (int i = 8; i <= 32; i++) {
			if (i >= 22 && i <= 25) {
				CellRangeAddress cellRangeAddress = new CellRangeAddress(i, i, 4, 5);
				sheet.addMergedRegion(cellRangeAddress);
				ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
			}
			if (i >= 8 && i <= 32) {
				CellRangeAddress cellRangeAddress = new CellRangeAddress(i, i, 11, 14);
				sheet.addMergedRegion(cellRangeAddress);
				if (i == 8 || i == 12 || i == 19 || i == 25) {
					ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
				}
			}
		}
	}
	
	public void summary(Workbook workbook,String time) {
		String[] title = {"??????","??????","","","",""};
		String sheetName = "??????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		CellStyle contentStyle = workbook.createCellStyle();
		CellStyle tipStyle = workbook.createCellStyle();
		CellStyle tipTableStyle = workbook.createCellStyle();
		String titleContent = "??????????????????("+time+")";
		Sheet sheet = ExcelUtil.createSheet(title, 1, sheetName, workbook, 1, 2, titleContent, titleStyle, headStyle,
				valueStyle);
		valueStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		valueStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		CellRangeAddress headRegion = new CellRangeAddress(2, 2, 2, 6);
		sheet.addMergedRegion(headRegion);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLUE.getIndex());
		tipStyle.setFont(font);
		tipStyle.setWrapText(true);
		tipTableStyle.cloneStyleFrom(tipStyle);
		ExcelUtil.setValueStyle(tipTableStyle,HSSFCellStyle.BORDER_DOTTED,IndexedColors.BLUE.getIndex());
		for(int i = 1;i <= 7;i++) {
			Row row = sheet.createRow(i+2);
			if(i <=5 && i >=2) {
				row.setHeightInPoints(150);
			}else {
				row.setHeightInPoints(50);
			}
			Cell cell = row.createCell(1);
			cell.setCellStyle(valueStyle);
			cell.setCellValue(i);
			for(int j = 2;j <= 6;j++) {
				Cell cell2 = row.createCell(j);
				cell2.setCellStyle(contentStyle);
			}
			CellRangeAddress region = new CellRangeAddress(i+2, i+2, 2, 6);
			sheet.addMergedRegion(region);
			ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_DOTTED, region, sheet, workbook);
		}
		Row row = sheet.createRow(11);
		Cell cell = row.createCell(1);
		cell.setCellStyle(tipStyle);
		cell.setCellValue("???1");
		
		Row row1 = sheet.createRow(12);
		row1.setHeightInPoints(30);
		Cell cell1 = row1.createCell(1);
		cell1.setCellStyle(tipStyle);
		cell1.setCellValue("???????????????????????????5?????????????????????????????????????????????????????????\r\n" + 
				"???????????????????????????5????????????????????????????????????????????????????????????");
		
		
		Row row2 = sheet.createRow(14);
		Cell cell2 = row2.createCell(1);
		cell2.setCellStyle(tipStyle);
		cell2.setCellValue("???2");
		
		Row row3 = sheet.createRow(15);
		Cell cell3 = row3.createCell(1);
		cell3.setCellStyle(tipStyle);
		cell3.setCellValue("???????????????????????????????????????????????????");
		
		String[][] riskArr = {{"<10%","???"},{"10%-20%","???"},{">20%","???"}};
		for(int i = 0;i < 3;i++) {
			Row row4 = sheet.createRow(16+i);
			for(int j = 0;j < 2;j++) {
				Cell cell4 = row4.createCell(1+j);
				cell4.setCellStyle(tipTableStyle);
				cell4.setCellValue(riskArr[i][j]);
			}
		}
		
		Row row5 = sheet.createRow(20);
		Cell cell5 = row5.createCell(1);
		cell5.setCellStyle(tipStyle);
		cell5.setCellValue("???3");
		
		Row row6 = sheet.createRow(21);
		Cell cell6 = row6.createCell(1);
		cell6.setCellStyle(tipStyle);
		cell6.setCellValue("?????????????????????????????????????????????");
		
		String[][] devArr = {{"<2%","??????"},{"2%-4%","??????"},{">4%","??????"}};
		for(int i = 0;i < 3;i++) {
			Row row4 = sheet.createRow(22+i);
			for(int j = 0;j < 2;j++) {
				Cell cell4 = row4.createCell(1+j);
				cell4.setCellStyle(tipTableStyle);
				cell4.setCellValue(devArr[i][j]);
			}
		}
		
		Row row7 = sheet.createRow(26);
		Cell cell7 = row7.createCell(1);
		cell7.setCellStyle(tipStyle);
		cell7.setCellValue("???4");
		
		Row row8 = sheet.createRow(27);
		Cell cell8 = row8.createCell(1);
		cell8.setCellStyle(tipStyle);
		cell8.setCellValue("??????????????????????????????????????????????????????");
		
		String[][] fixArr = {{"<1.05","???"},{"1.05-1.15","??????"},{">4%","???"}};
		for(int i = 0;i < 3;i++) {
			Row row4 = sheet.createRow(28+i);
			for(int j = 0;j < 2;j++) {
				Cell cell4 = row4.createCell(1+j);
				cell4.setCellStyle(tipTableStyle);
				cell4.setCellValue(fixArr[i][j]);
			}
		}
		
		CellRangeAddress region = new CellRangeAddress(12, 12, 1, 6);
		sheet.addMergedRegion(region);
		CellRangeAddress region1 = new CellRangeAddress(15, 15, 1, 3);
		sheet.addMergedRegion(region1);
		CellRangeAddress region2 = new CellRangeAddress(21, 21, 1, 3);
		sheet.addMergedRegion(region2);
		CellRangeAddress region3 = new CellRangeAddress(27, 27, 1, 3);
		sheet.addMergedRegion(region3);
	}

	public void devVersion(Workbook workbook, List<Map> contents, String time) {
		String[] title = { "??????", "?????????????????????", "??????????????????", "??????????????????", "?????????????????????", "?????????????????????", "??????????????????", "???????????????", "????????????",
				"????????????" };
		String sheetName = "??????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		String titleContent = "??????????????????????????????(" + time + ")";
		int startRow = 4;
		Sheet sheet = ExcelUtil.createSheet(title, 1, sheetName, workbook, 1, 3, titleContent, titleStyle, headStyle,
				valueStyle);
		Row regionHeadRow = sheet.createRow(2);
		int startCol = 1;
		for (int i = 0; i < title.length; i++) {
			Cell cell = regionHeadRow.createCell(startCol);
			cell.setCellStyle(headStyle);
			if (startCol == 1) {
				cell.setCellValue("??????");
			} else if (startCol == 2) {
				cell.setCellValue("?????????");
			} else if (startCol == 5) {
				cell.setCellValue("????????????");
			} else if (startCol == 9) {
				cell.setCellValue("????????????");
			}
			startCol++;
		}
		CellRangeAddress region = new CellRangeAddress(2, 3, 1, 1);
		sheet.addMergedRegion(region);
		ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_DOTTED, region, sheet, workbook);
		CellRangeAddress region1 = new CellRangeAddress(2, 2, 2, 4);
		sheet.addMergedRegion(region1);
		ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_DOTTED, region1, sheet, workbook);
		CellRangeAddress region2 = new CellRangeAddress(2, 2, 5, 8);
		sheet.addMergedRegion(region2);
		ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_DOTTED, region2, sheet, workbook);
		CellRangeAddress region3 = new CellRangeAddress(2, 2, 9, 10);
		sheet.addMergedRegion(region3);
		ExcelUtil.setBorderStyle(XSSFCellStyle.BORDER_DOTTED, region3, sheet, workbook);
		String[] field = new String[] { "time", "planWindow", "tmpWindow", "urgWindow", "tmpAddTask", "tmpDeleteTask",
				"taskCount", "verUpdatePro", "reqCount", "defectCount" };
		Map<String, Object> formatMap = new HashMap<String, Object>();
		formatMap.put("verUpdatePro", "SUM(F@:G@)/H@");
		printContent(workbook, contents, sheet, 4, 1, valueStyle, field, null, formatMap);

	}

	/**
	 * ????????????????????????
	 * 
	 * @param workbook
	 * @param contents
	 * @param time
	 */
	public void defectPro(Workbook workbook, List<Map> contents, String time,String imgUrl) {
		String[] title = { "??????", "???????????????", "2?????????????????????", "???????????????", "???????????????", "?????????", "???????????????", "?????????", "??????????????????", "??????????????????" };
		String sheetName = "????????????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		String titleContent = "??????????????????????????????(" + time + ")";
		Integer startRow = 23;
		Object[] objects = new Object[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Sheet sheet = ExcelUtil.createSheet(title, 1, sheetName, workbook, 21, 22, titleContent, titleStyle, headStyle,
				valueStyle);
		String[] field = new String[] { "time", "taskCount", "defectCount", "fixedDefectCount", "caseCount",
				"defectPro", "omitDefect", "checkPro", "repairRound", "avgRepair" };
		startRow = printContent(workbook, contents, sheet, startRow, 1, valueStyle, field, objects, null);
		Row lastRow = sheet.createRow(startRow);
		Cell cell = lastRow.createCell(1);
		cell.setCellStyle(valueStyle);
		cell.setCellValue("??????");
		for (int i = 0; i < objects.length; i++) {
			Cell valueCell = lastRow.createCell(i + 2);
			valueCell.setCellStyle(valueStyle);
			String content = objects[i].toString();
			if (i == 4) {
				content = objects[3].toString().equals("0") ? "0%"
						: String.format("%.2f",
								Double.valueOf(objects[1].toString()) / Double.valueOf(objects[3].toString()) * 100)
								+ "%";
			} else if (i == 6) {
				content = "100.00%";
			} else if (i == 8) {
				content = objects[2].toString().equals("0") ? "0"
						: String.format("%.2f",
								Double.valueOf(objects[7].toString()) / Double.valueOf(objects[2].toString()));
			}
			valueCell.setCellValue(content);
		}
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(imgUrl.split("base64,")[1]);
		Drawing patriarch = sheet.createDrawingPatriarch();  
		ClientAnchor anchor = new XSSFClientAnchor(0,0,0,0,1,1,10,19);
		patriarch.createPicture(anchor, workbook.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_JPEG));
		sheet.setDisplayGridlines(false);
	}

	public void defectProBySystem(Workbook workbook, List<Map> contents, String time,String imgUrl) {
		String[] title = { "????????????", "???????????????", "????????????", "???????????????", "???????????????", "???????????????", "?????????", "??????????????????", "??????????????????", "???????????????",
				"??????????????????" };
		String sheetName = "??????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		String titleContent = "??????????????????(" + time + ")";
		int startRow = 23;
		Sheet sheet = ExcelUtil.createSheet(title, 1, sheetName, workbook, 21, 22, titleContent, titleStyle, headStyle,
				valueStyle);
		String[] field = new String[] { "systemName", "taskCount", "defectCount", "fixedDefectCount", "remainDefect",
				"caseCount", "defectPro", "repairRound", "avgRepair", "omitDefect", "omitBelong" };
		printContent(workbook, contents, sheet, startRow, 1, valueStyle, field, null, null);
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(imgUrl.split("base64,")[1]);
		Drawing patriarch = sheet.createDrawingPatriarch();  
		ClientAnchor anchor = new XSSFClientAnchor(0,0,0,0,1,1,11,19);
		patriarch.createPicture(anchor, workbook.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_JPEG));
		sheet.setDisplayGridlines(false);
	}

	public void defectProBySystemAllDate(Workbook workbook, List<Map> contents, String time,String imgUrl) {
		String[] title = { "????????????", "???????????????", "????????????", "???????????????", "???????????????", "???????????????", "?????????", "???????????????", "?????????", "??????????????????",
				"??????????????????" };
		String sheetName = "?????????????????????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		String titleContent = "????????????????????????????????????(" + time + ")";
		int startRow = 23;
		Sheet sheet = ExcelUtil.createSheet(title, 1, sheetName, workbook, 21, 22, titleContent, titleStyle, headStyle,
				valueStyle);
		String[] field = new String[] { "systemName", "taskCount", "defectCount", "fixedDefectCount", "remainDefect",
				"caseCount", "defectPro", "omitDefect", "checkPro", "repairRound", "avgRepair" };
		printContent(workbook, contents, sheet, startRow, 1, valueStyle, field, null, null);
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(imgUrl.split("base64,")[1]);
		Drawing patriarch = sheet.createDrawingPatriarch();  
		ClientAnchor anchor = new XSSFClientAnchor(0,0,0,0,1,1,11,19);
		patriarch.createPicture(anchor, workbook.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_JPEG));
		sheet.setDisplayGridlines(false);
	}

	public void defectProByProject(Workbook workbook, List<Map> contents, String time, Integer type,String imgUrl) {
		String[] title = { "???????????????", "????????????", "?????????", "????????????", "???????????????", "???????????????", "???????????????", "?????????" };
		String sheetName = type == 1 ? "???????????????????????????" : "???????????????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		String titleContent = "?????????????????????(" + time + ")";
		int startRow = 25;
		Sheet sheet = ExcelUtil.createSheet(title, 0, sheetName, workbook, 23, 24, titleContent, titleStyle, headStyle,
				valueStyle);
		String[] field = new String[] { "projectName", "systemName", "taskCount", "defectCount", "fixedDefectCount",
				"remainDefect", "caseCount", "defectPro" };
		printContent(workbook, contents, sheet, startRow, 0, valueStyle, field, null, null);
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(imgUrl.split("base64,")[1]);
		Drawing patriarch = sheet.createDrawingPatriarch();  
		ClientAnchor anchor = new XSSFClientAnchor(0,0,0,0,0,1,11,20);
		patriarch.createPicture(anchor, workbook.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_JPEG));
		sheet.setDisplayGridlines(false);
	}

	public void defectLevel(Workbook workbook, List<Map> contents, String time,String imgUrl) {
		String[] title = { "????????????", "????????????" };
		String sheetName = "?????????????????????";
		CellStyle titleStyle = workbook.createCellStyle();
		CellStyle headStyle = workbook.createCellStyle();
		CellStyle valueStyle = workbook.createCellStyle();
		String titleContent = "??????????????????\r\n(" + time + ")";
		int startRow = 6;
		Sheet sheet = ExcelUtil.createSheet(title, 9, sheetName, workbook, 4, 5, titleContent, titleStyle, headStyle,
				valueStyle);
		String[] field = new String[] { "severityLevelName", "defectCount" };
		printContent(workbook, contents, sheet, startRow, 9, valueStyle, field, null, null);
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(imgUrl.split("base64,")[1]);
		Drawing patriarch = sheet.createDrawingPatriarch();  
		ClientAnchor anchor = new XSSFClientAnchor(0,0,0,0,1,1,8,17);
		patriarch.createPicture(anchor, workbook.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_JPEG));
		sheet.setDisplayGridlines(false);
	}

	public Integer printContent(Workbook workbook, List<Map> contents, Sheet sheet, Integer startRow,
			Integer startCol, CellStyle cellStyle, String[] field, Object[] totals, Map<String, Object> formatMap) {
		for (int i = 0; i < contents.size(); i++) {
			Row row = sheet.createRow(startRow);
			Map<String, Object> map = contents.get(i);
			Integer col = startCol;
			for (int j = 0; j < field.length; j++) {
				Cell cell = row.createCell(col);
				if (formatMap != null && formatMap.containsKey(field[j])) {
					CellStyle cellStyle2 = workbook.createCellStyle();
					cellStyle2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
					cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
					cell.setCellStyle(cellStyle2);
					String format = formatMap.get(field[j]).toString();
					format = format.replaceAll("@", (startRow + 1) + "");
					cell.setCellFormula(format);
				} else {
					cell.setCellStyle(cellStyle);
				}
				String content = map.get(field[j]) == null ? "" : map.get(field[j]).toString();
				if(NumberUtils.isNumber(content)) {
					if (content.indexOf(".") != -1) {
						Double contentDou = 0d;
						if (content.indexOf("%") != -1) {
							content = content.substring(0, content.length() - 1);
							contentDou = Double.valueOf(content);
							String contentNum = (contentDou.equals(0d) ? "0" : String.format("%.2f", contentDou));
							if (totals != null && NumberUtils.isNumber(content)) {
								totals[j - 1] = Double.valueOf(totals[j - 1].toString()) + Double.valueOf(contentNum);
							}
							content = contentNum + "%";
						} else {
							contentDou = Double.valueOf(content);
							content = contentDou.equals(0d) ? "0" : String.format("%.2f", contentDou);
							if (totals != null && NumberUtils.isNumber(content)) {
								totals[j - 1] = Double.valueOf(totals[j - 1].toString()) + Double.valueOf(content);
							}
						}
					} else if (totals != null) {
						if (totals[j - 1].toString().indexOf(".") != -1) {
							totals[j - 1] = Double.valueOf(totals[j - 1].toString()) + Integer.valueOf(content);
						} else {
							totals[j - 1] = Integer.valueOf(totals[j - 1].toString()) + Integer.valueOf(content);
						}
					}
				}
				if (NumberUtils.isNumber(content) && content.indexOf(".") == -1) {
					cell.setCellValue(Integer.valueOf(content));
				} else {
					if (content.indexOf(",") != -1) {
						content = content.replaceAll(",", "\r\n");
					}
					cell.setCellValue(content);
				}
				col++;
			}
			startRow++;
		}
		return startRow;
	}

	/**
	 * ??????????????????
	 * 
	 * @param workbook
	 * @return
	 */
	public CellStyle[] getBorderStyle(Workbook workbook) {
		CellStyle[] cellStyles = new CellStyle[11];
		for (int i = 0; i < 8; i++) {
			cellStyles[i] = workbook.createCellStyle();
			if (i <= 4) {
				cellStyles[i].setTopBorderColor(IndexedColors.BLUE.getIndex());
				cellStyles[i].setRightBorderColor(IndexedColors.BLUE.getIndex());
				cellStyles[i].setBottomBorderColor(IndexedColors.BLUE.getIndex());
				cellStyles[i].setLeftBorderColor(IndexedColors.BLUE.getIndex());
			}
		}
		cellStyles[0].setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyles[1].setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyles[2].setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyles[3].setBorderRight(HSSFCellStyle.BORDER_THIN);

		cellStyles[4].setBorderLeft(HSSFCellStyle.BORDER_DASH_DOT);

		cellStyles[5].setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyles[5].setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyles[5].setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyles[5].setBorderRight(HSSFCellStyle.BORDER_THIN);

		cellStyles[6].setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyles[6].setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyles[6].setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyles[6].setBorderRight(HSSFCellStyle.BORDER_THIN);

		return cellStyles;
	}

	/**
	 * ?????????????????????
	 * @param workbook
	 * @param worseProjectList
	 * @param yearMonthList
	 */
	private void defectProByWorseProject(Workbook workbook, List<Map> worseProjectList,List<String> yearMonthList,String month) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> newWorseProjectList = new ArrayList<>();
		for (Map<String, Object> map : worseProjectList) {
			List<Map<String, Object>> timeList = new ArrayList<>();
			Boolean flag = false;
			Double totalDefectPro = 0d;
			Integer defectCount = 0;
			Double caseCount = 0d;
			Integer reqCount = 0;
			for (Map<String, Object> map2 : newWorseProjectList) {
				if (map2.get("projectName").toString()
						.equals(map.get("projectName") == null ? " " : map.get("projectName").toString())) {
					Map<String, Object> defectMap = new HashMap<>();
					timeList = (List<Map<String, Object>>) map2.get("timeList");
					Double defectProDou = Double.valueOf(map.get("defectPro").toString());
					String defectProStr = String.format("%.2f", defectProDou);
					defectCount = Integer.valueOf(map2.get("defectCount").toString());
					caseCount = Double.valueOf(map2.get("caseCount").toString());
					reqCount = Integer.valueOf(map2.get("reqCount").toString());
					defectCount += Integer.valueOf(map.get("defectCount").toString());
					caseCount += Double.valueOf(map.get("caseCount").toString());
					reqCount += Integer.valueOf(map.get("reqCount").toString());
					totalDefectPro = caseCount == 0?0d:(defectCount/caseCount * 100);
					map2.put("defectTotal", totalDefectPro);
					map2.put("defectCount", defectCount);
					map2.put("caseCount", caseCount);
					map2.put("reqCount", reqCount);
					defectMap.put("time", map.get("time"));
					defectMap.put("defectPro", defectProStr);
					timeList.add(defectMap);
					map2.put("timeList", timeList);
					flag = true;
					break;
				}
			}
			if (!flag) {
				HashMap<String, Object> map2 = new HashMap<>();
				Map<String, Object> defectMap = new HashMap<>();
				Double defectProDou = Double.valueOf(map.get("defectPro").toString());
				String defectProStr = String.format("%.2f", defectProDou);
				defectCount += Integer.valueOf(map.get("defectCount").toString());
				caseCount += Double.valueOf(map.get("caseCount").toString());
				reqCount += Integer.valueOf(map.get("reqCount").toString());
				totalDefectPro = caseCount == 0?0d:(defectCount/caseCount * 100);
				defectMap.put("time", map.get("time"));
				defectMap.put("defectPro", defectProStr);
				timeList.add(defectMap);
				map2.put("timeList", timeList);
				map2.put("projectName", map.get("projectName") == null ? " " : map.get("projectName"));
				map2.put("defectTotal", totalDefectPro);
				map2.put("defectCount", defectCount);
				map2.put("caseCount", caseCount);
				map2.put("reqCount", reqCount);
				newWorseProjectList.add(map2);
			}
		}
		/* ?????? */
		newWorseProjectList.sort((Map<String, Object> map1,
				Map<String, Object> map2) -> (Double.valueOf(map2.get("defectTotal").toString())
						- Double.valueOf(map1.get("defectTotal").toString())) > 0d ? 1 : -1);
		CellStyle valueStyle = workbook.createCellStyle();
		String sheetName = "??????????????????????????????????????????";
		Sheet sheet = workbook.createSheet(sheetName);
		ExcelUtil.setValueStyle(valueStyle,HSSFCellStyle.BORDER_THIN,IndexedColors.GREY_50_PERCENT.getIndex());
		int colWidth = sheet.getColumnWidth(0) * 2;
		int intFlag = 0;
		Set<String> defectTotalSet = new HashSet<>();
		for (int i = 0; i < newWorseProjectList.size(); i++) {
			if (intFlag > 2) {
				break;
			}
			Map<String, Object> worseProjectMap = newWorseProjectList.get(i);
			if(Integer.valueOf(worseProjectMap.get("reqCount").toString()) > 5) { 
				if(!defectTotalSet.contains(worseProjectMap.get("defectTotal").toString())) {
					intFlag++;
					defectTotalSet.add(worseProjectMap.get("defectTotal").toString());
				}
			}else {
				continue;
			}
			String projectName = worseProjectMap.get("projectName").toString();
			Double defectTotalDou = Double.valueOf(worseProjectMap.get("defectTotal").toString());
			String defectTotal = defectTotalDou.equals(0d)?"0":String.format("%.2f", defectTotalDou) + "%";
			int firstRow = 21+intFlag*4;
			Row row = sheet.createRow(firstRow);
			Cell cell = row.createCell(0);
			cell.setCellValue(projectName);
			Row row1 = sheet.createRow(firstRow+1);
			Cell cell1 = row1.createCell(0);
			cell1.setCellStyle(valueStyle);
			cell1.setCellValue("??????");
			Row row2 = sheet.createRow(firstRow+2);
			Cell cell2 = row2.createCell(0);
			cell2.setCellStyle(valueStyle);
			cell2.setCellValue("?????????");
			
			List<Map<String, Object>> timeList = (List<Map<String, Object>>)worseProjectMap.get("timeList");
			for(int j = yearMonthList.size();j >= 1;j--) {
				Cell monthCell = row1.createCell(j);
				monthCell.setCellStyle(valueStyle);
				monthCell.setCellValue(yearMonthList.get(yearMonthList.size()-j));
				Integer monthInt = Integer.valueOf(yearMonthList.get(yearMonthList.size()-j).substring(5, yearMonthList.get(yearMonthList.size()-j).indexOf("???")));

				Cell defectCell = row2.createCell(j);
				defectCell.setCellStyle(valueStyle);
				String defectPro = "0%";
				for(int k = 0;k < timeList.size();k++) {     //?????????????????????????????????
					if(timeList.get(k).get("time").toString().equals(yearMonthList.get(yearMonthList.size()-j))) {
						defectPro = timeList.get(k).get("defectPro").toString()+"%";
						break;
					}else if(monthInt > Integer.valueOf(month)) {
						defectPro = " ";
					}
				}
				defectCell.setCellValue(defectPro);
				sheet.setColumnWidth(j, colWidth < 3000 ? 3000 : colWidth);
			}
			Cell totalCell = row1.createCell(yearMonthList.size()+1);
			totalCell.setCellStyle(valueStyle);
			totalCell.setCellValue("??????");
			Cell defectTotalCell = row2.createCell(yearMonthList.size()+1);
			defectTotalCell.setCellStyle(valueStyle);
			defectTotalCell.setCellValue(defectTotal);
			CellRangeAddress rangeCell = new CellRangeAddress(firstRow, firstRow, 0, 2);
			sheet.addMergedRegion(rangeCell);
		}
		
	}
}
