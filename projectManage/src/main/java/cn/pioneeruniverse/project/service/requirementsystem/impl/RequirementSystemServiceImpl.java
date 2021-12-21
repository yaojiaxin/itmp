package cn.pioneeruniverse.project.service.requirementsystem.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONObject;
import cn.pioneeruniverse.project.common.SynRequirementSystemUtil;
import cn.pioneeruniverse.project.dao.mybatis.RequirmentSystemMapper;
import cn.pioneeruniverse.project.entity.TblRequirementSystem;
import cn.pioneeruniverse.project.service.requirementsystem.RequirementSystemService;
import cn.pioneeruniverse.project.vo.SynRequirementSystem;

@Transactional(readOnly = true)
@Service("requirementSystemService")
public class RequirementSystemServiceImpl implements RequirementSystemService {
	
	@Autowired
	private RequirmentSystemMapper reqSystemMapper;

	@Override
	@Transactional(readOnly = false)
	public void insertReqSystem(TblRequirementSystem requirementSystem) {		
		reqSystemMapper.insertReqSystem(requirementSystem);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateReqSystem(TblRequirementSystem requirementSystem) {		
		reqSystemMapper.updateReqSystem(requirementSystem);
	}

	@Override
	@Transactional(readOnly = false)
	public int updateReqSystemData(String reqSystemList,String reqId) {
		int status = 0;
		reqSystemMapper.deleteReqSystem(Long.valueOf(reqId));
        List<SynRequirementSystem> resultList = JSONObject.parseArray(reqSystemList, SynRequirementSystem.class);
        for (SynRequirementSystem synReqSystem : resultList) {
        	TblRequirementSystem trs = SynRequirementSystemUtil.SynTblRequirementSystem(synReqSystem);
			trs.setRequirementId(Long.valueOf(reqId));
			if(trs.getSystemId()==null){
				trs.setStatus(1);
				trs.setCreateBy((long) 1);
				trs.setLastUpdateBy((long) 1);
				trs.setCreateDate(new Timestamp(new Date().getTime()));
				trs.setLastUpdateDate(new Timestamp(new Date().getTime()));
				reqSystemMapper.insertReqSystem(trs);
			}else{
				TblRequirementSystem trs1 = reqSystemMapper.selectReqSystemByReqSystem(trs);
				if(trs1!=null&&trs1.getId()!=null){
					trs.setId(trs1.getId());
					trs.setStatus(1);
					trs.setLastUpdateBy((long) 1);
					trs.setLastUpdateDate(new Timestamp(new Date().getTime()));
					reqSystemMapper.updateReqSystem(trs);
				}else {
					trs.setStatus(1);
					trs.setCreateBy((long) 1);
					trs.setLastUpdateBy((long) 1);
					trs.setCreateDate(new Timestamp(new Date().getTime()));
					trs.setLastUpdateDate(new Timestamp(new Date().getTime()));
					reqSystemMapper.insertReqSystem(trs);
				}
			}
        }                       
        return status;
	}	

	@Override
	public int getFunctionCountByReqId(Long requirementId) {		
		return reqSystemMapper.getFunctionCountByReqId(requirementId);
	}
}
