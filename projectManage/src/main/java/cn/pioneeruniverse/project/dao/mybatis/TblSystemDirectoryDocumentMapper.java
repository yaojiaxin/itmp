package cn.pioneeruniverse.project.dao.mybatis;


import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocument;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface TblSystemDirectoryDocumentMapper extends BaseMapper<TblSystemDirectoryDocument> {

    List<Map<String, Object>> getDeptdirectory(Map<String, Object> requirementIds);

    List<Map<String, Object>> getRequiredirectory(Map<String, Object> map);

    List<Map<String, Object>> getSystemDirectory(Map<String, Object> mapParam);

    List<Map<String, Object>> getSvnFilesByDevTaskId(Long devTaskId);

    List<Map<String, Object>> getGitFilesByDevTaskId(Long devTaskId);

    List<Map<String, Object>> getSystemDirectorySearch(Map<String, Object> mapParam);

    List<TblSystemDirectoryDocument> getDocumentsByRequire(Map<String, Object> param);

    List<TblSystemDirectoryDocument> getDocumentsByRequireSystem(Map<String, Object> param);

    List<String> getRequireBydocuId(String documetId);
}