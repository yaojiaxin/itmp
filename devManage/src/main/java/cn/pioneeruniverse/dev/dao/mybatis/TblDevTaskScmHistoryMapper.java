package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.entity.TblDevTaskScmHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:51 2019/6/5
 * @Modified By:
 */
public interface TblDevTaskScmHistoryMapper {

    int insertDevTaskScmHistory(TblDevTaskScmHistory tblDevTaskScmHistory);

    TblDevTaskScmHistory findScmCountBySystemId(@Param(value = "systemId") Long systemId,
                                                @Param(value = "day") Long day);

    List<TblDevTaskScmHistory> find7DayScmCountBySystemId(Long systemId);

}
