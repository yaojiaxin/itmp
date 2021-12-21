package cn.pioneeruniverse.dev.dao.mybatis;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblServerInfo;

public interface TblServerInfoMapper extends BaseMapper<TblServerInfo>{
    int deleteByPrimaryKey(Long id);

    Integer insert(TblServerInfo record);

    TblServerInfo selectByPrimaryKey(Long id);
    
    List<TblServerInfo> selectByCon(@Param("tblServerInfo") TblServerInfo tblServerInfo,@Param("userId")Long userId,@Param("haveHost") List<Long> haveHost);

    int updateByPrimaryKeySelective(TblServerInfo record);
    List<String> selectByUserId(@Param("currentUserId")Long currentUserId);

	List<TblServerInfo> selectByIds(Map<String, Object> serverMap);

	List<TblServerInfo> selectByManyId(@Param("array")String[] array);

	List<TblServerInfo> selectAllServerInfo(@Param("tblServerInfo")TblServerInfo tblServerInfo, @Param("haveHost") List<Long> haveHost);
}