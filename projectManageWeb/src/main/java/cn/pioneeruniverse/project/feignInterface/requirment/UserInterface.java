package cn.pioneeruniverse.project.feignInterface.requirment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.project.entity.TblDeptInfo;
import cn.pioneeruniverse.project.entity.TblUserInfo;
import cn.pioneeruniverse.project.feignFallback.requirment.UserFallback;





@FeignClient(value="system",fallbackFactory = UserFallback.class)
public interface UserInterface {

	@RequestMapping(value="user/getDept",method=RequestMethod.POST)
	List<TblDeptInfo> getDept();
	@RequestMapping(value="user/getUserNoCon",method=RequestMethod.POST)
	List<TblUserInfo> getUser();
	
	@RequestMapping(value = "user/findUserById", method = RequestMethod.POST)
    Map<String, Object> findUserById(@RequestParam("userId")Long userId);
	
	@RequestMapping(value="dept/selectDeptById",method=RequestMethod.POST)
	Map<String,Object> selectDeptById(@RequestParam("id")Long id);
}
