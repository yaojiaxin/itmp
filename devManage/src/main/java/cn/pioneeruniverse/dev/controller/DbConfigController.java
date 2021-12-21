package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.CommonUtils;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.entity.TblServerInfo;
import cn.pioneeruniverse.dev.entity.TblSystemDbConfig;
import cn.pioneeruniverse.dev.entity.TblSystemDbConfigVo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.service.dbconfig.IDbConfigService;
import cn.pioneeruniverse.dev.service.serverinfo.IServerInfoService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author weiji
 *
 */
@RestController
@RequestMapping("dbConfig")
public class DbConfigController extends BaseController{
	@Autowired
	private IDbConfigService dbConfigService;
	@Autowired
	private RedisUtils redisUtils;





	/**
	 *  条件查询所有数据库信息带分页(jqGrid)
	 * @param tblSystemDbConfigVo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getAllDbConfig", method = RequestMethod.POST)
	public JqGridPage<TblSystemDbConfigVo> getAllDbConfig(TblSystemDbConfigVo tblSystemDbConfigVo, HttpServletRequest request, HttpServletResponse response) {
		JqGridPage<TblSystemDbConfigVo> jqGridPage = null;
		try {
			jqGridPage=dbConfigService.findDbConfigListPage(new JqGridPage<>(request, response),tblSystemDbConfigVo,request);
		} catch (Exception e) {
			super.handleException(e, e.getMessage());
		}
		return jqGridPage;
	}



	/**
	 * 查询当前登陆用户所在项目组系统
	 * @return List<TblSystemInfo>
	 */
    @RequestMapping(value = "getSystemByUserId", method = RequestMethod.POST)
	public  Map<String, Object>  getSystemByUserId(HttpServletRequest request){
		Map<String, Object> result = new HashMap<>();

		try {
			List<TblSystemInfo> systemInfos=dbConfigService.getSystemByUserId(request);
            result.put("systemInfos",systemInfos);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);

        }catch (Exception e){
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "查询失败");
            super.handleException(e, e.getMessage());
        }

		return result;



	}
	/**
	 * 查询系统模块和系统环境
	 * @return  Map<String, Object>
	 */
    @RequestMapping(value = "getEnvAndModuleBySystemId", method = RequestMethod.POST)
	public  Map<String, Object>  getEnvAndModuleBySystemId(long systemId,HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        try {
            result =dbConfigService.getEnvAndModuleBySystemId(systemId);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        }catch (Exception e){
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "查询失败");
            super.handleException(e, e.getMessage());
        }
		return  result;


	}

	/**
	 * 查询系统模块和系统环境
	 * @return  Map<String, Object>
	 */
    @RequestMapping(value = "addDbConfig", method = RequestMethod.POST)
	public  Map<String, Object>   addDbConfig(TblSystemDbConfig tblSystemDbConfig,HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        try {
        	if(tblSystemDbConfig.getUrl()!=null){
        		String url=tblSystemDbConfig.getUrl();
        		url=url.replaceAll("&amp;","&");
        		tblSystemDbConfig.setUrl(url);
			}
            dbConfigService.addDbConfig(tblSystemDbConfig);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        }catch (Exception e){
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "查询失败");
            super.handleException(e, e.getMessage());
        }
        return  result;



	}


	/**
	 * 删除记录
	 * @return  Map<String, Object>
	 */
    @RequestMapping(value = "deleteDbConfig", method = RequestMethod.POST)
	public   Map<String, Object>  deleteDbConfig(long id,HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        try {
            dbConfigService.deleteDbConfig(id);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);

        }catch (Exception e){
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "删除失败");
            super.handleException(e, e.getMessage());
        }
        return  result;

	}

	/**
	 * 更新记录
	 * @return  Map<String, Object>
	 */
    @RequestMapping(value = "updateDbConfig", method = RequestMethod.POST)
	public   Map<String, Object>   updateDbConfig(TblSystemDbConfig tblSystemDbConfig,HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        try {
			if(tblSystemDbConfig.getUrl()!=null){
				String url=tblSystemDbConfig.getUrl();
				url=url.replaceAll("&amp;","&");
				tblSystemDbConfig.setUrl(url);
			}
            dbConfigService.updateDbConfig(tblSystemDbConfig);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        }catch (Exception e){
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "更新失败");
            super.handleException(e, e.getMessage());
        }
        return  result;



	}

}
