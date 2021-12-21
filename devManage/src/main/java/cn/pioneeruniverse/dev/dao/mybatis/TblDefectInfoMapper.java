package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.*;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TblDefectInfoMapper extends BaseMapper<TblDefectInfo> {
    int deleteByPrimaryKey(Long id);

    TblDefectInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectInfo record);

    int updateByPrimaryKey(TblDefectInfo record);

    List<TblRequirementInfo> getAllRequirement(Map<String, Object> map);

    void insertDefect(TblDefectInfo tblDefectInfo);

    String selectMaxDefectCode();

    void updateDefect(TblDefectInfo tblDefectInfo);

    void updateDefectAssignUser(TblDefectInfo tblDefectInfo);

    void updateDefectRejectReason(TblDefectInfo tblDefectInfo);

    void updateDefectStatus(TblDefectInfo tblDefectInfo);

    void updateDefectSolveStatus(TblDefectInfo tblDefectInfo);

    String getUserNameById(Long id);

    TblDefectInfo getDefectById(Long id);

    void removeDefect(Long id);

    List<TblDefectInfo> findDefectList(DefectInfoVo tblDefectInfo);

    void updateDevDefect(Map<String,Object> map);

    void deleteDefectById(Long id);

    int countFindDefectList(DefectInfoVo tblDefectInfo);
    
    List<TblDefectInfo> findDefectByAssignUserId(Long userId);

    TblDefectInfo getDefectByCode(String code);

    TblDefectInfo getDefectEntity(Long defectId);

    String getDafectFieldTemplateById(@Param("id")Long id, @Param("fieldName")String fieldName);

	void updateCommssioningWindowId(TblRequirementFeature requirementFeature);

    TblDefectInfo findDefectById(Long id);

    //获取开发任务系统id
    TblDevTask selectRequirementFeatureById(Long id);

}