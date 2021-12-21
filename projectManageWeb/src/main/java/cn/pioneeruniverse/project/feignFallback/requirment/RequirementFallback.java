package cn.pioneeruniverse.project.feignFallback.requirment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.feignInterface.requirment.RequirementInterface;
import feign.hystrix.FallbackFactory;

import javax.servlet.http.HttpServletRequest;


@Component
public class RequirementFallback  implements FallbackFactory<RequirementInterface>{

    private static final Logger logger = LoggerFactory.getLogger(RequirementFallback.class);

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
	public RequirementInterface create(Throwable cause) {
		return new RequirementInterface(){

			@Override
			public Map<String, Object> toEditRequirementById(Long rids) {
				return handleFeignError(cause);
			}

			@Override
			public List<TblRequirementInfo> getExcelRequirement(String findRequirment, Long uid,List<String> roleCodes) {
				return null;
			}

			@Override
			public List<String> getRequirementsByIds(String reqIds) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> findRequirementField(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<String> getsystems(Long id) {
				// TODO Auto-generated method stub
				return null;
			}


			/*@Override
			public List<TblRequirementInfo> getAllRequirement(String requirmentJson, Integer pageIndex, Integer pageSize) {
				return null;
			}			
			@Override
			public int getCountRequirement(String requirmentJson) {
				return 0;
			}*/
			/*@Override
			public Map<String, Object> findRequirementById(Long id,Long parentId) {				
				return handleFeignError(cause);
			}*/
			/*@Override
			public List<TblDataDic> getDataDicList(String datadictype) {
				return null;
			}*/
						
		};
	}	
	
}
