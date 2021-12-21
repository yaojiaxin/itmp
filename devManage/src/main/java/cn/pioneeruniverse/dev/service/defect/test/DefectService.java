package cn.pioneeruniverse.dev.service.defect.test;

import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.dev.entity.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author:liushan
 * Date: 2018/12/10 下午 1:44
 */
public interface DefectService {
 List<TblRequirementInfo> getAllRequirement(TblRequirementInfo requirementInfo, Integer pageNumber, Integer pageSize)throws Exception;

 Map<String, Object> insertDefect(TblDefectInfo tblDefectInfo, HttpServletRequest request)throws Exception;

 Map<String,Object> getDicKey(String key);

 Map<String, Object> getAllSystemInfo(TblSystemInfo systemInfo, Integer pageNumber, Integer pageSize,HttpServletRequest request)throws Exception;

 void removeDefect(Long id,HttpServletRequest request)throws Exception;

 void removeDefectById(Long id)throws Exception;

 String updateFiles(MultipartFile[] files, Long defectId, Long logId, HttpServletRequest request) throws Exception;

 TblDefectAttachement getDefectAttByUrl(String path)throws Exception;

 Map<String, Object>  updateDefect(TblDefectInfo tblDefectInfo, HttpServletRequest request)throws Exception;

 List<TblDefectAttachement> findAttListByDefectId(Long defectId)throws Exception;

 void removeAtts(Long[] ids, Long defectId, Long logId,HttpServletRequest request) throws Exception;

 String updateRemarkLogFiles(MultipartFile[] files, Long logId, HttpServletRequest request) throws Exception;

 Long updateDefectwithTBC(TblDefectInfo tblDefectInfo, TblDefectRemark defectRemark, Double actualWorkload, HttpServletRequest request)throws Exception;

 Long updateDefectStatus(TblDefectInfo tblDefectInfo, TblDefectRemark defectRemark,  HttpServletRequest request)throws Exception;

 List<TblDefectLog> getDefectLogsById(Long id)throws Exception;

 Map<String, Object> getDefectRecentLogById(Long defectId)throws Exception;

 Map<String,Object> getAllComWindow(TblCommissioningWindow window, Integer pageNumber, Integer pageSize)throws Exception;

 void export(JqGridPage<DefectInfoVo> defectInfoVoJqGridPage, DefectInfoVo defectInfoVo,HttpServletResponse response, HttpServletRequest request) throws Exception;

 JqGridPage<DefectInfoVo> findDefectListPage(JqGridPage<DefectInfoVo> page,DefectInfoVo tblDefectInfo,Long currentUserId,HttpServletRequest request) throws Exception;

 TblDefectInfo getDefectById(Long defectId);

 TblDefectInfo getDefectEntity(Long defectId);

List<TblDefectRemark> getRemarkByDefectId(Long defectId);

List<TblDefectRemarkAttachement> getRemarkAttsByDefectId(Long defectId);

void addDefectRemark(TblDefectRemark tblDefectRemark, List<TblDefectRemarkAttachement> files,
		HttpServletRequest request);

void addTmpDefectRemark(TblDefectRemark tblDefectRemark, List<TblDefectRemarkAttachement> files,
		HttpServletRequest request);

}
