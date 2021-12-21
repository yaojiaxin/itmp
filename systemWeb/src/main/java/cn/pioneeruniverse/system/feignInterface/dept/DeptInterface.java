package cn.pioneeruniverse.system.feignInterface.dept;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.system.feignFallback.dept.DeptFallback;
import cn.pioneeruniverse.system.vo.dept.TblDeptInfo;

@FeignClient(value="system",fallbackFactory=DeptFallback.class)
public interface DeptInterface {

	@RequestMapping(value="dept/getAllDept",method=RequestMethod.POST)
    Map<String,Object> getAllDept(@RequestBody TblDeptInfo dept);

	
	@RequestMapping(value="dept/selectDeptById",method=RequestMethod.POST)
    Map<String,Object> selectDeptById(@RequestParam("id") Long id);

	
	@RequestMapping(value="dept/selectDeptByParentId",method=RequestMethod.POST)
    Map<String,Object> selectDeptByParentId(@RequestParam("id") Long id) ;

	@RequestMapping(value="dept/insertDept",method=RequestMethod.POST)
    Map<String,Object> insertDept(@RequestBody TblDeptInfo dept);

	@RequestMapping(value="dept/updateDept",method=RequestMethod.POST)
    Map<String,Object> updateDept(@RequestBody TblDeptInfo dept);
	
	@RequestMapping(value="dept/getDeptByCompanyId",method=RequestMethod.POST)
    Map<String,Object> getDeptByCompanyId(@RequestParam("companyId") Long companyId);
}
