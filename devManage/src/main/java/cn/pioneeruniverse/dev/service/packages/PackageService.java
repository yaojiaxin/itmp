package cn.pioneeruniverse.dev.service.packages;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.common.nexus.NexusSearchVO;
import cn.pioneeruniverse.dev.entity.TblArtifactInfo;
import cn.pioneeruniverse.dev.entity.TblRequirementFeature;

public interface PackageService {
	
	Map  findFrature(TblRequirementFeature tblRequirementFeature, Integer rows, Integer page);
	
	void addArtifactFrature(String artifactInfoStr,String tagStr,HttpServletRequest request);
	
	Map<String, Object> findNexusType(NexusSearchVO nexusSearchVO,Long systemId,Long projectId,String tagStr,Integer rows,Integer page)  throws Exception;
	
	Map<String, Object> getArtifactInfo(Long id);
	
	void relateFeature(Long requirementFeatureId,Long artifactInfoId,HttpServletRequest request);
	
	void removeFeature(Long id,HttpServletRequest request);
	
	void removeManyFeature(String ids,HttpServletRequest request);
	
	void deleteTagByArtifactInfoId(Long artifactInfoId);
	
	List<TblArtifactInfo> findNewPackage(Long systemId,Long systemModuleId,Integer env);

	List<String> findRequidsByartId(String artifactids);

	void downPackage(String nexusPath, String repositoryName, HttpServletRequest request, HttpServletResponse response) throws Exception;
	public void addArtifactFrature(TblArtifactInfo tblArtifactInfo, String tagStr,long userId,String artType);

	Map<String, Object> getEnvType(Long id);
}
