package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblTestTaskRemarkAttachement;

public interface TblTestTaskRemarkAttachementMapper extends BaseMapper<TblTestTaskRemarkAttachement>{
	List<TblTestTaskRemarkAttachement> findTaskRemarkAttachement(List<Long> ids);
	
	void addRemarkAttachement(List<TblTestTaskRemarkAttachement> list);
}
