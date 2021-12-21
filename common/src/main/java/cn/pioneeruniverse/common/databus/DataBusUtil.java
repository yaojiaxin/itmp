package cn.pioneeruniverse.common.databus;

import com.ccic.databus.client.publish.DataBusPublishClient;
import com.ccic.databus.common.exception.DataBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataBusUtil {

    private static Boolean integration;
    private static String databusSource;
    private static String databusccFlag;

    private final static Logger log = LoggerFactory.getLogger(DataBusUtil.class);

    @Value("${system.integration.open}")
    public void setIntegration(Boolean param) {
        integration = param;
    }
    @Value("${databus.source}")
    public void setDatabusSource(String param) {
        databusSource = param;
    }
    @Value("${databuscc.flag}")
    public void setDatabusccFlag(String param) {
       databusccFlag = param;
    }

    /**
     * 调用dataBus公共方法
     *
     * @param databusccName     dataName
     * @param businessNumbe     业务编号
     * @param result            报文
     *
     */
    public static void send(String databusccName, String businessNumbe, String result) {
        DataBusUtil dataBus = new DataBusUtil();
        if(dataBus.integration == true){
            DataBusPublishClient client = DataBusPublishClientSingleton.getInstance(dataBus.databusccFlag);
            log.info("databus调用开始，databus地址为:{}", databusccName);
            try {
                client.send(databusccName, dataBus.databusSource, businessNumbe,result);
                log.info("databus.source为:{}", dataBus.databusSource);
                log.info("调用结束");
            }catch (DataBusException e){
                log.error("databus调用异常，databus地址为:{}", e);
                log.error(e.getMessage(), e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

}
