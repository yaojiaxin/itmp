package cn.pioneeruniverse.dev.service.testExecute.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.PageWithBootStrap;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblDefectInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseExecuteAttachementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseExecuteMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseStepExecuteMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseStepMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetExecuteRoundCaseResultMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblUserInfoMapper;
import cn.pioneeruniverse.dev.service.testCaseManage.TestCaseManageService;
import cn.pioneeruniverse.dev.service.testExecute.testExecuteService;

@Transactional(readOnly = true)
@Service("testExecuteService")
public class testExecuteServiceImpl implements testExecuteService{
	 public static final String EXECUTERESULT = "TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT"; 
	 @Autowired
	 private TblTestSetCaseMapper tblTestSetCaseMapper;
	 @Autowired 
	 private TblTestSetCaseStepMapper tblTestSetCaseStepMapper;
	 @Autowired
	 private TblTestSetExecuteRoundCaseResultMapper tblTestSetExecuteRoundCaseResult;
	 @Autowired
	 private RedisUtils redisUtils;
	 @Autowired
	 private  TblTestSetCaseExecuteMapper tblTestSetCaseExecuteMapper;
	 @Autowired
	 private  TblTestSetCaseStepExecuteMapper tblTestSetCaseStepExecuteMapper;
	 @Autowired
	 private TblTestSetCaseExecuteAttachementMapper tblTestSetCaseExecuteAttachementMapper;
	 @Autowired
	 private TblDefectInfoMapper tblDefectInfoMapper;
	 @Autowired
	 private TblUserInfoMapper tblUserInfoMapper;
	 @Autowired
	 private TestCaseManageService testCaseManageService;
	 /**
	  * 批量通过查看
	  */
	@Override
	@Transactional(readOnly = true)
	public  List<Map<String, Object>> selectByPrimaryKey(String testSetId,int excuteRound,String caseExecuteResultList,Integer pageNumber, Integer pageSize) {
		 Map<String, Object> map = new HashMap<>();
		 map = PageWithBootStrap.setPageNumberSize(map,pageNumber,pageSize);
		 map.put("testSetId", Long.parseLong(testSetId));
		 map.put("excuteRound", excuteRound);
		 List<Integer> caseExecuteResul=JSON.parseArray(caseExecuteResultList, Integer.class);
		 Integer unexecuted=0;
		 if(caseExecuteResul!=null) {
			 for(int i=0;i<caseExecuteResul.size();i++) {
					if(caseExecuteResul.get(i)==1) {
						unexecuted=caseExecuteResul.get(i);
						caseExecuteResul.remove(i);
					}
				}
		 }
		map.put("unexecuted", unexecuted);
		 map.put("caseExecuteResultList", caseExecuteResul);
		return tblTestSetCaseMapper.selectBatchPass(map);
	}
	//测试案例执行弹窗
	@Override
	@Transactional(readOnly = true)
	public List<TblTestSetCaseStep> selectTestCaseRun(Long id) {
		 Map<String, Object> result=new HashMap<>();
		 List<TblTestSetCaseStep> list=tblTestSetCaseStepMapper.selectByPrimaryKey(id);
		/* PageInfo<TblTestSetCaseStep> pageInfo = new PageInfo<TblTestSetCaseStep>(list);
		 result.put("rows", list);
		 result.put("total", pageInfo.getPages());*/
		return list;
	}
	//测试案例执行
	@Override
	@Transactional(readOnly = false)
	public String insertSelective(String rows, String type, String enforcementResults,String excuteRemark,int excuteRound,Long testSetId, Long testUserId,String files) {
		Map<String, Object> result=new HashMap<>();
		List<Map<String,String>> list = (List<Map<String,String>>) JSONArray.parse(enforcementResults);
		result = (Map)JSON.parse(rows);  
		result.put("createBy", testUserId);
		//result.put("list", list);
		result.put("caseExecuteResult", type);
		result.put("excuteRemark", excuteRemark);
		result.put("excuteRound", excuteRound);
		result.put("testSetId", testSetId);
		int number=tblTestSetExecuteRoundCaseResult.updateSelective(result);//执行轮次结果
		tblTestSetCaseExecuteMapper.insertSelective(result);//添加执行结果
		if(!files.equals("")&&!files.equals(null)&&!files.equals("[]")) {
  			List<TblTestSetCaseExecuteAttachement> fileTask = JsonUtil.fromJson(files, JsonUtil.createCollectionType(ArrayList.class,TblTestSetCaseExecuteAttachement.class));
  			for(int i=0;i<fileTask.size();i++) {
  				Long execeteId=Long.parseLong( result.get("id").toString());
  				fileTask.get(i).setTestSetCaseExecuteId(execeteId);
  				fileTask.get(i).setCreateBy(testUserId);
  			}
  			tblTestSetCaseExecuteAttachementMapper.addAttachement(fileTask);
  		}
		for(int i=0;i<list.size();i++) {
			list.get(i).put("id", result.get("id").toString());
		}
		if(list.size()>0) {
			tblTestSetCaseStepExecuteMapper.insertSelective(list);//执行步骤结果
		}
		 
		if(number<1) {
			tblTestSetExecuteRoundCaseResult.insertSelective(result);//表中无数据添加
		}
			tblTestSetCaseMapper.updateExecuteResult(result);//更新测试案例执行结果	
			return result.get("id").toString();
	}
	//测试案例执行结果查询
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> selectTestExecute(Long testCaseExecuteId,Long testSetCaseId) {
		Map<String, Object> result=new HashMap<>();
		//TblTestSetCaseExecute tblTestSetCaseExecute=new TblTestSetCaseExecute();
		//tblTestSetCaseExecute =tblTestSetCaseExecuteMapper.findeSetCase(testCaseExecuteId);
		//result.put("tblTestSetCaseExecute", tblTestSetCaseExecute);
		List<TblTestSetCaseStepExecute>  list=tblTestSetCaseStepExecuteMapper.selectByPrimaryKey(testCaseExecuteId);
		TblTestSetCaseExecute listCase=tblTestSetCaseExecuteMapper.selectByID(testCaseExecuteId);
		String userName=tblUserInfoMapper.getUserNameById(listCase.getExecuteUserId());//执行人名称
		List<TblTestSetCaseExecuteAttachement> listFile= tblTestSetCaseExecuteAttachementMapper.selectAttchement(testCaseExecuteId);
		List<TblDefectInfo> listDefect=tblDefectInfoMapper.selectBytestCaseId(testCaseExecuteId);
		result.put("listCase", listCase);
		result.put("userName", userName);
		result.put("listStep", list);
		result.put("listFile", listFile);
		result.put("listDefect", listDefect);
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public  List<TblTestSetCaseExecute> selectTestCaseExecute(String myData) {
		TblTestSetCaseExecute  tblTestSetCaseExecute=JSON.parseObject(myData,TblTestSetCaseExecute.class);
		 List<TblTestSetCaseExecute> list=tblTestSetCaseExecuteMapper.findeSetCase(tblTestSetCaseExecute);
		return list;
	}
	
	
	@Override
	@Transactional(readOnly = false)
	public void delecteFile(Long id, Long userId) {
		Map<String, Object> result=new HashMap<>();
		result.put("id", id);
		result.put("lastUpdateBy", userId);
		tblTestSetCaseExecuteAttachementMapper.deleteFile(result);
	}
	/**
	 * 测试案例查看
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> selectUpdateCase(Long testSetCaseId) {
		Map<String, Object> result=new HashMap<>();
		List<TblTestSetCaseStep> list=tblTestSetCaseStepMapper.selectByPrimaryKey(testSetCaseId);
		result=tblTestSetCaseMapper.getTestSetCaseId(testSetCaseId);
		result.put("caseStep", list);
		return result;
	}
	/**
	 * 修改案例
	 * @throws Exception 
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateCaseStep(String testSetCase, String testCaseStep, Long testUserId) throws Exception {
		try {
			testCaseManageService.updateCaseStep(testSetCase,testCaseStep,testUserId);
		Map<String, Object> result=new HashMap<>();
		result = (Map)JSON.parse(testSetCase); 
		List<TblTestSetCaseStep> tblCaseStep = JsonUtil.fromJson(testCaseStep,
				JsonUtil.createCollectionType(ArrayList.class, TblTestSetCaseStep.class));
		result.put("testUserId", testUserId);
		tblTestSetCaseMapper.updatetestSetCase(result);
		Long caseId=Long.parseLong(result.get("id").toString());
		List<TblTestSetCaseStep> beforeList=tblTestSetCaseStepMapper.selectByPrimaryKey(caseId);
		List<Long> beforeIds = CollectionUtil.extractToList(beforeList, "id");
		if(tblCaseStep==null) {
			tblCaseStep=new ArrayList<TblTestSetCaseStep>();
		}
		List<Long> afterIds = CollectionUtil.extractToList(tblCaseStep, "id");
		List<Long> deleteIds = (List<Long>) CollectionUtil.getDiffent(beforeIds,afterIds);
		if (deleteIds.size()>0) {
			tblTestSetCaseStepMapper.updateStatus(deleteIds);
		}
		for(TblTestSetCaseStep i:tblCaseStep) {
			if(i.getId()!=null) {
				i.setLastUpdateBy(testUserId);
				tblTestSetCaseStepMapper.updateCaseStep(i);
			}else {
				i.setCreateBy(testUserId);
				i.setTestSetCaseId(caseId);
				i.setStatus(1);
				tblTestSetCaseStepMapper.insert(i);
			}
		}
		}catch (Exception e) {
			throw e;
		}
	}
	/**批量执行
	 * 
	 */
	@Override
	@Transactional(readOnly = false)
	public void uodateTestCaseExecute(String allExecuteDate, Long userId) {
		Map<String, Object> result=new HashMap<>();
		List<Map<String, Object>> tblCaseStep = JsonUtil.fromJson(allExecuteDate,
				JsonUtil.createCollectionType(ArrayList.class, Map.class));
		result.put("createBy", userId);
		result.put("list", tblCaseStep);
		for(int i=0;i<tblCaseStep.size();i++) {
			Map<String, Object> map=new HashMap<>();
			map=tblCaseStep.get(i);
			map.put("createBy", userId);
			tblTestSetExecuteRoundCaseResult.updateRoundResult(map);
			tblTestSetCaseExecuteMapper.insertCodeBatch(map);
			if(tblCaseStep.get(i).get("resultId")==null||tblCaseStep.get(i).get("resultId")=="") {
				Map<String, Object> roundResult=new HashMap<>();//添加执行表
				roundResult.put("tblCaseStep", tblCaseStep.get(i));
				roundResult.put("createBy", userId);
				tblTestSetExecuteRoundCaseResult.insertResltPass(roundResult);
			}
			Long id =Long.parseLong(tblCaseStep.get(i).get("setCaseId").toString());
			List<TblTestSetCaseStep> listStep=tblTestSetCaseStepMapper.selectByPrimaryKey(id);
			
			
			for(int j=0;j<listStep.size();j++) {
				listStep.get(j).setId(Long.parseLong(map.get("id").toString()));
			}
			if(listStep.size()>0) {
				tblTestSetCaseStepExecuteMapper.insertPass(listStep);
			}
			
		}
		
		tblTestSetCaseMapper.updateResult(result);
		
	}
	@Override
	@Transactional(readOnly = true)
	public List<TblTestSetCaseVo> findExeTestSetByexcuteRound(Long testSetId, int excuteRound) {
		List<TblTestSetCaseVo> list=new ArrayList<>();
		Map<String, Object> result=new HashMap<>();
		result.put("testSetId", testSetId);
		result.put("excuteRound", excuteRound);
		list= tblTestSetCaseMapper.selectByTestSetId(result);
		 Object object=redisUtils.get("TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT");
		 Map<String, Object> mapsource=new HashMap<>();
 		if (object != null &&!"".equals( object)) {//redis有直接从redis中取
 			mapsource = JSON.parseObject(object.toString());
 		}
 		 if(list.size()>0) {
			 for(int i=0;i<list.size(); i++) {
				 list.get(i).setExcuteRound("第"+excuteRound+"轮");
				 for(String key:mapsource.keySet()) {
					 if(list.get(i).getCaseExecuteResult()==null) {
						 list.get(i).setExecuteResult("未执行");
					 }else {
						 if(key.equals(list.get(i).getCaseExecuteResult().toString())) {
							 list.get(i).setExecuteResult(mapsource.get(key).toString());
						 }
					 } 
				 }
				 List<CaseStepVo> tscs= tblTestSetCaseStepMapper.findByPrimaryKey(list.get(i).getId());
				 list.get(i).setCaseStep(tscs);
			 }
		 }
		 return list;
	}
	@Override
	@Transactional(readOnly = true)
	public List<TblTestSetCaseVo> findExeTestSet(Long testSetId, int excuteRound[]) {
		List<TblTestSetCaseVo> list=new ArrayList<TblTestSetCaseVo>();
		
		Object object=redisUtils.get("TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT");
		Map<String, Object> mapsource=new HashMap<>();
 		if (object != null &&!"".equals( object)) {//redis有直接从redis中取
 			mapsource = JSON.parseObject(object.toString());
 		}
		for(int i=0;i<excuteRound.length;i++) {
			Map<String, Object> result=new HashMap<>();
			result.put("testSetId", testSetId);
			result.put("excuteRound", excuteRound[i]);
			
			List<TblTestSetCaseVo> list2=tblTestSetCaseMapper.selectByTestSetId(result);
			 if(list2.size()>0) {
				 for(int j=0;j<list2.size(); j++) {
					 list2.get(j).setExcuteRound("第"+excuteRound[i]+"轮");
					 for(String key:mapsource.keySet()) {
						 if(list2.get(j).getCaseExecuteResult()==null) {
							 list2.get(j).setExecuteResult("未执行");
						 }else {
							 if(key.equals(list2.get(j).getCaseExecuteResult().toString())) {
								 list2.get(j).setExecuteResult(mapsource.get(key).toString());
							 }
						 } 
					 }
				 }
				 list.addAll(list2);
			 }
		}
		for(TblTestSetCaseVo caseVo:list) {
			List<CaseStepVo> tscs= tblTestSetCaseStepMapper.findByPrimaryKey(caseVo.getId());
			caseVo.setCaseStep(tscs);
		}
		return list;
	}
	@Override
	public List<TblTestSetCaseExecute> selectByTestSet(Long testSetId) {
		
		return tblTestSetCaseExecuteMapper.selectByTestSet(testSetId);
	}
	
	//获取案例状态
	@Override
	public Long getCaseExecuteResult(Long testSetId, String caseNumber) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<>();
		map.put("testSetId",testSetId);
		map.put("caseNumber", caseNumber);
		return tblTestSetCaseMapper.getCaseExecuteResult(map);
	}
	
	//获取挂起和撤销的执行记录
	@Override
	public List<TblTestSetCaseExecute> getCaseExecute(Long testSetId, String caseNumber) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<>();
		map.put("testSetId", testSetId);
		map.put("caseNumber", caseNumber);
		List<TblTestSetCaseExecute> list = tblTestSetCaseExecuteMapper.getCaseExecute(map);
		for (TblTestSetCaseExecute tblTestSetCaseExecute : list) {
			Integer integer = tblTestSetCaseExecute.getCaseExecuteResult();
			if(integer != null) {
				String key = integer.toString();
			String string = redisUtils.get("TBL_TEST_SET_CASE_CASE_EXECUTE_RESULT").toString();
			JSONObject jsonObject = JSON.parseObject(string);
			String result = jsonObject.get(key).toString();
			tblTestSetCaseExecute.setExecuteResult(result);
			}
		}
		return list;
	}
	
	
	
}
