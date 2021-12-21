package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocument;
import cn.pioneeruniverse.project.service.assetsLibrary.IAssetsLibraryRqService;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author weiji
 *
 */
@RestController
@RequestMapping("assetsLibraryRq")
public class AssetsLibraryRequireController extends BaseController{

	@Autowired
	private IAssetsLibraryRqService iAssetsLibraryRqService;
	@Autowired
	private S3Util s3Util;





	/**
	 *  部门视角目录
	 * @param  requireIds 搜索需求id
	 * @param  systemIds  搜索系统id
	 * @param  reqTaskIds 搜索任务id
	 * @param  type       目录层级 1部门 2需求
	 * @param  znodeId    一层目录id
	 * @param
	 * @return Map<String, Object>
	 *
	 */
	@RequestMapping(value = "getDeptdirectory", method = RequestMethod.POST)
	public Map<String, Object> getDeptdirectory(HttpServletRequest request,String requireId, String systemId,String reqTaskId, String type, String znodeId,String search) {
		    Map<String, Object> result = new HashMap<>();
		      if(type==null){
                type="1";
			  }
				long[] systemIds = null;
				long[] requireIds = null;
				long[] reqTaskIds = null;
				if (!systemId.equals("")) {
					systemIds = (long[]) ConvertUtils.convert(systemId.split(","), long.class);
				}
				if (!requireId.equals("")) {
					requireIds = (long[]) ConvertUtils.convert(requireId.split(","), long.class);
				}
				if (!reqTaskId.equals("")) {
					reqTaskIds = (long[]) ConvertUtils.convert(reqTaskId.split(","), long.class);
				}

				result.put("status", Constants.ITMP_RETURN_SUCCESS);
				try {
					result = iAssetsLibraryRqService.getDeptdirectory(result, requireIds, systemIds, reqTaskIds, type, znodeId,search);
				} catch (Exception e) {
					return this.handleException(e, "获取信息失败");
				}


			return  result;

	}





	/**
	 *  系统视角目录
	 * @param  requireIds 搜索需求id
	 * @param  systemIds  搜索系统id
	 * @param  reqTaskIds 搜索任务id
	 * @param  type       目录层级 1部门 2需求
	 * @param  znodeId    一层目录id
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getSystemDirectory", method = RequestMethod.POST)
	public Map<String, Object> getSystemDirectory( HttpServletRequest request,String requireId,String systemId,String reqTaskId,String type,String znodeId,String search) {
        if(type==null){
            type="1";
        }
		long[] systemIds=null;
		long[] requireIds=null;
		long[]  reqTaskIds=null;
		if(!systemId.equals("")){
			systemIds=(long[]) ConvertUtils.convert(systemId.split(","),long.class);
		}
		if(!requireId.equals("")){
			requireIds=(long[]) ConvertUtils.convert(requireId.split(","),long.class);
		}
		if(!reqTaskId.equals("")){
			reqTaskIds = (long[]) ConvertUtils.convert(reqTaskId.split(","),long.class);
		}
		Map<String, Object> result = new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            result=iAssetsLibraryRqService.getSystemDirectory(result,requireIds,systemIds,reqTaskIds,type,znodeId,search);
        } catch (Exception e) {
            return this.handleException(e,"获取信息失败");
        }
        return  result;

	}







	/**
	 * 从bucket下载附件
	 *
	 * @param tblSystemDirectoryDocument 文档类
	 */
	@RequestMapping(value = "downObject")
	public void downObject(TblSystemDirectoryDocument tblSystemDirectoryDocument,HttpServletRequest request, HttpServletResponse response) {

        try {

            if(!StringUtils.isBlank(tblSystemDirectoryDocument.getDocumentS3Bucket())&&!StringUtils.isBlank(tblSystemDirectoryDocument.getDocumentS3Key())&&!StringUtils.isBlank(tblSystemDirectoryDocument.getDocumentName())) {
                String documentS3Bucket=  URLDecoder.decode(request.getParameter("documentS3Bucket"), "UTF-8");
                String documentS3Key=  URLDecoder.decode(request.getParameter("documentS3Key"), "UTF-8");
                String documentName=  URLDecoder.decode(request.getParameter("documentName"), "UTF-8");
                s3Util.downObject(documentS3Bucket, documentS3Key, documentName, response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }



	}

    /**
     * 从bucket下载markdown转string
     *
     * @param tblSystemDirectoryDocument 文档类
     */

	public String getMarkDown(TblSystemDirectoryDocument tblSystemDirectoryDocument) throws Exception {
		return s3Util.getStringByS3(tblSystemDirectoryDocument.getDocumentS3Bucket().toString(), tblSystemDirectoryDocument.getDocumentS3Key());

	}


	/**
	 * 从bucket下载markdown转string
	 *
	 * @param tblSystemDirectoryDocument 需求id
	 */

	public Map<String,Object> getStringByS3(TblSystemDirectoryDocument tblSystemDirectoryDocument) throws Exception {
		Map<String,Object> result=new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            String markDown=s3Util.getStringByS3(tblSystemDirectoryDocument.getDocumentS3Bucket().toString(),tblSystemDirectoryDocument.getDocumentS3Key());
            result.put("markDown",markDown);
            //获取此文档附件
            result=iAssetsLibraryRqService.getAttachments(result,String.valueOf(tblSystemDirectoryDocument.getId()));
        } catch (Exception e) {
            return this.handleException(e,"获取信息失败");
        }

        return  result;
	}


//	/**
//	 * 获取需求说明书
//	 *
//	 * @param requireId
//	 */
//
//	public Map<String,Object> getRequiredir(String requireId,String pid) throws Exception {
//		Map<String,Object> result=new HashMap<>();
//        result.put("status", Constants.ITMP_RETURN_SUCCESS);
//        try {
//            result=iAssetsLibraryRqService.getRequiredir(result,requireId,pid);
//        } catch (Exception e) {
//            return this.handleException(e,"获取信息失败");
//        }
//        return  result;
//	}


    /**
     * 获取文档
     *
     * @param requireId 需求id
	 * @param documentType 文档类型
	 */
    @RequestMapping(value = "getDocuments", method = RequestMethod.POST)
	public  Map<String, Object> getDocuments(long requireId,String docType,String systemId) throws Exception {
        Map<String,Object> result=new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            result=iAssetsLibraryRqService.getDocuments(result,requireId,docType,systemId);
        } catch (Exception e) {
            return this.handleException(e,"获取信息失败");
        }
        return  result;
    }



	/**
	 * 查询关联信息
	 *
	 * @param documetId 文档id
	 */
	@RequestMapping(value = "getRelationInfo", method = RequestMethod.POST)
	public Map<String,Object> getRelationInfo(String documetId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            result = iAssetsLibraryRqService.getRelationInfo(result, documetId);
        } catch (Exception e) {
            return this.handleException(e, "获取信息失败");

        }

        return result;
    }
    /**
     * 查询历史
     *
     * @param documetId 文档id
     */
	@RequestMapping(value = "getDocumentHistory", method = RequestMethod.POST)
    public Map<String,Object> getDocumentHistory(String documetId) throws Exception {
        Map<String,Object> result=new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            result=iAssetsLibraryRqService.getDocumentHistory(result,documetId);
        } catch (Exception e) {
            return this.handleException(e,"获取信息失败");
        }
        return  result;
    }


    /**
     * 查询附件
     *
     * @param documetId 文档id
     */

    public Map<String,Object> getAttachments(String documetId) throws Exception {
        Map<String,Object> result=new HashMap<>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            result=iAssetsLibraryRqService.getAttachments(result,documetId);
        } catch (Exception e) {
            return this.handleException(e,"获取信息失败");
        }
        return  result;
    }






}
