package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDevTaskRemark;

public interface TblDevTaskRemarkMapper extends BaseMapper<TblDevTaskRemark>{
	/*添加备注*/
	Long addTaskRemark(TblDevTaskRemark tblDevTaskRemark);
	
	List<TblDevTaskRemark> selectRemark(Long DevTaskId);
}
