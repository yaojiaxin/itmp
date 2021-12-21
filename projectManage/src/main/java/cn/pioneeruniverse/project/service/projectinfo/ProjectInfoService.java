package cn.pioneeruniverse.project.service.projectinfo;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;

import cn.pioneeruniverse.project.entity.TblProjectInfo;

import javax.servlet.http.HttpServletRequest;

public interface ProjectInfoService {

    List<TblProjectInfo> selectProjects(TblProjectInfo tblProjectInfo, Integer page, Integer rows);

    void insertProject(TblProjectInfo tblProjectInfo);

    void deleteProjectById(Long id);

    TblProjectInfo selectProjectById(Long id);

    void updateProject(TblProjectInfo tblProjectInfo);

    Integer selectCount();

    List<TblProjectInfo> getAllProjectInfo();

    Map<String, Object> getAllProject(HttpServletRequest request);
}
