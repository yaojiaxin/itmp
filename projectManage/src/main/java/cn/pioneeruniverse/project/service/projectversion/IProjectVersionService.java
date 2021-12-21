package cn.pioneeruniverse.project.service.projectversion;

import cn.pioneeruniverse.project.entity.TblSystemVersion;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IProjectVersionService {

     Map<String, Object> selectSystemByProjectId(Long projectId);

     Map<String, Object> getProjectVersionByCon(Long projectId);

     void insertVersion(TblSystemVersion systemVersion, HttpServletRequest request);

     Map<String, Object> getProjectVersionBySystemVersion(TblSystemVersion systemVersion);

     void updateVersion(TblSystemVersion systemVersion, HttpServletRequest request);

     void closeOrOpenSystemVersion(TblSystemVersion systemVersion, HttpServletRequest request);
}
