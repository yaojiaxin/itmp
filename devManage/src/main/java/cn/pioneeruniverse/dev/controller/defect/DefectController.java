package cn.pioneeruniverse.dev.controller.defect;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.databus.DataBusRequestHead;
import cn.pioneeruniverse.common.databus.DataBusUtil;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.common.velocity.tag.VelocityDataDict;
import cn.pioneeruniverse.dev.dao.mybatis.TblDefectInfoMapper;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.service.CustomFieldTemplate.ICustomFieldTemplateService;
import cn.pioneeruniverse.dev.service.defect.test.DefectService;
import cn.pioneeruniverse.dev.service.devtask.DevTaskService;
import cn.pioneeruniverse.dev.service.worktask.WorkTaskService;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;


/**
 * Description: 缺陷管理后端controller
 * Author:liushan
 * Date: 2018/12/10 下午 1:41
 */
@RestController
@RequestMapping(value = "defect")
public class DefectController extends BaseController {
    @Autowired
    private DefectService defectService;
    @Autowired
    private WorkTaskService workTaskService;
    @Autowired
    private DevTaskService devTaskService;
    @Autowired
    private TblDefectInfoMapper defectInfoMapper;
    @Autowired
    private S3Util s3Util;
    private Long currentUserId;
    @Autowired
    private ICustomFieldTemplateService iCustomFieldTemplateService;

    @Value("${databuscc.defectName}")
    private String databusccName;

    /**
     * 根据id查询当前日志信息
     * @param defectId
     * @return
     */
    @RequestMapping(value = "getDefectRecentLogById",method = RequestMethod.POST)
    public Map<String, Object> getDefectRecentLogById(Long defectId){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (defectId == null){
                return handleException(new Exception(), "查询当前信息失败");
            }
            result = defectService.getDefectRecentLogById(defectId);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "查询当前信息失败");
        }
        return result;
    }

    /**
     * 根据id查询当前日志信息
     * @param defectId
     * @return
     */
    @RequestMapping(value = "getDefectLogById",method = RequestMethod.POST)
    public Map<String, Object> getDefectLogById(Long defectId){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            if (defectId == null){
                return handleException(new Exception(), "查询当前信息失败");
            }
            List<TblDefectLog> defectLogs = defectService.getDefectLogsById(defectId);
            result.put("data",defectLogs);
        } catch (Exception e) {
            return handleException(e, "查询当前信息失败");
        }
        return result;
    }

    /**
     * 移除附件
     * @param ids
     * @return
     */
    @RequestMapping(value = "removeAtts",method = RequestMethod.POST)
    public Map<String, Object> removeAtts(Long[] ids,Long defectId,Long logId,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            if (ids == null && logId != null ){
                return handleException(new Exception(), "删除附件失败");
            }
            defectService.removeAtts(ids,defectId,logId,request);
        } catch (Exception e) {
            return handleException(e, "修改缺陷失败");
        }
        return result;
    }

    /**
     * 编辑查询附件
     * @param defectId
     * @return
     */
    @RequestMapping(value = "findAttList",method = RequestMethod.POST)
    public Map<String, Object> findAttList(Long defectId){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            if (defectId == null){
                return handleException(new Exception(), "查询附件失败");
            }
            List<TblDefectAttachement> defectAttachements = defectService.findAttListByDefectId(defectId);
            TblDefectInfo defectInfo = defectService.getDefectEntity(defectId);
            List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByDefect(defectId);
            List<TblDefectRemark> remarks = defectService.getRemarkByDefectId(defectId);
            List<TblDefectRemarkAttachement> remarkAtts = defectService.getRemarkAttsByDefectId(defectId);
            result.put("field", extendedFields);
            result.put("attList",defectAttachements);
            result.put("defectInfo",defectInfo);
            result.put("remarks", remarks);
            result.put("remarkAtts", remarkAtts);
        } catch (Exception e) {
            return handleException(e, "查询附件失败");
        }
        return result;
    }

    /**
     * 修改的操作
     * @param tblDefectInfo
     * @param defectRemark
     * @param request
     * @return
     */
    @RequestMapping(value = "updateDefectStatus",method = RequestMethod.POST)
    public Map<String, Object> updateDefectStatus(Long defectId, TblDefectInfo tblDefectInfo, TblDefectRemark defectRemark, HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            if (defectId == null){
                return handleException(new Exception(), "修改缺陷失败");
            }
            tblDefectInfo.setId(defectId);
            Long logId = defectService.updateDefectStatus(tblDefectInfo,defectRemark,request);
            result.put("logId",logId);
        } catch (Exception e) {
            return handleException(e, "修改缺陷失败");
        }
        return result;
    }

    /**
     * 待确认的操作
     * @param tblDefectInfo
     * @param defectRemark
     * @param request
     * @return
     */
    @RequestMapping(value = "updateDefectwithTBC",method = RequestMethod.POST)
    public Map<String, Object> updateDefectwithTBC(Long defectId,Long oldAssignUserId,TblDefectInfo tblDefectInfo, TblDefectRemark defectRemark,Double actualWorkload,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        currentUserId = CommonUtil.getCurrentUserId(request);
        try {
            if(currentUserId != null && oldAssignUserId != null && oldAssignUserId.longValue() != currentUserId.longValue()){
                result.put("status", "noPermission");
                return result;
            }
            if (defectId == null){
                return handleException(new Exception(), "修改缺陷失败");
            }
            tblDefectInfo.setId(defectId);
            Long logId = defectService.updateDefectwithTBC(tblDefectInfo,defectRemark,actualWorkload,request);
            result.put("logId", logId);

            TblDefectInfo dblDefectInfo = defectInfoMapper.findDefectById(defectId);
            if(tblDefectInfo.getDefectStatus()==3&&dblDefectInfo.getCreateType() == 2){
                dblDefectInfo.setRejectReason(tblDefectInfo.getRejectReason());
                String resultInfo = putDefectInfo(dblDefectInfo);
                DataBusUtil.send(databusccName,dblDefectInfo.getDefectCode(),resultInfo);
            }
        } catch (Exception e) {
            return handleException(e, "修改缺陷失败");
        }
        return result;
    }

    @RequestMapping(value = "updateDefect",method = RequestMethod.POST)
    public Map<String, Object> updateDefect(TblDefectInfo tblDefectInfo, HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        currentUserId = CommonUtil.getCurrentUserId(request);
        try {
            Map<String, Object> idMap = defectService.updateDefect(tblDefectInfo,request);
            if(idMap.containsKey("defectId") && idMap.containsKey("logId")){
                result.put("defectId",idMap.get("defectId"));
                result.put("logId",idMap.get("logId"));
            }
        } catch (Exception e) {
            return handleException(e, "修改缺陷失败");
        }
        return result;
    }

    @RequestMapping(value = "insertDefect",method = RequestMethod.POST)
    public Map<String, Object> insertDefect(TblDefectInfo tblDefectInfo ,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            Map<String, Object> idMap = defectService.insertDefect(tblDefectInfo,request);
            result.put("defectId",idMap.get("defectId"));
            result.put("logId",idMap.get("logId"));
        } catch (Exception e) {
            return handleException(e, "新建缺陷失败");
        }
        return result;
    }

    @RequestMapping(value = "list", method = RequestMethod.POST)
    public JqGridPage<DefectInfoVo> list(HttpServletRequest request,HttpServletResponse response,
                                          DefectInfoVo tblDefectInfo) {
        JqGridPage<DefectInfoVo> list = null;
        currentUserId = CommonUtil.getCurrentUserId(request);
        try {
             list = defectService.findDefectListPage(new JqGridPage<>(request, response),tblDefectInfo,currentUserId,request);
        } catch (Exception e) {
            logger.error("获取缺陷信息失败" + ":" + e.getMessage(), e);
            return null;
        }
        return list;
    }

    @RequestMapping(value="getDefectEntity",method = RequestMethod.POST)
    public Map<String, Object> getDefectEntity(Long defectId){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            if (defectId == null){
                return handleException(new Exception(), "修改缺陷失败");
            }
            List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByDefect(defectId);
            TblDefectInfo defectInfo = defectService.getDefectEntity(defectId);
            result.put("defectInfo",defectInfo);
            result.put("field", extendedFields);
        } catch (Exception e) {
            return handleException(e, "新建缺陷失败");
        }
        return result;
    }

    /**
     * 逻辑删除缺陷
     * @param id
     * @return
     */
    @RequestMapping(value = "removeDefect", method = RequestMethod.POST)
    public Map<String,Object> removeDefect(Long id,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            defectService.removeDefect(id,request);
        } catch (Exception e) {
            return handleException(e, "删除缺陷信息失败");
        }
        return result;
    }


    /**
     * 上传文件
     * @param files
     * @return
     */
    @RequestMapping(value="updateFiles")
    public Map<String, Object> updateFiles(@RequestParam("files") MultipartFile[] files,@RequestParam("id") Long defectId,@RequestParam("logId") Long logId, HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            String str = defectService.updateFiles(files,defectId,logId,request);
            if (str.equals("NULL")){
                return handleException(new Exception(), "上传文件信息失败");
            }
        } catch (Exception e) {
            return handleException(e, "上传文件信息失败");
        }
        return result;
    }

    /**
     * 上传备注文件
     * @param files
     * @param request
     * @return
     */
    @RequestMapping(value="updateRemarkLogFiles")
    public Map<String, Object> updateRemarkLogFiles(@RequestParam("files") MultipartFile[] files,@RequestParam("logId") Long logId,HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            String str = defectService.updateRemarkLogFiles(files,logId,request);
            if (str.equals("NULL")){
                return handleException(new Exception(), "上传文件信息失败");
            }
        } catch (Exception e) {
            return handleException(e, "上传文件信息失败");
        }
        return result;
    }

    /**
     * 下载文件
     */
    @RequestMapping(value= "downloadFile")
    public void downloadFile(TblDefectAttachement defectAttachement,HttpServletResponse response) {
        s3Util.downObject(defectAttachement.getFileS3Bucket(), defectAttachement.getFileS3Key(), defectAttachement.getFileNameOld(), response);
    }

    /**
     * 导出当前页面查询出的结果集
     * @param defectInfoVo
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "export")
    public void export(DefectInfoVo defectInfoVo,HttpServletResponse response,HttpServletRequest request) throws Exception{
       defectService.export(new JqGridPage<DefectInfoVo>(request, response),defectInfoVo,response,request) ;
   }
    
  //保存备注
    @PostMapping(value = "addDefectRemark")
	public Map<String, Object> addTaskRemark(String remark, String attachFiles, HttpServletRequest request) {
    	 Map<String, Object> result = new HashMap<>();
         result.put("status", Constants.ITMP_RETURN_SUCCESS);
         List<TblDefectRemarkAttachement> files = JsonUtil.fromJson(attachFiles,
 				JsonUtil.createCollectionType(ArrayList.class, TblDefectRemarkAttachement.class));
		try {
			TblDefectRemark tblDefectRemark = JSON.parseObject(remark, TblDefectRemark.class);
			defectService.addDefectRemark(tblDefectRemark,files, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 return handleException(e, "保存备注失败");
		}
		 return result;
	}

    private String putDefectInfo(TblDefectInfo tblDefectInfo){
        Map<String, Object> mapAll = new LinkedHashMap<>();
        Map<String,Object> requestBody = new HashMap<>();
        VelocityDataDict dict= new VelocityDataDict();

        String comment = "";
        requestBody.put("problemNumber",tblDefectInfo.getDefectCode());
        requestBody.put("operationType",1);
        Map<String,String> rejectReason = dict.getDictMap("TBL_DEFECT_INFO_REJECT_REASON");
        for(Map.Entry<String, String> entry : rejectReason.entrySet()){
            if(entry.getKey().equals(tblDefectInfo.getRejectReason().toString())){
                comment = entry.getValue();
            }
        }
        requestBody.put("comment",comment);
        requestBody.put("releaseTime","");
        mapAll.put("requestHead",DataBusRequestHead.getRequestHead());
        mapAll.put("requestBody",requestBody);
        String result = JSON.toJSONString(mapAll, SerializerFeature.WriteDateUseDateFormat);
        return result;
    }
}


