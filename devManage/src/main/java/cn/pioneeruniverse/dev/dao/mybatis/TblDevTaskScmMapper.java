package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.dev.entity.TblDevTaskScmHistory;
import org.apache.ibatis.annotations.Param;
import cn.pioneeruniverse.dev.entity.TblDevTaskScm;


public interface TblDevTaskScmMapper {

    int insertOrUpdateDevTaskScm(TblDevTaskScm tblDevTaskScm);

    List<TblDevTaskScm> selectDevTaskScmPage(@Param(value = "tblDevTaskScm") TblDevTaskScm tblDevTaskScm, @Param("currentUserId") Long currentUserId);

    TblDevTaskScm getDevTaskDetailByDevTaskId(Long devTaskId);

}