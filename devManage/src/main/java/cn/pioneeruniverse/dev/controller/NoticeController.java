package cn.pioneeruniverse.dev.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.service.projectNotice.IProjectNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.dev.entity.TblNoticeInfo;
import cn.pioneeruniverse.dev.service.notice.INoticeService;

@RestController
@RequestMapping("notice")
public class NoticeController extends BaseController {

	@Autowired
	private INoticeService iNoticeService;

    @Autowired
    private IProjectNoticeService iProjectNoticeService;


    @RequestMapping(value="selectNoticeById",method=RequestMethod.POST)
	public Map<String,Object> selectNoticeById(Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			TblNoticeInfo notice = iNoticeService.selectNoticeById(id);
			result.put("data", notice);
		}catch(Exception e){
			return this.handleException(e, "获取公告详情失败");
		}
		return result;
	}
	
	@RequestMapping(value="getAllNotice",method=RequestMethod.POST)
	public Map<String,Object> getAllNotice(HttpServletRequest request, TblNoticeInfo notice, Integer page, Integer rows,Integer type,String programId) {

    	Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			if (StringUtil.isNotEmpty(notice.getCreateDateStr())) {
				String[] dateArr = notice.getCreateDateStr().split(" - ");
				Date date = DateUtil.parseDate(dateArr[0].trim());
				notice.setCreateStartDate(date);
				date = DateUtil.parseDate(dateArr[1].trim() + " 23:59:59", DateUtil.fullFormat);
				notice.setCreateEndDate(date);
			}
			if (StringUtil.isNotEmpty(notice.getValidDateStr())) {
				String[] dateArr = notice.getValidDateStr().split(" - ");
				Date date = DateUtil.parseDate(dateArr[0].trim());
				notice.setStartTime(date);
				date = DateUtil.parseDate(dateArr[1].trim());
				notice.setEndTime(date);
			}
			result = iNoticeService.getAllNotice(request, notice, page, rows, type, programId);
		}catch(Exception e){
			return this.handleException(e, "获取公告详情失败");
		}
		return result;
	}
	@RequestMapping(value="insertNotice",method=RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
	public Map<String,Object> insertNotice(HttpServletRequest request,@RequestBody String noticeJson) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			TblNoticeInfo notice = new TblNoticeInfo();
			if (StringUtil.isNotEmpty(noticeJson)) {
				notice = JSONObject.parseObject(noticeJson, TblNoticeInfo.class);
				if (StringUtil.isNotEmpty(notice.getNoticeContent()) && notice.getNoticeType() != null
						&& notice.getStartTime() != null && notice.getEndTime() != null) {
					iNoticeService.insertNotice(request, notice);
                    if(notice.getNoticeType() == 2){
                        Long number = iProjectNoticeService.selectNoticeInfoByID(notice);
                        notice.setId(number);
                        iProjectNoticeService.insertProjectNotice(notice);
                        if (notice.getProjectIds() != null && notice.getProjectIds() != ""){
                            String str = notice.getProjectIds();
                            String[] projectType = str.split(",");
                            for (int i =0;i<projectType.length;i++){
                                notice.setProjectIds(projectType[i]);
                                iProjectNoticeService.insertProjectNotice(notice);
                            }
                        }
                    }
                   
				} else {
					result.put("status", Constants.ITMP_RETURN_FAILURE);
					result.put("errorMessage", "新增公告失败");
				}
			}
		}catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return this.handleException(e, "新增公告失败");
		}
		return result;
	}
	
	@RequestMapping(value="updateNotice",method=RequestMethod.POST)
	public Map<String,Object> updateNotice(HttpServletRequest request,@RequestBody String noticeJson) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			TblNoticeInfo notice = new TblNoticeInfo();
			if (StringUtil.isNotEmpty(noticeJson)) {
				notice = JSONObject.parseObject(noticeJson, TblNoticeInfo.class);
				if (StringUtil.isNotEmpty(notice.getNoticeContent()) && notice.getNoticeType() != null
						&& notice.getStartTime() != null && notice.getEndTime() != null && notice.getId() != null) {
					
					iNoticeService.updateNotice(request, notice);
				} else {
					result.put("status", Constants.ITMP_RETURN_FAILURE);
					result.put("errorMessage", "更新公告失败");
				}
			}
		}catch(Exception e){
			return this.handleException(e, "更新公告失败");
		}
		return result;
	}
	
	@RequestMapping(value="deleteNotice",method=RequestMethod.POST)
	public Map<String,Object> deleteNotice(HttpServletRequest request, Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			if (id != null) {
				TblNoticeInfo notice = new TblNoticeInfo();
				notice.setId(id);
				iNoticeService.deleteNotice(request, notice);
			} else {
				result.put("status", Constants.ITMP_RETURN_FAILURE);
				result.put("errorMessage", "删除公告失败");
			}
		}catch(Exception e){
			return this.handleException(e, "删除公告失败");
		}
		return result;
	}
	
	@RequestMapping(value="deleteNoticeList",method=RequestMethod.POST)
	public Map<String,Object> deleteNoticeList(HttpServletRequest request, String ids) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			if (StringUtil.isNotEmpty(ids)) {
				iNoticeService.deleteNoticeList(request, ids);
			} else {
				result.put("status", Constants.ITMP_RETURN_FAILURE);
				result.put("errorMessage", "删除公告失败");
			}
		}catch(Exception e){
			return this.handleException(e, "删除公告失败");
		}
		return result;
	}
	
	/**
	 * 获取时间内有效的系统公告列表
	 * @param request
	 * @param
	 * @return
	 */
	@RequestMapping(value="getAllValidSystemNotice",method=RequestMethod.POST)
	public Map<String,Object> getAllValidSystemNotice(HttpServletRequest request) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			
			List<TblNoticeInfo> list = iNoticeService.getAllValidSystemNotice(request);
			result.put("list",list);
		}catch(Exception e){
			return this.handleException(e, "获取展示公告失败");
		}
		return result;
	}
	
	/**
	 * 获取时间内有效的项目公告列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getAllValidProjectNotice",method=RequestMethod.POST)
	public Map<String,Object> getAllValidProjectNotice(HttpServletRequest request) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			
			List<TblNoticeInfo> list = iNoticeService.getAllValidProjectNotice(request);
			result.put("list",list);
		}catch(Exception e){
			return this.handleException(e, "获取展示公告失败");
		}
		return result;
	}
	

}
