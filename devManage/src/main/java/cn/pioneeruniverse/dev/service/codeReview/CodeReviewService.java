package cn.pioneeruniverse.dev.service.codeReview;

import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.dev.entity.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 17:30 2019/3/15
 * @Modified By:
 */
public interface CodeReviewService {

    JqGridPage<TblDevTaskScm> getCodeReviewPage(JqGridPage<TblDevTaskScm> jqGridPage, TblDevTaskScm tblDevTaskScm, Long currentUserId) throws Exception;

    List<TblDevTaskScmFile> getReviewFilesByDevTaskScmId(Long devTaskScmId);

    List<TblDevTaskScmGitFile> getReviewGitFilesByDevTaskScmId(Long devTaskScmId);

    Map<String, Object> getGitFileInfo(Long toolId, Long projectId, String branchName) throws URISyntaxException;

    Integer getFileCommentsCountByFileId(Long devTaskScmFileId, Integer scmFileType);

    TblDevTaskScm getDevTaskDetailByDevTaskId(Long devTaskId);

    String getFileContent(TblDevTaskScmFile tblDevTaskScmFile);

    String getGitFileContent(TblDevTaskScmGitFile tblDevTaskScmGitFile) throws URISyntaxException;

    Map<String, Map<String, Object>> getCompareFileInfo(TblDevTaskScmFile tblDevTaskScmFile);

    Map<String, Map<String, Object>> getCompareGitFileInfo(TblDevTaskScmGitFile tblDevTaskScmGitFile) throws URISyntaxException;

    List<TblDevTaskScmFileReview> getFileComments(Long devTaskScmFileId,Integer scmFileType);

    int updateCodeReviewResult(HttpServletRequest request, TblDevTask tblDevTask);

    void sendCodeReviewComment(TblDevTaskScmFileReview tblDevTaskScmFileReview, HttpServletRequest request);

}
