package cn.pioneeruniverse.dev.service.build.impl;

import java.util.Map;

import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.dev.entity.TblSystemModule;

public class JenkinsBase {
	
	protected String BUILD_TYPE = "buildType";//构建类型
	protected String BUILD_TYPE_AUTO = "auto";//自动构建
	protected String BUILD_TYPE_AUTO_DEPLOY = "autoDeploy";//自动部署
	protected String BUILD_TYPE_PACKAGE_AUTO_DEPLOY = "packageAutoDeploy";//包件自动部署
	protected String BUILD_TYPE_MANUAL = "manual";//手动构建 
	protected String BUILD_TYPE_MANUAL_DEPLOY = "manualDeploy";//手动部署
	protected String BUILD_DEPLOY_SUCCESS = "BUILD_DEPLOY_SUCCESS";//部署成功标示，用于日志判断
	protected String BUILD_DEPLOY_FAILURE = "BUILD_DEPLOY_FAILURE";
	
	protected String callBackJenkinsLog = "callBackJenkinsLog";//自动构建回调
	protected String callBackManualJenkins = "callBackManualJenkins";//手动构建回调
	protected String callBackManualDepolyJenkins = "callBackManualDepolyJenkins";//手动部署回调
	protected String callBackAutoDepolyJenkins = "callBackAutoDepolyJenkins";//自动部署回调
	protected String callBackPackageDepolyJenkins = "callBackPackageDepolyJenkins";//包件自动部署回调
	
	protected String scriptInsertMsg = "自动同步信息";
	
	protected String uploadTemp = "uploadTemp";//上传到PRO服务器暂存：config document sql
	protected String uploadConfigurationPath = "uploadTemp/configuration";
	protected String uploadDocumentPath = "uploadTemp/document";
	protected String uploadPackagePath = "uploadTemp/package";
	protected String uploadSqlPath = "uploadTemp/sql";
//	protected String shellNoPrint = " > /dev/null 2>&1 & ";//
//	protected String shellNoPrint = " ";
	
	protected String beforeStopGroovy = "BeforeStop";
	protected String afterStopGroovy = "AfterStop";
	protected String afterStartUpGroovy = "AfterStartUp";
	
	
	protected String SUFFIX_TITLE_1 = "====================";
	protected String SUFFIX_TITLE_2 = "---------------";
	
	protected String CHECKOUT_TITLE = "源代码checkout";
	protected String BUILD_TITLE = "执行构建";
	protected String SONAR_TITLE = "执行Sonar扫描";
	protected String DEPLOY_TITLE = "执行部署";
	protected String DEPLOY_AUTO_TITLE = "执行自动化部署";
	protected String DOWNLOAD_PACKAGE_TITLE = "执行下载包件";
	protected String INIT_GROOVY_TITLE = "解压包件并且SQL封装";
	
	protected String DEPLOY_SQL_TITLE = "执行服务器部署脚本";
	protected String DEPLOY_SQL_SUB_TITLE_1 = "停止服务前执行SQL";
	protected String DEPLOY_SQL_SUB_TITLE_2 = "停止服务后执行SQL";
	protected String DEPLOY_SQL_SUB_TITLE_3 = "启动服务后执行SQL";
	
	protected String DEPLOY_SQL_IP_TITLE = "当前部署服务器";
	
	
	
	/**
	 * AOP基于pipeline的stage开发
	 * @param tempSB
	 * @param blankCount
	 * @return
	 */
	protected int assembleAOPStart(Map<String, Object> paramMap, TblSystemModule tblSystemModule, StringBuilder tempSB, int blankCount) {
		
		if (tblSystemModule == null) {
			tempSB.append(getPreBlank(blankCount + 1)).append("dataMap.put('moduleCode',null)\n");
		} else {
			tempSB.append(getPreBlank(blankCount + 1)).append("dataMap.put('moduleCode','").append(tblSystemModule.getModuleCode()).append("')\n");
		}
		tempSB.append(getPreBlank(blankCount + 1)).append("stageStart(dataMap)\n");
		return blankCount;
	}
	
	/**
	 * AOP基于pipeline的stage结束
	 * @param tblSystemModule 
	 * @param tempSB
	 * @param blankCount
	 * @return
	 */
	protected int assembleAOPEnd(Map<String, Object> paramMap, TblSystemModule tblSystemModule, StringBuilder tempSB, int blankCount) {
		if (tblSystemModule == null) {
			tempSB.append(getPreBlank(blankCount + 1)).append("dataMap.put('moduleCode',null)\n");
		} else {
			tempSB.append(getPreBlank(blankCount + 1)).append("dataMap.put('moduleCode','").append(tblSystemModule.getModuleCode()).append("')\n");
		}
		tempSB.append(getPreBlank(blankCount + 1)).append("stageEnd(dataMap)\n");
		return blankCount;
	}
	
	protected String getPreBlank(int count) {
		String blank = "";
		for (int i = 0; i < count; i++) {
			blank += "  ";
		}
		return blank;
	}
	
	/**
	 * 将字符串通过\\封装，避免在Expect中被解析
	 * @param fileName
	 * @return
	 */
	protected String formatStringWithExcept(String fileName) {
		if (StringUtil.isNotEmpty(fileName)) {
			fileName = fileName.replaceAll("([\\$>\\[\\]])", "\\\\\\\\$1");
			fileName = fileName.replaceAll(" ", "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ ");//特殊情况...
		}
		return fileName;
	}
	
	/**
	 * 将字符串通过//封装，避免在pipeline中被解析
	 * @param fileName
	 * @return
	 */
	protected String formatStringWithPipeline(String fileName) {
		if (StringUtil.isNotEmpty(fileName)) {
			fileName = fileName.replaceAll("([\\(\\)&<>\\$ ])", "\\\\\\\\$1");
		}
		return fileName;
	}
	
	protected StringBuilder showScriptTitle(String title, String subTitle, int type, String suffixStr, StringBuilder sb, int blankCount) {
		if (type == 0) {//普通构建部署默认标题
			sb.append(getPreBlank(blankCount)).append("echo '").append(suffixStr).append("[").append(title).append("]").append(suffixStr).append("'\n");
		} else if (type == 1 || type == 2) {//带开始结束的标题
			String toggleStr = "";
			if (type == 1) {
				toggleStr = "START";
			} else if (type == 2) {
				toggleStr = "END";
			}
			String subTitleStr = ":" + subTitle;
			sb.append(getPreBlank(blankCount)).append("echo '").append(suffixStr).append("[").append(title).append(subTitleStr)
			.append("  ").append(toggleStr).append("]").append(suffixStr).append("'\n");
		}
		return sb;
	}
	protected StringBuilder showScriptTitle(String title, int type, String suffixStr, StringBuilder sb, int blankCount) {
		return showScriptTitle(title, "", type, suffixStr, sb, blankCount);
	}

}
