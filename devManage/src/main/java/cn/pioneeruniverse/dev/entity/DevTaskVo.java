package cn.pioneeruniverse.dev.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.common.utils.ExcelField;
import cn.pioneeruniverse.common.utils.RedisUtils;


/**
 *  开发任务Vo(需求特性表)
 * @author:tingting
 * @version:2018年11月15日 上午11:01:08
 */
public class DevTaskVo extends BaseEntity{

	private static final long serialVersionUID = 1L;

	private String featureName;

	private String featureCode;

	private String featureOverview;

	private Long projectId;

	private String projectName;

	private Long sprintId;

	private Long systemId;

	private Integer createType;

	private Long requirementId;

	private String requirementFeatureStatus;

	private String deployStatus;//部署状态

	private String deployStatusName;//部署状态

	private Integer manageUserId;

	private Integer executeUserId;

	private String temporaryStatus;//临时任务 （1=是  2=否）

	private Integer deptId;

	private String handleSuggestion;

	private Date planStartDate;

	private Date planEndDate;

	private Double estimateWorkload;

	private Double estimateRemainWorkload;	//预估剩余工作量（人天）

	private Date actualStartDate;

	private Date actualEndDate;

	private Double actualWorkload;

	private Long commissioningWindowId;//投产窗口（计划版本）

	private Integer requirementFeaturePriority;//任务优先级

	private String statusName;

	private String systemName;

	private String requirementCode;

	private String deptName;

	private String manageUserName;

	private String executeUserName;

	private String windowName;

	private Long systemVersionId;

	private String systemVersionName;

	private String systemScmBranch;

	private Long executeProjectGroupId;

	private Integer developmentMode;

	private Long assetSystemTreeId;

	private Long repairSystemVersionId;

	private String commissioningWindowIds;//用来存放 以，分割的投产窗口id字符串
	private String manageUserIds;//用来存放 以，分割的管理人id字符串
	private String requirementIds;//用来存放 以，分割的需求id字符串
	private String systemIds;//用来存放 以，分割的系统id字符串
	private String executeUserIds;//用来存放 以，分割的执行人id字符串
	private String  reqFeaturePrioritys;//任务优先级
	private String  sprints;//冲刺
	private List<ExtendedField> extendedFields;

	public List<ExtendedField> getExtendedFields() {
		return extendedFields;
	}

	public void setExtendedFields(List<ExtendedField> extendedFields) {
		this.extendedFields = extendedFields;
	}

	@ExcelField(title="系统模块", type=1, align=1, sort=90)
	public String getSystemTreeName() {

		return systemTreeName;
	}

	public void setSystemTreeName(String systemTreeName) {
		this.systemTreeName = systemTreeName;
	}
	@ExcelField(title="创建方式", type=1, align=1, sort=90)
	public String getCreateTypeName() {
		if("1".equals(createTypeName)){
			return "自建";
		}else if("2".equals(createTypeName)){
			return "同步";
		}else{
			return "";
		}


	}


	public void setCreateTypeName(String createTypeName) {
		this.createTypeName = createTypeName;
	}

	private String systemTreeName;//系统树名称
	private String createTypeName;

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	private Long taskId;

	private String requirementFeatureSourceName ;
	@ExcelField(title="任务来源", type=1, align=1, sort=90)
	public String getRequirementFeatureSourceName() {
		if("1".equals(requirementFeatureSourceName)){
			requirementFeatureSourceName="业务需求";
		}else if("2".equals(requirementFeatureSourceName)){
			requirementFeatureSourceName="生产问题";
		}else{
			requirementFeatureSourceName="";
		}
		return requirementFeatureSourceName;
	}

	public void setRequirementFeatureSourceName(String requirementFeatureSourceName) {
		this.requirementFeatureSourceName = requirementFeatureSourceName;
	}
	@ExcelField(title="生产问题单编号", type=1, align=1, sort=90)
	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getRequirementFeaturePriorityName() {
		return requirementFeaturePriorityName;
	}

	public void setRequirementFeaturePriorityName(String requirementFeaturePriorityName) {
		this.requirementFeaturePriorityName = requirementFeaturePriorityName;
	}
	@ExcelField(title="系统版本", type=1, align=1, sort=90)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getScmBranch() {
		return scmBranch;
	}

	public void setScmBranch(String scmBranch) {
		this.scmBranch = scmBranch;
	}
	@ExcelField(title="修复版本", type=1, align=1, sort=90)
	public String getRepairVersion() {
		return repairVersion;
	}

	public void setRepairVersion(String repairVersion) {
		this.repairVersion = repairVersion;
	}
	@ExcelField(title="故事点", type=1, align=1, sort=90)
	public String getStoryPoint() {
		return storyPoint;
	}

	public void setStoryPoint(String storyPoint) {
		this.storyPoint = storyPoint;
	}

	private  String questionNumber;
	private String requirementFeaturePriorityName;
	private String version;
	private String scmBranch;
	private String repairVersion;
	private String storyPoint;

	public String getFieldTemplate() {
		return fieldTemplate;
	}

	public void setFieldTemplate(String fieldTemplate) {
		this.fieldTemplate = fieldTemplate;
	}

	private String fieldTemplate;



	private List<String> roleCodes = new ArrayList<>();//当前登录用户所有的角色code


	@Transient
	@JsonFormat(pattern = "yyyyMMdd", timezone="GMT+8")
	private Date windowDate;

	private List<Map<String, Object>> brothers = new ArrayList<>();//兄弟开发任务

	private List<TblDevTask> devTasks = new ArrayList<>();//下属工作任务

	private List<Map<String, Object>> workStatusCount = new ArrayList<>();//下属工作任务各个状态的数量

	@Transient
	private String sprintName; //冲刺名称

	@Transient
	private String sidx; //排序字段
	
	@Transient
	private String sord; //排序顺序


	public Integer getDevelopmentMode() {
		return developmentMode;
	}

	public void setDevelopmentMode(Integer developmentMode) {
		this.developmentMode = developmentMode;
	}

	public String getSprints() {
		return sprints;
	}

	public void setSprints(String sprints) {
		this.sprints = sprints;
	}

	public List<String> getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(List<String> roleCodes) {
		this.roleCodes = roleCodes;
	}

	public String getReqFeaturePrioritys() {
		return reqFeaturePrioritys;
	}

	public void setReqFeaturePrioritys(String reqFeaturePrioritys) {
		this.reqFeaturePrioritys = reqFeaturePrioritys;
	}

	public Integer getRequirementFeaturePriority() {
		return requirementFeaturePriority;
	}

	public void setRequirementFeaturePriority(Integer requirementFeaturePriority) {
		this.requirementFeaturePriority = requirementFeaturePriority;
	}

	public List<Map<String, Object>> getWorkStatusCount() {
		return workStatusCount;
	}

	public void setWorkStatusCount(List<Map<String, Object>> workStatusCount) {
		this.workStatusCount = workStatusCount;
	}

	public List<TblDevTask> getDevTasks() {
		return devTasks;
	}

	public void setDevTasks(List<TblDevTask> devTasks) {
		this.devTasks = devTasks;
	}

	public String getCommissioningWindowIds() {
		return commissioningWindowIds;
	}

	public void setCommissioningWindowIds(String commissioningWindowIds) {
		this.commissioningWindowIds = commissioningWindowIds;
	}

	public String getManageUserIds() {
		return manageUserIds;
	}

	public void setManageUserIds(String manageUserIds) {
		this.manageUserIds = manageUserIds;
	}

	public String getRequirementIds() {
		return requirementIds;
	}

	public void setRequirementIds(String requirementIds) {
		this.requirementIds = requirementIds;
	}

	public String getSystemIds() {
		return systemIds;
	}

	public void setSystemIds(String systemIds) {
		this.systemIds = systemIds;
	}

	public String getExecuteUserIds() {
		return executeUserIds;
	}

	public void setExecuteUserIds(String executeUserIds) {
		this.executeUserIds = executeUserIds;
	}

	private Long userId;

	public String getDeployStatusName() {
		return deployStatusName;
	}

	public void setDeployStatusName(String deployStatusName) {
		this.deployStatusName = deployStatusName;
	}

	public String getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}

	public Long getSystemVersionId() {
		return systemVersionId;
	}

	public void setSystemVersionId(Long systemVersionId) {
		this.systemVersionId = systemVersionId;
	}

	public String getSystemVersionName() {
		return systemVersionName;
	}

	public void setSystemVersionName(String systemVersionName) {
		this.systemVersionName = systemVersionName;
	}

	public String getSystemScmBranch() {
		return systemScmBranch;
	}

	public void setSystemScmBranch(String systemScmBranch) {
		this.systemScmBranch = systemScmBranch;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@ExcelField(title="任务名称", type=1, align=1, sort=20)
	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	@ExcelField(title="任务编号", type=1, align=1, sort=10)
	public String getFeatureCode() {
		return featureCode;
	}

	public void setFeatureCode(String featureCode) {
		this.featureCode = featureCode;
	}

	@ExcelField(title="任务描述", type=1, align=1, sort=20)
	public String getFeatureOverview() {
		return featureOverview;
	}

	public void setFeatureOverview(String featureOverview) {
		this.featureOverview = featureOverview;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public Long getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(Long requirementId) {
		this.requirementId = requirementId;
	}

	public String getRequirementFeatureStatus() {
		return requirementFeatureStatus;
	}

	public void setRequirementFeatureStatus(String requirementFeatureStatus) {
		this.requirementFeatureStatus = requirementFeatureStatus;
	}

	public Integer getManageUserId() {
		return manageUserId;
	}

	public void setManageUserId(Integer manageUserId) {
		this.manageUserId = manageUserId;
	}

	public Integer getExecuteUserId() {
		return executeUserId;
	}

	public void setExecuteUserId(Integer executeUserId) {
		this.executeUserId = executeUserId;
	}

	@ExcelField(title="临时任务", type=1, align=1, sort=160)
	public String getTemporaryStatus() {
		if("1".equals(temporaryStatus)){
			temporaryStatus =  "是";
		}else if("2".equals(temporaryStatus)){
			temporaryStatus =  "否";
		}
		return temporaryStatus;
	}

	public void setTemporaryStatus(String temporaryStatus) {
		this.temporaryStatus = temporaryStatus;

	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	@ExcelField(title="处理意见", type=1, align=1, sort=90)
	public String getHandleSuggestion() {
		return handleSuggestion;
	}

	public void setHandleSuggestion(String handleSuggestion) {
		this.handleSuggestion = handleSuggestion;
	}

	@ExcelField(title="预计开始时间", type=1, align=1, sort=100)
	public Date getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(Date planStartDate) {
		this.planStartDate = planStartDate;
	}
	@ExcelField(title="预计结束时间", type=1, align=1, sort=110)
	public Date getPlanEndDate() {
		return planEndDate;
	}

	public void setPlanEndDate(Date planEndDate) {
		this.planEndDate = planEndDate;
	}

	@ExcelField(title="预计工作量", type=1, align=1, sort=120)
	public Double getEstimateWorkload() {
		return estimateWorkload;
	}

	public void setEstimateWorkload(Double estimateWorkload) {
		this.estimateWorkload = estimateWorkload;
	}

	@ExcelField(title="实际开始时间", type=1, align=1, sort=130)
	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	@ExcelField(title="实际结束时间", type=1, align=1, sort=140)
	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	@ExcelField(title="实际工作量", type=1, align=1, sort=150)
	public Double getActualWorkload() {
		return actualWorkload;
	}

	public void setActualWorkload(Double actualWorkload) {
		this.actualWorkload = actualWorkload;
	}

	public Long getCommissioningWindowId() {
		return commissioningWindowId;
	}

	public void setCommissioningWindowId(Long commissioningWindowId) {
		this.commissioningWindowId = commissioningWindowId;
	}

	@ExcelField(title="任务状态", type=1, align=1, sort=30)
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	@ExcelField(title="涉及系统", type=1, align=1, sort=40)
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	@ExcelField(title="关联需求", type=1, align=1, sort=50)
	public String getRequirementCode() {
		return requirementCode;
	}

	public void setRequirementCode(String requirementCode) {
		this.requirementCode = requirementCode;
	}

	@ExcelField(title="所属处室", type=1, align=1, sort=60)
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	@ExcelField(title="开发管理岗", type=1, align=1, sort=70)
	public String getManageUserName() {
		return manageUserName;
	}


	public void setManageUserName(String manageUserName) {
		this.manageUserName = manageUserName;
	}
	@ExcelField(title="执行人", type=1, align=1, sort=80)
	public String getExecuteUserName() {
		return executeUserName;
	}

	public void setExecuteUserName(String executeUserName) {
		this.executeUserName = executeUserName;
	}

	@ExcelField(title="投产窗口", type=1, align=1, sort=90)
	public String getWindowName() {
		return windowName;
	}

	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}

	public List<Map<String, Object>> getBrothers() {
		return brothers;
	}

	public void setBrothers(List<Map<String, Object>> brothers) {
		this.brothers = brothers;
	}

	public Integer getCreateType() {
		return  createType;

	}

	public void setCreateType(Integer createType) {
		this.createType = createType;
	}

	public Date getWindowDate() {
		return windowDate;
	}

	public void setWindowDate(Date windowDate) {
		this.windowDate = windowDate;
	}

	public String getSprintName() {
		return sprintName;
	}

	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}
	public Long getExecuteProjectGroupId() {
		return executeProjectGroupId;
	}

	public void setExecuteProjectGroupId(Long executeProjectGroupId) {
		this.executeProjectGroupId = executeProjectGroupId;
	}
	public Long getSprintId() {
		return sprintId;
	}

	public void setSprintId(Long sprintId) {
		this.sprintId = sprintId;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}


	public Long getRepairSystemVersionId() {
		return repairSystemVersionId;
	}

	public void setRepairSystemVersionId(Long repairSystemVersionId) {
		this.repairSystemVersionId = repairSystemVersionId;
	}

	public Long getAssetSystemTreeId() {
		return assetSystemTreeId;
	}

	public void setAssetSystemTreeId(Long assetSystemTreeId) {
		this.assetSystemTreeId = assetSystemTreeId;
	}

	@ExcelField(title="预估剩余工作量", type=1, align=1, sort=120)
	public Double getEstimateRemainWorkload() {
		return estimateRemainWorkload;
	}

	public void setEstimateRemainWorkload(Double estimateRemainWorkload) {
		this.estimateRemainWorkload = estimateRemainWorkload;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public String toString() {
		return "DevTaskVo{" +
				"featureName='" + featureName + '\'' +
				", featureCode='" + featureCode + '\'' +
				", featureOverview='" + featureOverview + '\'' +
				", projectId=" + projectId +
				", sprintId=" + sprintId +
				", systemId=" + systemId +
				", createType=" + createType +
				", requirementId=" + requirementId +
				", requirementFeatureStatus='" + requirementFeatureStatus + '\'' +
				", deployStatus='" + deployStatus + '\'' +
				", deployStatusName='" + deployStatusName + '\'' +
				", manageUserId=" + manageUserId +
				", executeUserId=" + executeUserId +
				", temporaryStatus='" + temporaryStatus + '\'' +
				", deptId=" + deptId +
				", handleSuggestion='" + handleSuggestion + '\'' +
				", planStartDate=" + planStartDate +
				", planEndDate=" + planEndDate +
				", estimateWorkload=" + estimateWorkload +
				", estimateRemainWorkload=" + estimateRemainWorkload +
				", actualStartDate=" + actualStartDate +
				", actualEndDate=" + actualEndDate +
				", actualWorkload=" + actualWorkload +
				", commissioningWindowId=" + commissioningWindowId +
				", requirementFeaturePriority=" + requirementFeaturePriority +
				", statusName='" + statusName + '\'' +
				", systemName='" + systemName + '\'' +
				", requirementCode='" + requirementCode + '\'' +
				", deptName='" + deptName + '\'' +
				", manageUserName='" + manageUserName + '\'' +
				", executeUserName='" + executeUserName + '\'' +
				", windowName='" + windowName + '\'' +
				", systemVersionId=" + systemVersionId +
				", systemVersionName='" + systemVersionName + '\'' +
				", systemScmBranch='" + systemScmBranch + '\'' +
				", executeProjectGroupId=" + executeProjectGroupId +
				", developmentMode=" + developmentMode +
				", assetSystemTreeId=" + assetSystemTreeId +
				", repairSystemVersionId=" + repairSystemVersionId +
				", commissioningWindowIds='" + commissioningWindowIds + '\'' +
				", manageUserIds='" + manageUserIds + '\'' +
				", requirementIds='" + requirementIds + '\'' +
				", systemIds='" + systemIds + '\'' +
				", executeUserIds='" + executeUserIds + '\'' +
				", reqFeaturePrioritys='" + reqFeaturePrioritys + '\'' +
				", sprints='" + sprints + '\'' +
				", extendedFields=" + extendedFields +
				", systemTreeName='" + systemTreeName + '\'' +
				", createTypeName='" + createTypeName + '\'' +
				", taskId=" + taskId +
				", requirementFeatureSourceName='" + requirementFeatureSourceName + '\'' +
				", questionNumber='" + questionNumber + '\'' +
				", requirementFeaturePriorityName='" + requirementFeaturePriorityName + '\'' +
				", version='" + version + '\'' +
				", scmBranch='" + scmBranch + '\'' +
				", repairVersion='" + repairVersion + '\'' +
				", storyPoint='" + storyPoint + '\'' +
				", fieldTemplate='" + fieldTemplate + '\'' +
				", roleCodes=" + roleCodes +
				", windowDate=" + windowDate +
				", brothers=" + brothers +
				", devTasks=" + devTasks +
				", workStatusCount=" + workStatusCount +
				", sprintName='" + sprintName + '\'' +
				", sidx='" + sidx + '\'' +
				", sord='" + sord + '\'' +
				", userId=" + userId +
				'}';
	}
}
