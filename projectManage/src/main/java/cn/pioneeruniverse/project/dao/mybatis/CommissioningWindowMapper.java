package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.project.entity.TblCommissioningWindow;

public interface CommissioningWindowMapper extends BaseMapper<TblCommissioningWindow>{

	List<TblCommissioningWindow> selectCommissioningWindows(HashMap<Object, Object> map);

	String selectMaxVersion();

	void insertCommissioningWindow(TblCommissioningWindow commissioningWindow);

	void delectCommissioningWindow(HashMap<String, Object> map);

	TblCommissioningWindow selectCommissioningWindowById(Long id);

	void editCommissioningWindow(TblCommissioningWindow commissioningWindow);
	
	List<TblCommissioningWindow> findCommissioningByWindowDate(TblCommissioningWindow commissioningWindow);

	List<TblCommissioningWindow> selectAllWindows(Map<String, Object> map);

	List<Map<String, Object>> getReqFeatureGroupbyWindow(Long requirementId);

}
