package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblTestSet;
import cn.pioneeruniverse.dev.entity.TblTestSetCase;
import cn.pioneeruniverse.dev.entity.TblTestSetCaseVo;

public interface TblTestSetCaseMapper extends BaseMapper<TblTestSetCase>{
	List<TblTestSetCaseVo> selectByTestSetId(Map<String, Object> map);
	
	Integer insert(TblTestSetCase testSetCase);

    /**
     * 批量修改
     * @param result
     * @return
     */
    int updateResult(Map<String, Object> result);
    /**
     * 批量执行查询
     * @param map
     * @return
     */
    List<Map<String, Object>> selectBatchPass(Map<String, Object> map);
    /**
     * 修改案例查询
     * @param id
     * @return
     */
    Map<String, Object> getTestSetCaseId(Long id);
    /**
     * 修改案例编辑
     * @param map
     * @return
     */
    int updatetestSetCase(Map<String, Object> map);
    /**
     * 根据案例ID查询数据
     * @param testSetCase
     * @return
     */
    
    List<Map<String, Object>> selectByCon(TblTestSetCase testSetCase);
    
    void updateCase(TblTestSetCase testSetCase);
    
    void updateManyStatus(Map<String, Object> map);
    
    void batchInsert(@Param("list") List<TblTestSetCase> list);
    
    Long judgeTestSetCase(@Param("testSetId") Long testSetId,@Param("caseNumber") String caseNumber);
    
    void updateExecuteResult(Map<String, Object> map);
    
    List<Map<String, Object>> selectCaseTree(@Param("testSetId") Long testSetId,@Param("executeRound") Integer executeRound,@Param("executeResultCode") Long executeResultCode);

	Long findCaseCount(Long id);

	Long getCaseExecuteResult(HashMap<String, Object> map);

	Long getCaseCount(Long devID);
}