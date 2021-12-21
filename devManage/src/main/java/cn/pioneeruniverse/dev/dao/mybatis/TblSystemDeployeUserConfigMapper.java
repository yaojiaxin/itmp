package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblSystemDeployeUserConfig;
import com.baomidou.mybatisplus.mapper.BaseMapper;


public interface TblSystemDeployeUserConfigMapper  extends BaseMapper<TblSystemDeployeUserConfig> {
    int deleteByPrimaryKey(Long id);



    int insertSelective(TblSystemDeployeUserConfig record);

    TblSystemDeployeUserConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblSystemDeployeUserConfig record);

    int updateByPrimaryKey(TblSystemDeployeUserConfig record);
}