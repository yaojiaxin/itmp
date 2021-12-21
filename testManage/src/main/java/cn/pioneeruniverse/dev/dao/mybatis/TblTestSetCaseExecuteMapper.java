package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.dev.entity.TblTestSetCaseExecute;
import feign.Param;

public interface TblTestSetCaseExecuteMapper {
	 //添加
	int insertSelective(Map<String, Object> map);
	 //更新执行结果
	 int updateSelective(Map<String, Object> map);
	 //查询测试执行
	 List<TblTestSetCaseExecute>  findeSetCase(TblTestSetCaseExecute tblTestSetCaseExecute);
	 // 根据测试集查询 
	 TblTestSetCaseExecute selectByID(Long id);
	 String selectNameById(Long id);

	 int insertCodeBatch (Map<String, Object> map);
	 
	 List<TblTestSetCaseExecute>  selectByTestSet(Long testSetId);
	List<TblTestSetCaseExecute> getCaseExecute(Map<String, Object> map);
	Long getCaseExecuteCount(Long devID);
	List<String> findExecuteUser(Long devID);
}
