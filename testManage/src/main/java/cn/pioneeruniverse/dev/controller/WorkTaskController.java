package cn.pioneeruniverse.dev.controller;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;
import cn.pioneeruniverse.common.utils.ExportExcel;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.dev.entity.TblRequirementFeatureRemarkAttachement;
import cn.pioneeruniverse.dev.entity.TblRequirementInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.entity.TblTestTask;
import cn.pioneeruniverse.dev.entity.TblTestTaskAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskLog;
import cn.pioneeruniverse.dev.entity.TblTestTaskLogAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskRemark;
import cn.pioneeruniverse.dev.entity.TblTestTaskRemarkAttachement;
import cn.pioneeruniverse.dev.service.workTask.workTaskRemark;
import cn.pioneeruniverse.dev.service.workTask.workTaskService;
import cn.pioneeruniverse.dev.entity.TblUserInfo;

@RestController
@RequestMapping("worktask")
public class WorkTaskController extends BaseController {
	@Autowired
	private workTaskService workTaskService;
	@Autowired
	private workTaskRemark workTaskRemark;
	@Autowired
	private S3Util s3Util;
	private Long currentUserId;

	/**
	 * ???????????????????????????(jqgrid)
	 * 
	 * @param workTask
	 * @param rows
	 * @param page
	 * @param request
	 * @return
	 */
	@PostMapping("getAllWorktask")
	public Map<String, Object> toConstruction(String workTask,String filters,  Integer rows, Integer page, HttpServletRequest request,
			String sidx, String sord) {
		Map<String, Object> result = new HashMap<String, Object>();
		TblTestTask tblDevTask = new TblTestTask();
		try {
			Long Userd = CommonUtil.getCurrentUserId(request);
			if (StringUtils.isNotBlank(workTask)) {
				tblDevTask = JSONObject.parseObject(workTask, TblTestTask.class);
			}
			tblDevTask.setSidx(sidx);
			tblDevTask.setSord(sord);
			tblDevTask.setId(Userd);
			result = workTaskService.getTestTask(tblDevTask,filters, page, rows,request);

		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * ???????????????????????????(bootstrap)
	 * 
	 * @param tblTestTask
	 * @param rows
	 * @param page
	 * @param request
	 * @return
	 */
	@PostMapping("getAllWorktask2")
	public Map<String, Object> getAllWorktask2(TblTestTask tblTestTask, Integer rows, Integer page,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		TblTestTask tblDevTask = new TblTestTask();
		try {
			Long Userd = CommonUtil.getCurrentUserId(request);
			if (tblTestTask != null) {
				tblDevTask.setId(Userd);
				result = workTaskService.getTestTask2(tblTestTask, page, rows);
			}
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	/**
	 * ??????????????????id??????????????????
	 * 
	 * @param testTaskId
	 * @return
	 */
	@RequestMapping(value = "getWorkTaskByTestTaskId", method = RequestMethod.POST)
	public Map<String, Object> getWorkTaskByTestTaskId( Long featureId,String nameOrNumber,String createBy,String testTaskId) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<TblTestTask> list = workTaskService.selectTestTaskByRequirementFeatureId(featureId,nameOrNumber,createBy,testTaskId);
			result.put("rows", list);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}

	// ????????????
	@RequestMapping(value = "getAllTestUser")
	public List<TblTestTask> getAllTestUser(HttpServletRequest request) {
		Long testUserId = CommonUtil.getCurrentUserId(request);
		 List<TblTestTask> list = workTaskService.getAllTestUser(testUserId);
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param tblTestTask
	 * @param pageNumber
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getAllFeatureTask")
	public Map<String, Object> getAllFeatureTask(TblTestTask tblTestTask, Integer pageNumber, Integer pageSize,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);

		try {
			List<Map<String, Object>> list = workTaskService.getAllFeatureTask(tblTestTask, pageNumber, pageSize);
			List<Map<String, Object>> list2 = workTaskService.getAllFeatureTask(tblTestTask, null, null);
			result.put("rows", list);
			result.put("total", list2.size());
		} catch (Exception e) {

			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
			return handleException(e, "????????????????????????");
		}
		return result;
	}

	/**
	 * ???????????????(1,2)????????????
	 * 
	 * @param tblTestTask
	 * @param pageNumber
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getAllFeature")
	public Map<String, Object> getAllFeature(TblTestTask tblTestTask, Integer pageNumber, Integer pageSize,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		Long userdId =CommonUtil.getCurrentUserId(request);
		try {
			List<Map<String, Object>> list = workTaskService.getAllFeature(tblTestTask,userdId, pageNumber, pageSize,request);
			List<Map<String, Object>> list2 = workTaskService.getAllFeature(tblTestTask,userdId, null, null,request);
			result.put("rows", list);
			result.put("total", list2.size());
		} catch (Exception e) {

			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
			return handleException(e, "????????????????????????");
		}
		return result;
	}

	// ??????????????????
	@RequestMapping(value = "addTestTask", method = RequestMethod.POST)
	public Map addTestTask(String objStr, String attachFiles, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long Userid = CommonUtil.getCurrentUserId(request);
			String UserName = CommonUtil.getCurrentUserName(request);
			String UserAccount = CommonUtil.getCurrentUserAccount(request);
			workTaskService.addtestTask(objStr, attachFiles, Userid, request, UserAccount);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;

	}

	// ?????? noPermission
	@RequestMapping(value = "updateTestTask", method = RequestMethod.POST)
	public Map updateTestTask(String obj, String attachFiles, String deleteFiles, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblTestTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteFiles,
					JsonUtil.createCollectionType(ArrayList.class, TblTestTaskAttachement.class));
			if (tblDevTaskAttachement.size() > 0) {
				workTaskRemark.updateNo(tblDevTaskAttachement, request);
			}
			TblTestTask TestTask = JSON.parseObject(obj, TblTestTask.class);
			Long Userid = CommonUtil.getCurrentUserId(request);
			//??????????????????id????????????????????????????????????????????????????????????????????????????????????
			List<Long> userIds = workTaskService.selectUserIdsById(TestTask.getId());
			if ((TestTask.getOldTestUserId() != null && TestTask.getOldTestUserId().longValue() != Userid.longValue())
					&& (TestTask.getCreateBy() != null && TestTask.getCreateBy().longValue() != Userid.longValue())
					&& (TestTask.getTaskAssignUserId() != null && TestTask.getTaskAssignUserId().longValue() != Userid.longValue())
					&&(userIds != null && !userIds.contains(Userid))) {
				map.put("status", "noPermission");
				return map;
			}
			workTaskService.updateTestTask(obj, attachFiles, deleteFiles, Userid, request);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;

	}

	// ??????????????????
	@RequestMapping(value = "getEditDevTask", method = RequestMethod.POST)
	public Map<String, Object> getEditDevTask(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<TblTestTaskAttachement> list = workTaskRemark.findAttachement(Long.parseLong(id));
			result = workTaskService.getEditTestTask(id);
			result.put("attachements", list);
		} catch (Exception e) {
			result.put("status", "2");
		}
		return result;
	}

	/* ???????????? */
	@RequestMapping(value = "delFile", method = RequestMethod.POST)
	public Map delFile(String id, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (!id.equals("") || !id.equals(null)) {

				// workTaskRemark.updateNo(id,request);
				result.put("status", "success");
			}
		} catch (Exception e) {
			result.put("status", "fail");
		}

		return result;

	}

	// ????????????
	@RequestMapping(value = "getSeeDetail", method = RequestMethod.POST)
	public Map<String, Object> getSeeDetail(String id) {
		List<TblTestTaskRemark> list =  new ArrayList<>();
		List<TblTestTaskLogAttachement> listLogAttachement =  new ArrayList<>();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = workTaskService.getSeeDetail(id);
			list = workTaskRemark.selectRemark(id);
			List<Long> idRemark = new ArrayList<Long>(list.size());
			List<TblTestTaskRemarkAttachement> list2 =  new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				idRemark.add(list.get(i).getId());
			}
			if (idRemark.size() > 0) {
				list2 = workTaskRemark.findTaskRemarkAttchement(idRemark);
			}
			List<TblTestTaskLog> logsList = workTaskRemark.findLogList(Long.parseLong(id));
			List<Long> idLog = new ArrayList<Long>(list.size());
			for (int j = 0; j < logsList.size(); j++) {
				idLog.add(logsList.get(j).getId());
			}
			if (idLog.size() > 0) {
				listLogAttachement = workTaskRemark.findLogAttachement(idLog);
			}
			List<TblTestTaskAttachement> listfile = workTaskRemark.findAttachement(Long.parseLong(id));
			result.put("attachements", listfile);
			result.put("logs", logsList);
			result.put("Attchement", list2);
			result.put("rmark", list);
			result.put("logAttachement", listLogAttachement);
		} catch (Exception e) {
			return super.handleException(e, "??????????????????");
		}

		return result;

	}

	// ?????????
	@RequestMapping(value = "DHandleTest", method = RequestMethod.POST)
	public Map DHandleDev(String handle, String DHattachFiles, String deleteFiles, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblTestTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteFiles,
					JsonUtil.createCollectionType(ArrayList.class, TblTestTaskAttachement.class));
			if (tblDevTaskAttachement.size() > 0) {
				workTaskRemark.updateNo(tblDevTaskAttachement, request);
			}
			TblTestTask tblTestTask = JSON.parseObject(handle, TblTestTask.class);
			currentUserId = CommonUtil.getCurrentUserId(request);
			//??????????????????id????????????????????????????????????????????????????????????????????????????????????
			List<Long> userIds = workTaskService.selectUserIdsById(tblTestTask.getId());
			if ((tblTestTask.getOldTestUserId() != null
					&& tblTestTask.getOldTestUserId().longValue() != currentUserId.longValue())
					&& (tblTestTask.getCreateBy() != null && tblTestTask.getCreateBy().longValue() != currentUserId.longValue())
					&& (tblTestTask.getTaskAssignUserId() != null && tblTestTask.getTaskAssignUserId().longValue() != currentUserId.longValue())
					&&(userIds != null && !userIds.contains(currentUserId))) {
				map.put("status", "noPermission");
				return map;
			}
			workTaskService.DHandle(handle, DHattachFiles, deleteFiles, request);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;
	}
	@RequestMapping(value = "examineHandle", method = RequestMethod.POST)
	public Map examineHandle(String handle, String DHattachFiles, String deleteFiles, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblTestTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteFiles,
					JsonUtil.createCollectionType(ArrayList.class, TblTestTaskAttachement.class));
			if (tblDevTaskAttachement.size() > 0) {
				workTaskRemark.updateNo(tblDevTaskAttachement, request);
			}
			TblTestTask tblTestTask = JSON.parseObject(handle, TblTestTask.class);
			currentUserId = CommonUtil.getCurrentUserId(request);
			//??????????????????id????????????????????????????????????????????????????????????????????????????????????
			List<Long> userIds = workTaskService.selectUserIdsById(tblTestTask.getId());
			if ((tblTestTask.getOldTestUserId() != null
					&& tblTestTask.getOldTestUserId().longValue() != currentUserId.longValue())
					&& (tblTestTask.getCreateBy() != null && tblTestTask.getCreateBy().longValue() != currentUserId.longValue())
					&& (tblTestTask.getTaskAssignUserId() != null && tblTestTask.getTaskAssignUserId().longValue() != currentUserId.longValue())
					&&(userIds != null && !userIds.contains(currentUserId))) {
				map.put("status", "noPermission");
				return map;
			}
			workTaskService.examineHandle(handle, DHattachFiles, deleteFiles, request);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;
	}

	// ?????????
	@RequestMapping(value = "HandleTest", method = RequestMethod.POST)
	public Map HandleDev(String handle, String HattachFiles, String deleteFiles, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblTestTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteFiles,
					JsonUtil.createCollectionType(ArrayList.class, TblTestTaskAttachement.class));
			if (tblDevTaskAttachement.size() > 0) {
				workTaskRemark.updateNo(tblDevTaskAttachement, request);
			}
			TblTestTask tblTestTask = JSON.parseObject(handle, TblTestTask.class);
			currentUserId = CommonUtil.getCurrentUserId(request);
			//??????????????????id????????????????????????????????????????????????????????????????????????????????????
			List<Long> userIds = workTaskService.selectUserIdsById(tblTestTask.getId());
			if ((tblTestTask.getOldTestUserId() != null
					&& tblTestTask.getOldTestUserId().longValue() != currentUserId.longValue())
					&& (tblTestTask.getCreateBy() != null && tblTestTask.getCreateBy().longValue() != currentUserId.longValue())
					&& (tblTestTask.getTaskAssignUserId() != null && tblTestTask.getTaskAssignUserId().longValue() != currentUserId.longValue())
					&&(userIds != null && !userIds.contains(currentUserId))) {
				map.put("status", "noPermission");
				return map;
			}
			workTaskService.Handle(handle, HattachFiles, deleteFiles, request);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;
	}

	// ??????
	@RequestMapping(value = "assigDev", method = RequestMethod.POST)
	public Map assigDev(String assig, String Remark, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			currentUserId = CommonUtil.getCurrentUserId(request);
			TblTestTask tblTestTask = JSON.parseObject(assig, TblTestTask.class);
			//??????????????????id????????????????????????????????????????????????????????????????????????????????????
			List<Long> userIds = workTaskService.selectUserIdsById(tblTestTask.getId());
			if ((tblTestTask.getOldTestUserId() != null && tblTestTask.getOldTestUserId().longValue() != currentUserId.longValue())
					&& (tblTestTask.getCreateBy() != null && tblTestTask.getCreateBy().longValue() != currentUserId.longValue())
					&& (tblTestTask.getTaskAssignUserId() != null && tblTestTask.getTaskAssignUserId().longValue() != currentUserId.longValue())
					&&(userIds != null && !userIds.contains(currentUserId))) {
				map.put("status", "noPermission");
				return map;
			}
			workTaskService.assigTest(assig, Remark, request);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "getExcelAllWork")
	public void getExcelUser(String excelDate, HttpServletResponse response, HttpServletRequest request) {
		List<TblTestTask> list = new ArrayList<TblTestTask>();
		TblTestTask tblDevTask = new TblTestTask();
		List<Integer> Array = new ArrayList();
		try {
			Long Userd = CommonUtil.getCurrentUserId(request);
			if (StringUtils.isNotBlank(excelDate)) {
				tblDevTask = JSONObject.parseObject(excelDate, TblTestTask.class);
			}
				
			tblDevTask.setId(Userd);

			if (tblDevTask.getTestTaskStatus() != null) {
				String devStatus = tblDevTask.getTestTaskStatus().toString();
				if (devStatus.length() == 1) {
					Array.add(Integer.parseInt(devStatus));
					tblDevTask.setTestStatusList(Array);
				} else {
					for (int i = 0; i < devStatus.length(); i++) {
						String status = devStatus.substring(i, i + 1);
						Array.add(Integer.parseInt(status));
					}
					tblDevTask.setTestStatusList(Array);
				}
			}
			SimpleDateFormat sformat = new SimpleDateFormat("yyyy???MM???dd???HH???mm???ss???");
			long now = System.currentTimeMillis();
			String day = sformat.format(now);
			String fileName = "???????????????" + day + ".xls";
			list = workTaskService.getExcelAllWork(tblDevTask,request);
			new ExportExcel("", TblTestTask.class).setDataList(list).write(response, fileName).dispose();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
	}
	// ?????????????????????

	public void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			try {
				fileName = new String(fileName.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}

	// ????????????
	@RequestMapping(value = "getSystem", method = RequestMethod.POST)
	public Map<String, Object> getSystem(String workTask, Integer rows, Integer page, HttpServletRequest request) {
		Long userId = CommonUtil.getCurrentUserId(request);
		Map<String, Object> result = new HashMap<String, Object>();
		TblSystemInfo systemInfo = new TblSystemInfo();
		if (StringUtils.isNotBlank(workTask)) {
			systemInfo = JSONObject.parseObject(workTask, TblSystemInfo.class);
		}
			
		systemInfo.setId(userId);
		result = workTaskService.getAllsystem(systemInfo, page, rows);
		return result;
	}

	// ????????????
	@RequestMapping(value = "Requirement", method = RequestMethod.POST)
	public Map<String, Object> getRequirement(String workTask, Integer rows, Integer page, HttpServletRequest request) {
		Long userId = CommonUtil.getCurrentUserId(request);
		Map<String, Object> result = new HashMap<String, Object>();
		TblRequirementInfo rquirement = new TblRequirementInfo();
		if (StringUtils.isNotBlank(workTask)) {
			rquirement = JSONObject.parseObject(workTask, TblRequirementInfo.class);
		}
			
		rquirement.setId(userId);
		result = workTaskService.getAllRequirt(rquirement, page, rows);
		return result;
	}

	// ??????????????????
	@RequestMapping(value = "ReqSystem")
	public Map ReqSystem() {
		Map<String, Object> map = workTaskService.ReqSystem();
		return map;
	}

	// ????????????
	@RequestMapping(value = "ReqStatus")
	public Map ReqStatus() {
		Map<String, Object> map = workTaskService.ReqStatus();
		return map;
	}

	/* ???????????? */
	@PostMapping(value = "addTaskRemark")
	public void addTaskRemark(String remark, String attachFiles, HttpServletRequest request) {

		List<TblTestTaskRemarkAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblTestTaskRemarkAttachement.class));
		Long Userid = CommonUtil.getCurrentUserId(request);
		String userName = CommonUtil.getCurrentUserName(request);
		String UserAccount = CommonUtil.getCurrentUserAccount(request);
		TblTestTaskRemark tblTestTaskRemark = JSON.parseObject(remark, TblTestTaskRemark.class);
		workTaskRemark.addTaskRemark(remark, Userid, userName, UserAccount, files);

	}

	// ????????????
	@RequestMapping(value = "uploadFile")
	public List<Map<String, Object>> uploadFile(@RequestParam("files") MultipartFile[] files) throws Exception {
		List<Map<String, Object>> attinfos = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (files.length > 0 && files != null) {
				for (MultipartFile file : files) {
					String OldfileName = getFileNameNoEx(file.getOriginalFilename());
					if (!file.isEmpty()) {
						InputStream inputStream = file.getInputStream();
						map = new HashMap<String, Object>();
						String extension = file.getOriginalFilename()
								.substring(file.getOriginalFilename().lastIndexOf(".") + 1);// ?????????
						String newFileName = UUID.randomUUID().toString().replace("-", "");

						Random random = new Random();
						String i = String.valueOf(random.nextInt());
						String keyname = s3Util.putObject(s3Util.getTestTaskBucket(), i, inputStream);
						map.put("fileS3Key", keyname);
						map.put("fileS3Bucket", s3Util.getTestTaskBucket());

						map.put("fileNameNew", newFileName);// newFileName
						// map.put("filePath", url);//url
						map.put("fileNameOld", file.getOriginalFilename());// filename
						// map.put("length", file.getSize());
						map.put("fileType", extension);// extension

						attinfos.add(map);
					} else {
						// ??????????????????
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attinfos;
	}

	@RequestMapping(value = "downloadFile")
	public void downloadFile(String fileS3Bucket, String fileS3Key, String fileNameOld, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (!StringUtils.isBlank(fileS3Bucket) && !StringUtils.isBlank(fileS3Key)
					&& !StringUtils.isBlank(fileNameOld)) {
				s3Util.downObject(fileS3Bucket, fileS3Key, fileNameOld, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}

	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	@RequestMapping(value = "getTestTask", method = RequestMethod.POST)
	public Map<String, Object> getTestTask(Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblTestTask testTask = workTaskService.getTestTaskById(id);
			result.put("data", testTask);
		} catch (Exception e) {
			return handleException(e, "????????????????????????");
		}
		return result;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param tblDevTask
	 * @param devID
	 * @param notWithUserID
	 * @param pageNumber
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getAllTextUser")
	public Map<String, Object> getAllTextUser(TblUserInfo tblUserInfo, Integer devID, Integer notWithUserID,
			Integer pageNumber, Integer pageSize, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		Long id = CommonUtil.getCurrentUserId(request);
		tblUserInfo.setId(id);
		try {
			List<Map<String, Object>> list = workTaskService.getAllTestUser(tblUserInfo, notWithUserID, devID,
					pageNumber, pageSize);
			List<Map<String, Object>> list2 = workTaskService.getAllTestUser(tblUserInfo, notWithUserID, devID, null,
					null);
			result.put("rows", list);
			result.put("total", list2.size());
		} catch (Exception e) {

			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
			return handleException(e, "????????????????????????");
		}
		return result;

	}
	
}
