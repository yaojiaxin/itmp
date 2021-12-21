package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.dev.entity.TblCommissioningWindow;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface TblCommissioningWindowMapper  extends BaseMapper<TblCommissioningWindow> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblCommissioningWindow record);

    TblCommissioningWindow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblCommissioningWindow record);

    int updateByPrimaryKey(TblCommissioningWindow record);

	List<TblCommissioningWindow> getAll();

	String getWindowName(Long commissioningWindowId);

	String getWindowNameById(Long commissioningWindowId);

    List<TblCommissioningWindow> getAllComWindow(Map<String,Object> map);

    int getAllComWindowTotal(Map<String,Object> map);
}