package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.dev.entity.TblTestTask;
import cn.pioneeruniverse.dev.service.testReport.ITestReport;
import cn.pioneeruniverse.dev.vo.TestReportParamVo;

@RestController
@RequestMapping("testReport")
public class TestReportController extends BaseController{

	@Autowired
	private ITestReport testReportService;
	
	/**
	 * 导出
	 * @param startDate
	 * @param endDate
	 * @param systemIdStr
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("exportReport")
	public Map<String, Object> exportReport(TestReportParamVo testReportParamVo,HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			testReportService.export1(testReportParamVo, request, response);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	@RequestMapping("getChartData")
	public Map<String, Object> getChartData(String startDate,String endDate,String systemIdStr,HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = testReportService.getChartData(startDate, endDate, systemIdStr,request);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}
}
