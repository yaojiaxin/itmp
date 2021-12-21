package cn.pioneeruniverse.project.feignFallback.requirment;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.pioneeruniverse.project.entity.TblDeptInfo;
import cn.pioneeruniverse.project.entity.TblUserInfo;
import cn.pioneeruniverse.project.feignInterface.requirment.UserInterface;
import feign.hystrix.FallbackFactory;


@Component
public class UserFallback  implements FallbackFactory<UserInterface>{

	@Override
	public UserInterface create(Throwable cause) {
		// TODO Auto-generated method stub
		return new UserInterface() {
			
			@Override
			public List<TblUserInfo> getUser() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<TblDeptInfo> getDept() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> findUserById(Long userId) {				
				return null;
			}

			@Override
			public Map<String, Object> selectDeptById(Long id) {
				return null;
			}
		};
	}

	




	 
	
	
	
}
