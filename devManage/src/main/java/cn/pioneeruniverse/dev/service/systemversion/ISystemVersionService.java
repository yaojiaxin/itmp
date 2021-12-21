package cn.pioneeruniverse.dev.service.systemversion;

import cn.pioneeruniverse.dev.entity.TblSystemVersion;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public interface ISystemVersionService {
     Map<String, Object> getSystemVersionByCon(Long systemId,Integer status);

     Integer insertVersion(TblSystemVersion systemVersion, HttpServletRequest request);

     Integer updateVersion(TblSystemVersion systemVersion, HttpServletRequest request);

     Integer closeOrOpenSystemVersion (TblSystemVersion systemVersion, HttpServletRequest request);

     TblSystemVersion getSystemVersionBySystemVersionId(Long systemVersionId);
}
