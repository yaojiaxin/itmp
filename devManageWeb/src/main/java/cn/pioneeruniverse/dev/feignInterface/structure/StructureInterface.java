package cn.pioneeruniverse.dev.feignInterface.structure;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.dev.feignFallback.structure.StructureFallback;


@FeignClient(value="devManage",fallbackFactory=StructureFallback.class)
public interface StructureInterface {
	

	
	@RequestMapping(value="structure/getAllSystemInfo",method=RequestMethod.POST)
    Map<String,Object> getAllSystemInfo( @RequestParam("pageIndex") Integer pageIndex, @RequestParam("pageSize") Integer pageSize);
	@RequestMapping(value="structure/getSystemModule",method=RequestMethod.POST)
	Map<String,Object> getSystemModule( @RequestParam("id") Integer id);  
	
	@RequestMapping(value="structure/creatJenkinsJob",method=RequestMethod.POST)
	Map<String,Object> creatJenkinsJob( @RequestParam("systemId") Integer systemId, @RequestParam("systemName")String systemName,@RequestParam("serverType")String serverType,@RequestParam("module")String[] module);


	
	
}
