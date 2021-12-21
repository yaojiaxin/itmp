package cn.pioneeruniverse.project.feignInterface.requirment;

import java.util.Map;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.feignFallback.requirment.RequirementFallback;

import javax.servlet.http.HttpServletRequest;

@FeignClient(value="projectManage",fallbackFactory=RequirementFallback.class)
public interface RequirementInterface {
	
	//根据id获取需求信息
	@RequestMapping(value="requirement/toEditRequirementById",method=RequestMethod.POST)
	Map<String, Object> toEditRequirementById(@RequestParam("rIds") Long rIds);
		
	@RequestMapping(value="requirement/getExcelRequirement",method=RequestMethod.POST)
	List<TblRequirementInfo> getExcelRequirement(@RequestParam("findRequirment")String findRequirment,
        @RequestParam("uid")Long uid,@RequestBody List<String> roleCodes);
	
	@RequestMapping(value="requirement/getRequirementsByIds",method=RequestMethod.POST)
	List<String> getRequirementsByIds(@RequestParam("reqIds")String reqIds);

	@RequestMapping(value="requirement/getRequirementFiled2",method=RequestMethod.POST)
	Map<String, Object> findRequirementField(@RequestParam("id")Long id);
	
	@RequestMapping(value="requirement/getsystems",method=RequestMethod.POST)
	List<String> getsystems(@RequestParam("id")Long id);
	
	/*@RequestMapping(value="requirement/getCountRequirement",method=RequestMethod.POST)
    int getCountRequirement(@RequestParam("requirmentJson") String requirmentJson);*/
	
	//根据id查询需求信息	
	/*@RequestMapping(value="requirement/findRequirementById",method=RequestMethod.POST)
	Map<String,Object> findRequirementById(@RequestParam("rId")Long rId,@RequestParam("parentId")Long parentId);*/
	
	/*@RequestMapping(value="requirement/getDataDicList",method=RequestMethod.POST)
	List<TblDataDic> getDataDicList(@RequestParam("datadictype")String datadictype);*/
		
}
