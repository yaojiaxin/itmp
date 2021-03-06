package cn.pioneeruniverse.dev.feignFallback.testtask;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.pioneeruniverse.dev.feignInterface.devtask.TestTaskInterface;
import feign.hystrix.FallbackFactory;

/**
*  类说明 
* @author:tingting
* @version:2018年11月13日 下午3:22:21 
*/
@Component
public class TestTaskFallback implements FallbackFactory<TestTaskInterface>{

	@Override
	public TestTaskInterface create(Throwable cause) {
		// TODO Auto-generated method stub
		return new TestTaskInterface() {
			
			@Override
			public Map<String, Object> toAddData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> getOneDevTask(Long id) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	

}
