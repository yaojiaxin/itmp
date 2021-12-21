package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.dto.AssetSystemTreeDTO;
import cn.pioneeruniverse.dev.entity.TblCustomFieldTemplate;
import cn.pioneeruniverse.dev.entity.TblDefectInfo;
import cn.pioneeruniverse.dev.entity.TblProjectInfo;
import cn.pioneeruniverse.dev.entity.TblRequirementInfo;
import cn.pioneeruniverse.dev.vo.DefectInfoVo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TblDefectInfoMapper extends BaseMapper<TblDefectInfo> {
    int deleteByPrimaryKey(Long id);

    TblDefectInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblDefectInfo record);

    int updateByPrimaryKey(TblDefectInfo record);

    List<Map<String, Object>> getAllRequirement(Map<String, Object> map);

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

    List<TblDefectInfo> findDefectList( @Param("defect") DefectInfoVo tblDefectInfo, @Param("page") Object pageNum,  @Param("rows") Object rows);

    void insertDevDefect(TblDefectInfo tblDefectInfo);

    Integer deleteDefectById(Long id);
    
    List<TblDefectInfo> selectBytestCaseId(Long testCaseExecuteId);

    int countFindDefectList( @Param("defect") DefectInfoVo tblDefectInfo);

    int countGetAllRequirement(TblRequirementInfo requirementInfo);

    List<TblDefectInfo> findDefectsByTestTaskId(Long testTaskId);

    TblDefectInfo getDefectEntity(Long defectId);

    List<TblProjectInfo>  getAllProjectByCurrentUserId(Long currentUserId);

    List<TblProjectInfo> getAllProject();

    String getDafectFieldTemplateById(@Param("id")Long id, @Param("fieldName")String fieldName);

	Long findDefectCount(Long id);

	List<TblProjectInfo> getProject(Long systemId);

	void updateCommssioningWindowId(@Param("testTaskIds")String testTaskIds,@Param("windowId") Long windowId);

    void updateDefectByWorkId(TblDefectInfo tblDefectInfo);

	List<Long> findUserIdBySystemId(Long systemId);

	Long findUserIdByDefectId(Long defectId);

	Long findSystemIdByDefectId(Long defectId);
	
	List<Map<String, Object>> selectDefectPro(@Param("startDate")String startDate,@Param("endDate") String endDate);
	
	List<Map<String, Object>> selectDefectProBySystem(@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("systemIds")List<String> systemIds,@Param("taskCount") Integer taskCount);
	
	List<Map<String, Object>> selectDefectProByProject(@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("systemIds")List<String> systemIds,@Param("taskCount") Integer taskCount);
	
	List<Map<String, Object>> selectDefectLevel(@Param("startDate")String startDate,@Param("endDate")String endDate);
	
	List<Map<String, Object>> selectWorseProject(@Param("startDate")String startDate,@Param("endDate")String endDate);

    TblDefectInfo getDefectByDefectCode(String defectCode);
    void insertSynDefect(TblDefectInfo tblDefectInfo);
    void updateSynDefect(TblDefectInfo tblDefectInfo);

    TblDefectInfo findDefectById(Long id);

    //查询tbl_custom_field_template表中字段custom_form='tbl_defect_info'中的值
    TblCustomFieldTemplate selectTblCustomFieldTemplateByTblDefectInfo();

    /**
     * 查询出所需日志字段
     **/
    TblDefectInfo getDefectByIdForLog(@Param("defectId") Long defectId);

    /**
     * 查询资产系统树表中的数据
     */
    List<AssetSystemTreeDTO> selectAssetSystemTreeAll();

    /**
     * 查询系统版本表中的数据
     * @return
     */
    List<AssetSystemTreeDTO> selectSystemVersionAll();

    /**
     *  查询数据字典表中的数据
     * @return
     */
    List<AssetSystemTreeDTO> selectDataDicAll();
}