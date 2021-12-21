package cn.pioneeruniverse.system.feignInterface.company;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.pioneeruniverse.system.feignFallback.dept.DeptFallback;
import cn.pioneeruniverse.system.vo.company.Company;
import cn.pioneeruniverse.system.vo.dept.TblDeptInfo;

@FeignClient(value="system",fallbackFactory=CompanyInterface.class)
public interface CompanyInterface {
	
	@RequestMapping(value="company/getCompany",method=RequestMethod.POST)
	List<Company> getCompany();
}
