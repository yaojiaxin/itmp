package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblDevTaskScmGitFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 15:59 2019/9/2
 * @Modified By:
 */
public interface TblDevTaskScmGitFileMapper {

    int insertOrUpdateDevTaskScmGitFile(@Param("commitFiles") List<String> commitFiles, @Param("tblDevTaskScmGitFile") TblDevTaskScmGitFile tblDevTaskScmGitFile);

    List<TblDevTaskScmGitFile> getReviewGitFilesByDevTaskScmId(Long devTaskScmId);

    List<TblDevTaskScmGitFile> getGitFilesByDevTaskId(Long devTaskId);

    List<Map<String, Object>> getGitFilesByDevTaskIds(Map<String, Object> param);
}
