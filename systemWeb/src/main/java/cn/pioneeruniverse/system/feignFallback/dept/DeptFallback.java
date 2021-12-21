package cn.pioneeruniverse.system.feignFallback.dept;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.system.feignInterface.dept.DeptInterface;
import cn.pioneeruniverse.system.vo.dept.TblDeptInfo;
import feign.hystrix.FallbackFactory;

@Component
public class DeptFallback implements FallbackFactory<DeptInterface>{

	private final static Logger logger = LoggerFactory.getLogger(DeptFallback.class);

	@Override
	public DeptInterface create(Throwable e) {
		return new DeptInterface(){

			@Override
			public Map<String, Object> getAllDept(TblDeptInfo dept) {
				return handleFeignError(e);
			}

			@Override
			public Map<String, Object> selectDeptById(Long id) {
				return handleFeignError(e);
			}

			@Override
			public Map<String, Object> selectDeptByParentId(Long id) {
				return handleFeignError(e);
			}

			@Override
			public Map<String, Object> insertDept(TblDeptInfo dept) {
				return handleFeignError(e);
			}

			@Override
			public Map<String, Object> updateDept(TblDeptInfo dept) {
				return handleFeignError(e);
			}

			@Override
			public Map<String, Object> getDeptByCompanyId(Long companyId) {
				return handleFeignError(e);
			}
			
		};
	}

	private Map<String,Object> handleFeignError(Throwable cause){
		Map<String,Object> map = new HashMap<String,Object>();
		String message = "部门接口调用故障";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		cause.printStackTrace(new PrintStream(baos));  
		String exception = baos.toString(); 
		map.put("status", Constants.ITMP_RETURN_FAILURE);
		logger.error(message+":"+exception);
		map.put("errorMessage", message);
		return map;
	}
}
