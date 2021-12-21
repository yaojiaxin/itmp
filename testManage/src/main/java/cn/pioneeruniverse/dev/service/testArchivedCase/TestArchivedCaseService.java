package cn.pioneeruniverse.dev.service.testArchivedCase;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import cn.pioneeruniverse.dev.entity.TblArchivedCase;
import cn.pioneeruniverse.dev.entity.TblCaseInfo;

public interface TestArchivedCaseService {

	List<TblArchivedCase> getArchivedCases(TblArchivedCase tblArchivedCase, String filters, Integer page, Integer rows);

	TblArchivedCase getArchivedCaseById(Long id);

	void insertTestCase(TblCaseInfo tblCaseInfo, HttpServletRequest request);

	void removeArchivedTest(List<Long> ids, HttpServletRequest request);

	void exportTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> importExcel(MultipartFile file, HttpServletRequest request) throws Exception;

	List<TblArchivedCase> getCaseAndSteps(TblArchivedCase tblArchivedCase, String f);

	void exportExcel(List<TblArchivedCase> list, HttpServletRequest request, HttpServletResponse response) throws Exception;

	void editCaseInfo(TblCaseInfo caseInfo, HttpServletRequest request);

	void editArchivedCase(TblCaseInfo caseInfo, HttpServletRequest request);

}
