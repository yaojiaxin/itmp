package cn.pioneeruniverse.dev.service.testReport;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.vo.TestReportParamVo;

public interface ITestReport {
	
	public Map<String, Object> getChartData(String startDate,String endDate,String systemIdStr,HttpServletRequest request);
	
	public void export1(TestReportParamVo testReportParamVo,HttpServletRequest request,HttpServletResponse response) throws Exception;
}
