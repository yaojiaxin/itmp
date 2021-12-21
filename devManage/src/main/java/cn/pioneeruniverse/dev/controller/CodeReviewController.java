package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.service.codeReview.CodeReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 16:59 2019/3/15
 * @Modified By:
 */
@RestController
@RequestMapping(value = "codeReview")
public class CodeReviewController {

    private final static Logger logger = LoggerFactory.getLogger(CodeReviewController.class);

    @Autowired
    private CodeReviewService codeReviewService;

    @RequestMapping(value = "getCodeReviewPage", method = RequestMethod.POST)
    public JqGridPage<TblDevTaskScm> getCodeReviewPage(HttpServletRequest request, HttpServletResponse response, TblDevTaskScm tblDevTaskScm) {
        JqGridPage<TblDevTaskScm> jqGridPage = null;
        try {
            jqGridPage = codeReviewService.getCodeReviewPage(new JqGridPage<>(request, response), tblDevTaskScm, CommonUtil.getCurrentUserId(request));
            return jqGridPage;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @RequestMapping(value = "getReviewFilesByDevTaskScmId", method = RequestMethod.POST)
    public List<TblDevTaskScmFile> getReviewFilesByDevTaskScmId(Long devTaskScmId) {
        return codeReviewService.getReviewFilesByDevTaskScmId(devTaskScmId);
    }

    @RequestMapping(value = "getReviewGitFilesByDevTaskScmId", method = RequestMethod.POST)
    public List<TblDevTaskScmGitFile> getReviewGitFilesByDevTaskScmId(Long devTaskScmId) {
        return codeReviewService.getReviewGitFilesByDevTaskScmId(devTaskScmId);
    }

    @RequestMapping(value = "getGitFileInfo", method = RequestMethod.POST)
    public Map<String, Object> getGitFileInfo(Long toolId, Long projectId, String branchName) {
        try {
            return codeReviewService.getGitFileInfo(toolId, projectId, branchName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @RequestMapping(value = "getFileCommentsCountByFileId", method = RequestMethod.POST)
    public AjaxModel getFileCommentsCountByFileId(Long devTaskScmFileId, Integer scmFileType) {
        try {
            return AjaxModel.SUCCESS(codeReviewService.getFileCommentsCountByFileId(devTaskScmFileId, scmFileType));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getDevTaskDetailByDevTaskId", method = RequestMethod.POST)
    public TblDevTaskScm getDevTaskDetailByDevTaskId(Long devTaskId) {
        return codeReviewService.getDevTaskDetailByDevTaskId(devTaskId);
    }

    @RequestMapping(value = "getFileContent", method = RequestMethod.POST)
    public AjaxModel getFileContent(TblDevTaskScmFile tblDevTaskScmFile) {
        try {
            return AjaxModel.SUCCESS(codeReviewService.getFileContent(tblDevTaskScmFile));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getGitFileContent", method = RequestMethod.POST)
    public AjaxModel getGitFileContent(TblDevTaskScmGitFile tblDevTaskScmGitFile) {
        try {
            return AjaxModel.SUCCESS(codeReviewService.getGitFileContent(tblDevTaskScmGitFile));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getFileComments", method = RequestMethod.POST)
    public AjaxModel getFileComments(Long devTaskScmFileId, Integer scmFileType) {
        try {
            return AjaxModel.SUCCESS(codeReviewService.getFileComments(devTaskScmFileId,scmFileType));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getCompareFileInfo", method = RequestMethod.POST)
    public AjaxModel getCompareFileInfo(TblDevTaskScmFile tblDevTaskScmFile) {
        try {
            return AjaxModel.SUCCESS(codeReviewService.getCompareFileInfo(tblDevTaskScmFile));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getCompareGitFileInfo", method = RequestMethod.POST)
    public AjaxModel getCompareGitFileInfo(TblDevTaskScmGitFile tblDevTaskScmGitFile) {
        try {
            return AjaxModel.SUCCESS(codeReviewService.getCompareGitFileInfo(tblDevTaskScmGitFile));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "updateCodeReviewResult", method = RequestMethod.POST)
    public AjaxModel updateCodeReviewResult(HttpServletRequest request, TblDevTask tblDevTask) {
        try {
            if (codeReviewService.updateCodeReviewResult(request, tblDevTask) != 1) {
                return AjaxModel.FAIL(new Exception("代码评审已通过，无法再次评审"));
            } else {
                return AjaxModel.SUCCESS("评审状态已被更新");
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "sendCodeReviewComment", method = RequestMethod.POST)
    public AjaxModel sendCodeReviewComment(TblDevTaskScmFileReview tblDevTaskScmFileReview, HttpServletRequest request) {
        try {
            codeReviewService.sendCodeReviewComment(tblDevTaskScmFileReview, request);
            return AjaxModel.SUCCESS("评审消息发送成功");
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

}
