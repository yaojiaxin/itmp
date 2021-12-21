package cn.pioneeruniverse.dev.feignFallback.defect;


import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.dev.feignInterface.defect.DefectInterface;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author:liushan
 * Date: 2018/12/10 下午 5:18
 */
@Component
public class DefectFallback implements FallbackFactory<DefectInterface> {
    private static Logger logger = LoggerFactory.getLogger(DefectFallback.class);



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
    public DefectInterface create(Throwable cause) {
        return new DefectInterface() {
           /* @Override
            public Map<String, Object> getAllSelect() {
                return handleFeignError(cause);
            }*/

        };

    }

}
