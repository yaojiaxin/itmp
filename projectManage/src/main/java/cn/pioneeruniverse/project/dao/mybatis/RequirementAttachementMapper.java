package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import cn.pioneeruniverse.project.entity.TblRequirementAttachement;

public interface RequirementAttachementMapper extends BaseMapper<TblRequirementAttachement>{
	   
    List<TblRequirementAttachement> getRequirementAttachement(Long id);
    
    int insertSelective(TblRequirementAttachement record);
    
    void removeAttachement(TblRequirementAttachement record);
    
}