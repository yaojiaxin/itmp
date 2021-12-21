package cn.pioneeruniverse.project.service.projectinfo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.pioneeruniverse.project.dao.mybatis.ProjectInfoMapper;
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.service.projectinfo.ProjectInfoService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public List<TblProjectInfo> selectProjects(TblProjectInfo tblProjectInfo, Integer page, Integer rows) {
//		PageInfo<TblProjectInfo>
//		PageHelper.startPage(Integer.getInteger(currentPage), Integer.getInteger(pageSize));   ,currentPage,pageSize
        HashMap<String, Object> map = new HashMap<>();
        Integer start = (page - 1) * rows;
        map.put("start", start);
        map.put("pageSize", rows);
        map.put("tblProjectInfo", tblProjectInfo);
        List<TblProjectInfo> list = projectInfoMapper.selectProjects(map);

//		PageInfo<TblProjectInfo> info = new PageInfo<TblProjectInfo>(list);

        return list;
    }

    @Override
    public void insertProject(TblProjectInfo tblProjectInfo) {
        // TODO Auto-generated method stub
        this.projectInfoMapper.insertProject(tblProjectInfo);
    }

    @Override
    public void deleteProjectById(Long id) {
        // TODO Auto-generated method stub
        this.projectInfoMapper.deleteProjectById(id);
    }

    @Override
    public TblProjectInfo selectProjectById(Long id) {
        // TODO Auto-generated method stub
        return this.projectInfoMapper.selectProjectById(id);

    }

    @Override
    public void updateProject(TblProjectInfo tblProjectInfo) {
        // TODO Auto-generated method stub
        this.projectInfoMapper.updateProject(tblProjectInfo);
    }

    @Override
    public Integer selectCount() {
        // TODO Auto-generated method stub
        return projectInfoMapper.selectCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblProjectInfo> getAllProjectInfo() {
        return projectInfoMapper.getAllProject();
    }

    /**
     * 根据当人用户 查询所处的项目
     * 系统管理员显示 所有项目
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> getAllProject(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        List<TblProjectInfo> projects = new ArrayList<>();
        if (new CommonUtils().currentUserWithAdmin(request) == false) {
            projects = projectInfoMapper.getAllProjectByCurrentUserId(CommonUtil.getCurrentUserId(request));
        } else {
            projects = projectInfoMapper.getAllProject();
        }
        map.put("projects", projects);
        return map;
    }


}
