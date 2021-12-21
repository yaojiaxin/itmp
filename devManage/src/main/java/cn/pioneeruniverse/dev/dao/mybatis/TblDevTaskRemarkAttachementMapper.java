package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDevTaskRemarkAttachement;

public interface TblDevTaskRemarkAttachementMapper extends BaseMapper<TblDevTaskRemarkAttachement>{
	List<TblDevTaskRemarkAttachement> findTaskRemarkAttachement(List<Long> ids);
	
	void addRemarkAttachement(List<TblDevTaskRemarkAttachement> list);
}
