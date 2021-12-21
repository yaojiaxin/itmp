package cn.pioneeruniverse.project.feignFallback;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.project.feignInterface.OamProjectInfoInterface;
import feign.hystrix.FallbackFactory;

@Component
public class OamProjectInfoFallback implements FallbackFactory<OamProjectInfoInterface> {
	
private static final Logger logger = LoggerFactory.getLogger(ProjectInfoFallback.class);
	
	private Map<String,Object> handleFeignError(Throwable cause){
		Map<String,Object> map = new HashMap<String,Object>();
		String message = "接口调用故障";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		cause.printStackTrace(new PrintStream(baos));  
		String exception = baos.toString(); 
		map.put("status", Constants.ITMP_RETURN_FAILURE);
		logger.error(message+":"+exception);
		map.put("errorMessage", message);
		return map;
	}

	@Override
	public OamProjectInfoInterface create(Throwable cause) {
		return new OamProjectInfoInterface() {

			@Override
			public Map<String, Object> selectProjectAndUserById(Long id, String type) {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

}
