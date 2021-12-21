package cn.pioneeruniverse.project.dao.mybatis.assetLibrary;

import cn.pioneeruniverse.project.entity.TblSystemDirectory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 12:00 2019/11/12
 * @Modified By:
 */
public interface SystemDirectoryDao extends BaseMapper<TblSystemDirectory> {

    List<TblSystemDirectory> getSystemDirectoryListForZtree(@Param("systemIds") String systemIds, @Param("projectId") Long projectId);

    int addSystemDirectory(TblSystemDirectory tblSystemDirectory);

    int delSystemDirectory(TblSystemDirectory tblSystemDirectory);

    int delSystemDirectoriesByIds(@Param("directoryIds") List<Long> directoryIds);

    int delSystemDirectoryBySystemId(@Param("systemId") Long systemId);

    int updateSystemDirectoryName(TblSystemDirectory tblSystemDirectory);

    TblSystemDirectory getDirectoryTypes(@Param("directoryId") Long directoryId);
    
    TblSystemDirectory  getDocumentType(@Param("directoryId") Long directoryId);

    List<TblSystemDirectory> getDirectoryTreeForDocumentLibrary(@Param("projectId") Long projectId);

    List<Long> getAllSonDirectoryIds(TblSystemDirectory tblSystemDirectory);
}
