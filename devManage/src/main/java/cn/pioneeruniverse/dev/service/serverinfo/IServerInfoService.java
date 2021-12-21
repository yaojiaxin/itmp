package cn.pioneeruniverse.dev.service.serverinfo;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.dev.entity.TblServerInfo;

public interface IServerInfoService {
	TblServerInfo selectById(Long id);
	
	JqGridPage<TblServerInfo> findServerInfo(JqGridPage<TblServerInfo> systemInfoJqGridPage, TblServerInfo tblServerInfo,HttpServletRequest httpServletRequest);
	
	Map<String, Object> findServerInfo2(String hostName,String IP,String haveHost, Integer page,Integer rows,HttpServletRequest request);
	
	void insertServerInfo(TblServerInfo tblServerInfo,HttpServletRequest request);
	
	void updateServerInfoById(TblServerInfo tblServerInfo,HttpServletRequest request);
	
	void deleteServerInfoById(Long id);
	
	void updateServerInfoStatusById(Long id,Integer status,HttpServletRequest request);

    Map<String, Object> findServerInfo3(String hostName, String ip, String haveHost, Integer page, Integer rows,Long systemId, HttpServletRequest request);
}
