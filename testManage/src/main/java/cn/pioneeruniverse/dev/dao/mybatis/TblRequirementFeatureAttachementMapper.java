package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.pioneeruniverse.dev.entity.TblRequirementFeatureAttachement;

public interface TblRequirementFeatureAttachementMapper {
	 int deleteByPrimaryKey(Long id);

	    int insert(TblRequirementFeatureAttachement record);

	    int insertSelective(TblRequirementFeatureAttachement record);

	    TblRequirementFeatureAttachement selectByPrimaryKey(Long id);

	    int updateByPrimaryKeySelective(TblRequirementFeatureAttachement record);

	    int updateByPrimaryKey(TblRequirementFeatureAttachement record);

		void insertAtt(TblRequirementFeatureAttachement tblRequirementFeatureAttachement);

		List<TblRequirementFeatureAttachement> findAttByRFId(Long id);

		void updateNo(Long id);

		void updateYes(Long id);

		TblRequirementFeatureAttachement getReqFeatureAttByUrl(String filePath);

		void deleteByIds(List<Long> diffIds);
}