package cn.pioneeruniverse.dev.feignInterface.devtask;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.pioneeruniverse.dev.entity.TblDeptInfo;
import cn.pioneeruniverse.dev.entity.TblUserInfo;
import cn.pioneeruniverse.dev.feignFallback.testtask.UserFallback;



@FeignClient(value="system",fallbackFactory = UserFallback.class)
public interface UserInterface {

	@RequestMapping(value="user/getDept",method=RequestMethod.POST)
	List<TblDeptInfo> getDept();
	
	
	
}
