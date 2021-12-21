package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblRequirementFeatureDeployStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TblRequirementFeatureDeployStatusMapper {

    List<TblRequirementFeatureDeployStatus> findByReqFeatureId(Long featureId);

    TblRequirementFeatureDeployStatus selectByPrimaryKey(Long id);

    TblRequirementFeatureDeployStatus findByReqFeatureIdDeployStatu(@Param(value = "featureId") Long featureId,
                                                                    @Param(value = "deployStatu") Integer deployStatu);

    void insertFeatureDeployStatus(TblRequirementFeatureDeployStatus featureDeployStatus);

    void updateFeatureDeployStatus(TblRequirementFeatureDeployStatus featureDeployStatus);

    void deleteByFeatureId(Long featureId);

    void deleteById(Long id);
}