package cn.pioneeruniverse.project.service.requirement.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.project.dao.mybatis.RequirementFeatureHistoryMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementFeatureMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementFeatureTimeTraceMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementMapper;
import cn.pioneeruniverse.project.dao.mybatis.SprintBurnoutMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblRequirementFeatureAttentionMapper;
import cn.pioneeruniverse.project.entity.TblRequirementFeature;
import cn.pioneeruniverse.project.entity.TblRequirementFeatureHistory;
import cn.pioneeruniverse.project.entity.TblRequirementFeatureTimeTrace;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.entity.TblSprintBurnout;
import cn.pioneeruniverse.project.feignInterface.DevTaskInterface;
import cn.pioneeruniverse.project.feignInterface.ProjectToSystemInterface;
import cn.pioneeruniverse.project.feignInterface.TestTaskInterface;
import cn.pioneeruniverse.project.service.requirement.RequirementFeatureService;

@Transactional(readOnly = true)
@Service("requirementFeatureService")
public class RequirementFeatureServiceImpl implements RequirementFeatureService {

    @Autowired
    private DevTaskInterface taskInterface;
    @Autowired
    private TestTaskInterface testInterface;
    @Autowired
    private RequirementMapper tblRequirementInfoMapper;
    @Autowired
    private RequirementFeatureMapper requirementFeatureMapper;
    @Autowired
    private RequirementFeatureHistoryMapper requirementFeatureHistoryMapper;
    @Autowired
    private RequirementFeatureTimeTraceMapper requirementFeatureTimeTraceMapper;
    @Autowired
    private SprintBurnoutMapper sprintBurnoutMapper;
    @Autowired
    private ProjectToSystemInterface projectToSystemInterface;
    @Autowired
    private TblRequirementFeatureAttentionMapper tblRequirementFeatureAttentionMapper;
   

    @Override
    public List<TblRequirementFeature> findFeatureByRequirementId(Long id) {
        return requirementFeatureMapper.findFeatureByRequirementId(id);
    }

    @Override
    public List<TblRequirementFeature> getAllFeature(String featureName) {
        return requirementFeatureMapper.getAllFeature(featureName);
    }

    //?????????????????????itmp_db
    @Override
    @Transactional(readOnly = false)
    public void updateTaskDataItmp(TblRequirementFeature feature,String reqId) {
        feature.setRequirementId(Long.valueOf(reqId));
        TblRequirementInfo tblRequirementInfo = tblRequirementInfoMapper.findRequirementById1(Long.valueOf(reqId));
        TblRequirementFeature feature1 = requirementFeatureMapper.getFeatureByCode(feature.getFeatureCode());
        TblRequirementFeatureTimeTrace featureTimeTrace = new TblRequirementFeatureTimeTrace();

        if (feature1==null) {
            if(tblRequirementInfo.getRequirementType()!=null) {
                feature.setDevelopmentDeptId(tblRequirementInfo.getDevelopmentDeptId());
            }
            if(!feature.getRequirementFeatureStatus().equals("00")) {
                feature.setRequirementFeatureStatus("01");
                requirementFeatureMapper.insertFeature(feature);
                featureTimeTrace.setRequirementFeatureId(feature.getId());
                featureTimeTrace.setRequirementFeatureCreateTime(feature.getCreateDate());
                featureTimeTrace.setStatus(1);
                featureTimeTrace.setCreateDate(new Timestamp(new Date().getTime()));
                featureTimeTrace.setLastUpdateDate(new Timestamp(new Date().getTime()));
                requirementFeatureTimeTraceMapper.insertFeatureTimeTrace(featureTimeTrace);
                //????????????
                sendAddMessage(feature);
                //???????????????????????????????????????????????????????????????????????? --ztt
                if (feature.getManageUserId()!=null || feature.getExecuteUserId()!=null) {
                	Map<String,Object> emWeMap = new HashMap<String, Object>();
            		emWeMap.put("messageTitle", "???IT???????????????????????????- ????????????????????????????????????");
            		emWeMap.put("messageContent","?????????????????????????????????????????????"+ feature.getFeatureCode()+" | "+feature.getFeatureName()+"????????????????????????");
            		emWeMap.put("messageReceiver",feature.getManageUserId()+","+feature.getExecuteUserId()+"," );//????????? ??????????????????????????? ???????????????
            		emWeMap.put("sendMethod", 3);//???????????? 3 ???????????????
            		projectToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
				}
        		
            }
        } else {
            if(feature.getRequirementFeatureStatus().equals("00")) {
                feature.setId(feature1.getId());
                requirementFeatureMapper.updateStatusById(feature);//?????????????????????????????????
                taskInterface.cancelStatusReqFeature(feature.getId());//?????????????????????????????????
            }else{
                feature.setId(feature1.getId());
                feature.setCreateBy(null);
                feature.setCreateDate(null);
                feature.setRequirementFeatureStatus(null);
                requirementFeatureMapper.updateFeatureById(feature);
                //????????????
                sendAddMessage(feature);
            }
            //??????????????? ???????????????????????????????????????????????????????????????????????????????????? --ztt
    		String userIds = tblRequirementFeatureAttentionMapper.getUserIdsByReqFeatureId(feature.getId());
    		if(StringUtils.isNotBlank(userIds)) {
    			Map<String,Object> emWeMap = new HashMap<String, Object>();
    			emWeMap.put("messageTitle", "???IT???????????????????????????- ???????????????????????????");
    			emWeMap.put("messageContent","???????????????"+ feature.getFeatureCode()+" | "+feature.getFeatureName()+"????????????????????????????????????");
    			emWeMap.put("messageReceiver",userIds );//????????? ?????????????????????
    			emWeMap.put("sendMethod", 3);//???????????? 3 ???????????????
    			projectToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
    		}

        }



    }



    private void sendAddMessage( TblRequirementFeature tblRequirementFeature){

        Map<String,Object> map=new HashMap<>();
        map.put("messageTitle","????????????????????????????????????");
        map.put("messageContent",tblRequirementFeature.getFeatureCode()+"|"+tblRequirementFeature.getFeatureName());
        map.put("messageReceiverScope",2);
        String userId="";
        if(tblRequirementFeature.getExecuteUserId()!=null){
            userId=userId+tblRequirementFeature.getExecuteUserId()+",";
        }
        if(tblRequirementFeature.getManageUserId()!=null){
            userId=userId+tblRequirementFeature.getManageUserId()+",";
        }
        if(!userId.equals("")) {
            userId = userId.substring(0, userId.length() - 1);
            map.put("messageReceiver", userId);
            projectToSystemInterface.insertMessage(JSON.toJSONString(map));
        }




    }



    //?????????????????????tmp_db
    @Override
    @DataSource(name="tmpDataSource")
    @Transactional(readOnly = false)
    public void updateTaskDataTmp(TblRequirementFeature feature,String reqId) {
        TblRequirementInfo tblRequirementInfo = tblRequirementInfoMapper.findRequirementById1(Long.valueOf(reqId));
        feature.setRequirementId(Long.valueOf(reqId));
        if(tblRequirementInfo.getRequirementType()!=null) {
            feature.setRequirementFeatureSource(putFeatureSource(tblRequirementInfo.getRequirementType()));
            feature.setDevelopmentDeptId(tblRequirementInfo.getDevelopmentDeptId());
        }
        TblRequirementFeature feature1 = requirementFeatureMapper.getFeatureByCode(feature.getFeatureCode());
        if (feature1==null) {
            if(!feature.getRequirementFeatureStatus().equals("00")) {
                feature.setRequirementFeatureStatus("11");
                requirementFeatureMapper.insertFeature(feature);
            }
        } else {
            if(feature.getRequirementFeatureStatus().equals("00")) {
                feature.setId(feature1.getId());
                requirementFeatureMapper.updateStatusById(feature);
                testInterface.cancelStatusReqFeature(feature.getId());
            }else{
                feature.setId(feature1.getId());
                feature.setCreateBy(null);
                feature.setCreateDate(null);
                feature.setRequirementFeatureStatus(null);
                requirementFeatureMapper.updateFeatureById(feature);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateStatusItmp(TblRequirementFeature feature) {
        TblRequirementFeature feature1 = requirementFeatureMapper.getFeatureByCode(feature.getFeatureCode());
        if(feature1!=null){
            feature.setId(feature1.getId());
            requirementFeatureMapper.updateStatusById(feature);
            //??????????????? ?????????????????????????????????????????????????????????????????????????????? --ztt
    		String userIds = tblRequirementFeatureAttentionMapper.getUserIdsByReqFeatureId(feature1.getId());
    		if(StringUtils.isNotBlank(userIds)) {
    			Map<String,Object> emWeMap = new HashMap<String, Object>();
    			emWeMap.put("messageTitle", "???IT???????????????????????????- ???????????????????????????");
    			emWeMap.put("messageContent","???????????????"+ feature1.getFeatureCode()+" | "+feature1.getFeatureName()+"????????????????????????????????????");
    			emWeMap.put("messageReceiver",userIds );//????????? ?????????????????????
    			emWeMap.put("sendMethod", 3);//???????????? 3 ???????????????
    			projectToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
    		}
    		
    		 taskInterface.cancelStatusReqFeature(feature1.getId());
        }
    }

    @Override
    @DataSource(name="tmpDataSource")
    @Transactional(readOnly = false)
    public void updateStatusByTmp(TblRequirementFeature feature) {
        TblRequirementFeature feature1 = requirementFeatureMapper.getFeatureByCode(feature.getFeatureCode());
        if(feature1!=null){
            feature.setId(feature1.getId());
            requirementFeatureMapper.updateStatusById(feature);
            testInterface.cancelStatusReqFeature(feature1.getId());
        }
    }

    private Long putFeatureSource(String reqType){
        int testSource=0;
        if(reqType.toUpperCase(Locale.ENGLISH).equals("POLICYREGULATION")){
            testSource=1;
        }else if(reqType.toUpperCase(Locale.ENGLISH).equals("SUPPORTSALES")){
            testSource=4;
        }else if(reqType.toUpperCase(Locale.ENGLISH).equals("RISKCONTROL")){
            testSource=3;
        }else if(reqType.toUpperCase(Locale.ENGLISH).equals("MANAGEMENTOPTIMIZATION")){
            testSource=5;
        }else if(reqType.toUpperCase(Locale.ENGLISH).equals("OTHER")){
            testSource=7;
        }
        return Long.valueOf(testSource);
    }

	@Override
    @Transactional(readOnly = false)
	public void executeFeatureToHistoryJob(String parameterJson) {
		Date date = new Date();
		String dateStr = DateUtil.formatDate(date);
		String selectDate = "";
		String startDate = "";
		String endDate = "";
		if (StringUtil.isNotEmpty(parameterJson)) {
			Map<String, String> parameterMap = JSON.parseObject(parameterJson, Map.class);
			selectDate = parameterMap.get("selectDate");
			startDate = parameterMap.get("startDate");
			endDate = parameterMap.get("endDate");
		}
		
		List<String> dateList = new ArrayList<String>();
		if (StringUtil.isNotEmpty(startDate) && StringUtil.isNotEmpty(endDate)) {
			Date start = DateUtil.parseDate(startDate);
			Date end = DateUtil.parseDate(endDate);
			while (start.compareTo(end) <= 0) {
				dateList.add(startDate);
				startDate = DateUtil.getNextDate(startDate);
				start = DateUtil.parseDate(startDate);
			}
		} else if (StringUtil.isNotEmpty(selectDate)) {
			dateList.add(selectDate);
		} else {
			dateList.add(dateStr);
		}
		
		for (String currentDate : dateList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("currentDate", currentDate);
			//?????????tbl_requirement_feature????????? where??????????????????????????????????????????????????????status=1 ??????????????????tbl_sprint_info?????????????????????
			List<TblRequirementFeature> featureList = requirementFeatureMapper.getFeatureToHistoryList(map);
			
			List<TblRequirementFeatureHistory> newFeatureHistoryList = new ArrayList<TblRequirementFeatureHistory>();
			for (TblRequirementFeature feature : featureList) {
				TblRequirementFeatureHistory newHistory = new TblRequirementFeatureHistory();
				newHistory.setRequirementFeatureId(feature.getId());
				newHistory.setAssetSystemTreeId(feature.getAssetSystemTreeId());
				newHistory.setEstimateRemainWorkload(feature.getEstimateRemainWorkload());
				newHistory.setCommissioningWindowId(feature.getCommissioningWindowId());
				newHistory.setSprintId(feature.getSprintId());
				newHistory.setProjectPlanId(feature.getProjectPlanId());
				newHistory.setRequirementFeatureStatus(feature.getRequirementFeatureStatus());
				newHistory.setStatus(1);
				newHistory.setCreateDate(date);
				newFeatureHistoryList.add(newHistory);
			}
			
			//???????????????
			EntityWrapper<TblRequirementFeatureHistory> featureHistoryWrapper = new EntityWrapper<TblRequirementFeatureHistory>();
			featureHistoryWrapper.eq("CREATE_DATE", currentDate);
			requirementFeatureHistoryMapper.delete(featureHistoryWrapper);
			for (TblRequirementFeatureHistory newHistory : newFeatureHistoryList) {
				requirementFeatureHistoryMapper.insert(newHistory);
			}
			
			//????????????????????????
			List<TblRequirementFeatureHistory> workloadList = requirementFeatureHistoryMapper.getFeatureHistoryWorkloadList(map);
			List<TblSprintBurnout> newSprintBurnoutList = new ArrayList<TblSprintBurnout>();
			for (TblRequirementFeatureHistory workload : workloadList) {
				TblSprintBurnout newSprintBurnout = new TblSprintBurnout();
				newSprintBurnout.setSprintId(workload.getSprintId());
				newSprintBurnout.setEstimateRemainWorkload(workload.getEstimateRemainWorkload());
				newSprintBurnout.setCreateDate(date);
				newSprintBurnoutList.add(newSprintBurnout);
			}
			
			//?????????????????????????????????????????????
			EntityWrapper<TblSprintBurnout> sprintBurnoutWrapper = new EntityWrapper<TblSprintBurnout>();
			sprintBurnoutWrapper.eq("CREATE_DATE", currentDate);
			sprintBurnoutMapper.delete(sprintBurnoutWrapper);
			for (TblSprintBurnout newSprintBurnout : newSprintBurnoutList) {
				sprintBurnoutMapper.insert(newSprintBurnout);
			}
		}
	}
}


