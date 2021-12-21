package cn.pioneeruniverse.common.sonar.client;


import javax.ws.rs.core.MultivaluedMap;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import cn.pioneeruniverse.common.sonar.bean.SonarQubeException;
import cn.pioneeruniverse.common.sonar.constants.SonarAPIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CeApi extends AbstractApi {
    private final static Logger log = LoggerFactory.getLogger(CeApi.class);
    public CeApi(String url, HTTPBasicAuthFilter auth) {
        super(url, auth);
    }
//    /**
//          查询 项目的analysis_uuid
//     * @param  projectUUid(project表uuid)
//     * @return 项目analysis_uuid
//     * @throws SonarQubeException
//     */
//    public String  search(String projectUUid) throws SonarQubeException{
//
//         MultivaluedMap<String, String> params = new MultivaluedMapImpl();
//         params.add("id", projectUUid);
//
//
//         ClientResponse clientResponse = RestApi.get(SonarAPIConstants.SONAR_URL,
//            		 SonarAPIConstants.SONAR_CE_TASK,getAuth(),params);
//         String result = clientResponse.getEntity(String.class);
//
//
//         return result;
//
//    }

    public String  getStatus(String projectKey,String param) throws SonarQubeException{

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("componentId", projectKey);
        params.add("status", param);
        ClientResponse clientResponse = RestApi.get(SonarAPIConstants.SONAR_URL,
                SonarAPIConstants.SONAR_CE_ACTIVITY,getAuth(),params);
        String result = clientResponse.getEntity(String.class);
        return result;
    }



    public String  getComponentId(String projectKey) throws SonarQubeException{
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("component", projectKey);
        ClientResponse clientResponse = RestApi.get(SonarAPIConstants.SONAR_URL,
                SonarAPIConstants.SONAR_CE_COMPONENT,getAuth(),params);
        String result = clientResponse.getEntity(String.class);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String current = jsonObject.getString("current");
        String  componentId=JSONObject.parseObject(current).getString("componentId");

        return componentId;

  }

         /**
          * 查询项目是否正在分析
          * @param  projectKey url param
          * @return boolean
          * @throws SonarQubeException
          */
    public boolean  isProgress(String projectKey,String url,String param) throws SonarQubeException{
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("component", projectKey);
        ClientResponse clientResponse = RestApi.get(url,
                SonarAPIConstants.SONAR_CE_COMPONENT,getAuth(),params);
        String result = clientResponse.getEntity(String.class);
        log.info("sonar数据="+result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String current = jsonObject.getString("current");
        String componentId="";
        if(current==null){
            String queue = jsonObject.getString("queue");
            JSONArray jArrayQueue = JSONObject.parseArray(queue);
            JSONObject jsobjectQueue = jArrayQueue.getJSONObject(0);
            componentId=jsobjectQueue.getString("componentId");
            log.info("queue数据="+componentId);

        }else {
            componentId = JSONObject.parseObject(current).getString("componentId");
            log.info("current数据="+componentId);
        }
        MultivaluedMap<String, String> params2 = new MultivaluedMapImpl();
        params2.add("componentId", componentId);
        params2.add("status",param);
        // params2.add("status", "IN_PROGRESS");
        ClientResponse clientResponse2 = RestApi.get(url,
                SonarAPIConstants.SONAR_CE_ACTIVITY,getAuth(),params2);
        String result2 = clientResponse2.getEntity(String.class);
        JSONObject jsonObject2 = JSONObject.parseObject(result2);
        String tasks = jsonObject2.getString("tasks");
        log.info("sonar数据2="+result2);
        JSONArray jArray = JSONObject.parseArray(tasks);
        if(jArray.size()==0){
            return false;
        }else{
            return  true;
        }
    }


}
