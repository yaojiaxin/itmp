package cn.pioneeruniverse.dev.service.sprint.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.dao.mybatis.TblSprintInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemInfoMapper;
import cn.pioneeruniverse.dev.entity.TblSprintInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.service.sprint.SprintInfoService;

@Service
public class SprintInfoServiceImpl implements SprintInfoService {
	
	@Autowired
	private TblSystemInfoMapper tblSystemInfoMapper;
	
	@Autowired
	private TblSprintInfoMapper tblSprintInfoMapper;

	//获取系统下拉框的数据
	@Override
	@Transactional(readOnly=true)
	public List<TblSystemInfo> getAllSystem() {
		return tblSystemInfoMapper.selectAll();
	}

	//冲刺管理列表显示
	@Override
	@Transactional(readOnly=true)
	public List<TblSprintInfo> getSprintInfos(String sprintName,Long uid,String systemIds, Integer validStatus,List<String> roleCodes,Integer page, Integer rows) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("sprintName", sprintName);
		map.put("systemIds", systemIds);
		map.put("uid", uid);
		map.put("validStatus", validStatus);
		map.put("start", (page-1)*rows);
		map.put("rows", rows);
		List<TblSprintInfo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			 list = tblSprintInfoMapper.getSprintInfos(map);
		}else {
			 list = tblSprintInfoMapper.getSprintInfoCondition(map);
		}
		
		/*for (TblSprintInfo tblSprintInfo : list) {
			tblSprintInfo.setSystemName(tblSystemInfoMapper.getSystemName(tblSprintInfo.getSystemId()));
			
		}*/
		return list;
	}
	
	@Override
	public Integer getSprintInfosCount(String sprintName, Long uid, String systemIds, Integer validStatus,List<String> roleCodes) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("sprintName", sprintName);
		map.put("systemIds", systemIds);
		map.put("uid", uid);
		map.put("validStatus", validStatus);
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			return tblSprintInfoMapper.getSprintInfosCount(map);
		}else {
			return tblSprintInfoMapper.getSprintInfosCountCondition(map);
		}
		
	}

	//删除冲刺任务
	@Override
	@Transactional(readOnly=false)
	public void deleteSprintInfo(HttpServletRequest request, String sprintIdList) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		map.put("updateUser", CommonUtil.getCurrentUserId(request));
		map.put("updateTime", new Timestamp(new Date().getTime()));
        String[] split = sprintIdList.split(",");
        for (int i=0;i<split.length;i++){
            map.put("id", split[i]);
            tblSprintInfoMapper.deleteSprintInfo(map);
        }

	}

	//新增冲刺任务
	@Override
	@Transactional(readOnly=false)
	public void addSprintInfo(TblSprintInfo sprintInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		sprintInfo.setStatus(1);
		sprintInfo.setCreateBy(CommonUtil.getCurrentUserId(request));
		sprintInfo.setCreateDate(new Timestamp(new Date().getTime()));
		sprintInfo.setValidStatus(1);
		if (sprintInfo.getSystemList() != null){
            String[] split = sprintInfo.getSystemList().split(",");
            for (int i=0;i<split.length;i++){
                sprintInfo.setSystemId(Long.parseLong(split[i]));
                tblSprintInfoMapper.addSprintInfo(sprintInfo);
            }

        }

	}

	//编辑数据回显
	@Override
	@Transactional(readOnly=true)
	public TblSprintInfo getSprintInfoById(Long id) {
		// TODO Auto-generated method stub
		return tblSprintInfoMapper.selectByPrimaryKey(id);
	}

	//编辑任务
	@Override
	@Transactional(readOnly=false)
	public void updateSprintInfo(TblSprintInfo sprintInfo, HttpServletRequest request) {
		// TODO Auto-generated method stub
		sprintInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		sprintInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
        String[] split = sprintInfo.getSprintIdList().split(",");
        String[] systemIdListSplit = sprintInfo.getSystemIdList().split(",");
        for (int i =0;i<split.length;i++){
            sprintInfo.setId(Long.parseLong(split[i]));
            sprintInfo.setSystemId(Long.parseLong(systemIdListSplit[i]));
            tblSprintInfoMapper.updateByPrimaryKeySelective(sprintInfo);
        }

	}

	//关闭任务
	@Override
	@Transactional(readOnly=false)
	public void closeSprint(String id, HttpServletRequest request) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		map.put("updateUser", CommonUtil.getCurrentUserId(request));
		map.put("updateTime", new Timestamp(new Date().getTime()));
        String[] split = id.split(",");
        for (int i=0;i<split.length;i++){
            map.put("id", split[i]);
            tblSprintInfoMapper.closeSprint(map);
        }
	}

	@Override
	public List<TblSprintInfo> getAllSprints(String sprintName, Long uid, String systemName, List<String> roleCodes,
			Integer pageNum, Integer pageSize) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("sprintName", sprintName);
		map.put("systemName", systemName);
		map.put("uid", uid);
		map.put("start", (pageNum-1)*pageSize);
		map.put("rows", pageSize);
		List<TblSprintInfo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			 list = tblSprintInfoMapper.getSprintInfos(map);
		}else {
			 list = tblSprintInfoMapper.getSprintInfoCondition(map);
		}
		
		return list;
	}

	@Override
	public Integer getAllSprinsCount(String sprintName, Long uid, String systemName, List<String> roleCodes) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("sprintName", sprintName);
		map.put("systemName", systemName);
		map.put("uid", uid);
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			return tblSprintInfoMapper.getSprintInfosCount(map);
		}else {
			return tblSprintInfoMapper.getSprintInfosCountCondition(map);
		}
	}

	//开启任务
	@Override
	@Transactional(readOnly=false)
	public void openSprint(String id, HttpServletRequest request) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		map.put("updateUser", CommonUtil.getCurrentUserId(request));
		map.put("updateTime", new Timestamp(new Date().getTime()));
        String[] split = id.split(",");
        for (int i=0;i<split.length;i++){
            map.put("id", split[i]);
            tblSprintInfoMapper.openSprint(map);
        }

	}

	/**
	 *  根据冲刺id获取该冲刺相关信息
	 * @param sprintIdLists
	 * @return
	 */
	@Override
	public TblSprintInfo selectSprintInfoById(@Param("sprintIdLists") List<Object> sprintIdLists) {
		return tblSprintInfoMapper.selectSprintInfoById(sprintIdLists);
	}

}
