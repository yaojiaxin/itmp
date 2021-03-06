package cn.pioneeruniverse.project.feignFallback;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.project.feignInterface.TestTaskInterface;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class TestTaskFallback  extends BaseController implements FallbackFactory<TestTaskInterface>{

    @Override
    public TestTaskInterface create(Throwable cause) {
        return new TestTaskInterface(){

            @Override
            public Map<String, Object> changeCancelStatus(Long requirementId) {
                return handleFeignError(cause);
            }

            @Override
            public Map<String, Object> cancelStatusReqFeature(Long reqFeatureId) {
                return null;
            }
        };
    }

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
}
