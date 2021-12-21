package cn.pioneeruniverse.system.service.dept.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.system.dao.mybatis.dept.DeptDao;
import cn.pioneeruniverse.system.entity.TblDeptInfo;
import cn.pioneeruniverse.system.service.dept.IDeptService;


@Service("iDeptService")
public class DeptServiceImpl extends ServiceImpl<DeptDao, TblDeptInfo> implements IDeptService {

    @Autowired
    private DeptDao deptDao;
    
    @Override
    public List<TblDeptInfo> getAllDept() {
        return deptDao.getAllDept();
    }

    @Override
    public TblDeptInfo selectDeptById(Long id) {
        return deptDao.selectDeptById(id);
    }

    @Override
    public List<TblDeptInfo> selectDeptByParentId(Long id) {
        return deptDao.selectDeptByParentId(id);
    }

    @Transactional
    @Override
    public void insertDept(TblDeptInfo dept) {
        String deptNumber = Constants.ITMP_DEPT_PREFIX + "0001";
        String maxNum = deptDao.selectMaxNum();
        if (StringUtils.isNotBlank(maxNum)) {
            Integer index = maxNum.indexOf(Constants.ITMP_DEPT_PREFIX);
            Integer len = Constants.ITMP_DEPT_PREFIX.length();
            Integer num = Integer.valueOf(maxNum.substring(index + len));
            deptNumber = Constants.ITMP_DEPT_PREFIX + String.format("%04d", num + 1);
        }
        dept.setDeptNumber(deptNumber);


        //设置parent_dept_ids
        String parentDeptIds = null;
        Long parentDeptId = dept.getParentDeptId();
        if (parentDeptId != null) {
            parentDeptIds = parentDeptId.toString();
            String ppIds = deptDao.selectPPDept(parentDeptId);
            if (StringUtils.isNotBlank(ppIds)) {
                parentDeptIds = ppIds + "," + parentDeptIds;
            }
        }
        dept.setParentDeptIds(parentDeptIds);
        deptDao.insertDept(dept);
    }

    @Transactional
    @Override
    public void updateDept(TblDeptInfo dept) {

        TblDeptInfo oldDept = deptDao.selectDeptById(dept.getId());
        oldDept.setDeptName(dept.getDeptName());
        Integer status = dept.getStatus();
        oldDept.setStatus(status);

        deptDao.updateDept(oldDept);
        deptDao.updateChildrenDept(oldDept);
        if (status == 2) {
            deptDao.setChildrenDisable(dept);
        }


    }

    @Override
    public List<TblDeptInfo> selectDeptByCompanyId(Long companyId) {
        return deptDao.selectDeptByCompanyId(companyId);
    }

    /**
     * 处理接收的部门信息
     *
     * @param deptData 接收部门数据
     */
    @Override
    @Transactional(readOnly = false)
    public List<Map<String,Object>> getDeptData(String deptData) {   
    	List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
    	Map<String, Object> result1 = new HashMap<>();
    	
    	Map<String, Object> result = jsonToMap(deptData);
    	/*String requestBody = result.get("requestBody").toString();   	
    	List<Map<String,Object>> listObjectSec = JSONArray.parseObject(requestBody,List.class);*/    	
    	/*for(Map<String,Object> map:listObjectSec) {*/
		TblDeptInfo deptInfo = new TblDeptInfo();
		String deptName = result.get("ORG_REGISTRATION_NAME").toString();
		String deptNumber = result.get("NEW_BRANCH_CODE").toString();        	
    	String parentDeptNumber = result.get("ORG_PARENT_ORG_ID").toString();
    	String status = result.get("VALID_STATUS").toString();
    	       	        	
    	TblDeptInfo dept = deptDao.getDeptByCode(deptNumber);
    	TblDeptInfo parentDept = deptDao.getDeptByCode(parentDeptNumber);
    	
    	deptInfo.setDeptName(deptName);
        deptInfo.setDeptNumber(deptNumber);
        deptInfo.setParentDeptNumber(parentDeptNumber);        
        deptInfo.setParentDeptId(parentDept==null?null:parentDept.getId());                    
        deptInfo.setStatus(status.equals("0")?2:1);  
        deptInfo.setLastUpdateBy(1L);
        deptInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
        
    	if (dept == null) {
    		deptInfo.setCreateBy(1L);   		
    		deptInfo.setCreateDate(new Timestamp(new Date().getTime()));   		
            deptDao.insertDept(deptInfo);            
        } else {
            deptInfo.setId(dept.getId());            
            deptDao.updateById(deptInfo);            
        }
    	result1.put("deptInfo", JSONObject.toJSONString(deptInfo));
    	listMap.add(result1);
    	/*}*/
		return listMap;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblDeptInfoDTO> getAllDeptInfo() {
        return deptDao.getAllDeptInfo();
    }

    //json转map
  	public static Map<String, Object> jsonToMap(String str) {
  		Map<String, Object> mapTypes = JSON.parseObject(str);
  		return mapTypes;  
  	}	
  	@Override
  	@DataSource(name="tmpDataSource")
    @Transactional(readOnly=false)
	public int tmpDeptData(TblDeptInfo deptInfo) {
  		TblDeptInfo dept= deptDao.selectDeptById(deptInfo.getId());
  		int status=0;
		if(dept==null) {
			status=deptDao.insert(deptInfo);
		}else {
			status=deptDao.updateById(deptInfo);
		}
		return status;
	}

	@Override
	public List<TblDeptInfo> getDeptByDeptName(TblDeptInfo deptInfo) {
		return deptDao.getDeptByDeptName(deptInfo);
	}
}
