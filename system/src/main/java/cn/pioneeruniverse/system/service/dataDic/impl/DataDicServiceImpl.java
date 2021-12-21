package cn.pioneeruniverse.system.service.dataDic.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.dto.TblDataDicDTO;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.system.dao.mybatis.dataDic.DataDicDao;
import cn.pioneeruniverse.system.entity.TblDataDic;
import cn.pioneeruniverse.system.service.dataDic.IDataDicService;

@Service("iDataDicService")
public class DataDicServiceImpl extends ServiceImpl<DataDicDao, TblDataDic> implements IDataDicService {

    @Autowired
    private DataDicDao dataDicDao;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    @Transactional(readOnly = true)
    public List<TblDataDic> getDataDicByTerm(String termCode) {
        return dataDicDao.getDataDicByTerm(termCode);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TblDataDic> selectList(TblDataDic dic) {
        return dataDicDao.selectList(new EntityWrapper<TblDataDic>(dic).eq("STATUS", 1).orderBy("TERM_CODE").orderBy("VALUE_SEQ"));
    }


    @Override
    @Transactional(readOnly = true)
    public JqGridPage<TblDataDicDTO> getDataDictPage(JqGridPage<TblDataDicDTO> jqGridPage, TblDataDicDTO tblDataDicDTO) throws Exception {
        jqGridPage.filtersAttrToEntityField(tblDataDicDTO);
        PageHelper.startPage(jqGridPage.getJqGridPrmNames().getPage(), jqGridPage.getJqGridPrmNames().getRows());
        List<TblDataDicDTO> list = dataDicDao.selectDataDicPage(tblDataDicDTO);
        PageInfo<TblDataDicDTO> pageInfo = new PageInfo<>(list);
        jqGridPage.processDataForResponse(pageInfo);
        return jqGridPage;
    }

    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void saveDataDictSubmit(String dataDics,/* String delDataDicts,*/ String dataDicMapForRedis, HttpServletRequest request) {
        List<TblDataDic> dataDicList = JsonUtil.fromJson(dataDics, JsonUtil.createCollectionType(List.class, TblDataDic.class));
//        List<Long> delDataDicList = JsonUtil.fromJson(delDataDicts, JsonUtil.createCollectionType(List.class, Long.class));
        Map<String, Map<String, String>> dataDicForRedis = JsonUtil.fromJson(dataDicMapForRedis, Map.class);
        if (CollectionUtil.isNotEmpty(dataDicList)) {
            for (TblDataDic tblDataDic : dataDicList) {
                tblDataDic.preInsertOrUpdate(request);
                if (tblDataDic.getId() != null) {
                    //修改
                    dataDicDao.updateDataDict(tblDataDic);
                } else {
                    //新增
                    dataDicDao.addDataDict(tblDataDic);
                }
            }
        }
//        for (Long id : delDataDicList) {
//            //删除
//            TblDataDic tblDataDic = new TblDataDic();
//            tblDataDic.setId(id);
//            tblDataDic.preInsertOrUpdate(request);
//            tblDataDic.setStatus(tblDataDic.DEL_FLAG_DELETE);
//            dataDicDao.delDataDict(tblDataDic);
//        }
        Collections.sort(dataDicList);
        //同步更新redis
        /*for (Map.Entry<String, Map<String, String>> entry : dataDicForRedis.entrySet()) {
            if (entry.getValue().isEmpty()) {
                redisUtils.remove(entry.getKey());
            } else {
                redisUtils.set(entry.getKey(), JsonUtil.toJson(entry.getValue()));
            }
        }*/
//        Map<String,String> map = new HashMap<>();
        Map<String, String> map = Maps.newLinkedHashMap();
        if (CollectionUtil.isNotEmpty(dataDicList)) {
        	for (TblDataDic tblDataDic : dataDicList) {
        		if(tblDataDic.getStatus()!=2 && tblDataDic.getValueName()!=null && tblDataDic.getValueCode()!=null && tblDataDic.getValueSeq()!=null) {
        			map.put(tblDataDic.getValueCode(), tblDataDic.getValueName());
        		}
        	}
		}
        if(map==null || map.size()==0) {
        	 redisUtils.remove(dataDicList.get(0).getTermCode());
        }else {
        	redisUtils.set(dataDicList.get(0).getTermCode(), JSON.toJSONString(map));
        }
        SpringContextHolder.getBean(IDataDicService.class).saveDataDict(dataDics,dataDicMapForRedis,request);
    }
    
    
    @Override
    @DataSource(name = "tmpDataSource")
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
	public void saveDataDict(String dataDics, String dataDicMapForRedis, HttpServletRequest request) {
		// TODO Auto-generated method stub
    	List<TblDataDic> dataDicList = JsonUtil.fromJson(dataDics, JsonUtil.createCollectionType(List.class, TblDataDic.class));
//      List<Long> delDataDicList = JsonUtil.fromJson(delDataDicts, JsonUtil.createCollectionType(List.class, Long.class));
      Map<String, Map<String, String>> dataDicForRedis = JsonUtil.fromJson(dataDicMapForRedis, Map.class);
      if (CollectionUtil.isNotEmpty(dataDicList)) {
          for (TblDataDic tblDataDic : dataDicList) {
              tblDataDic.preInsertOrUpdate(request);
              if (tblDataDic.getId() != null) {
                  //修改
                  dataDicDao.updateDataDict(tblDataDic);
              } else {
                  //新增
                  dataDicDao.addDataDict(tblDataDic);
              }
          }
      }
	}

    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void updateDataDictStatus(TblDataDic tblDataDic, HttpServletRequest request) {
        tblDataDic.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
        tblDataDic.setLastUpdateDate(new Timestamp(new Date().getTime()));
        dataDicDao.updateStatusByTermCode(tblDataDic);
        if (tblDataDic.getStatus().equals(tblDataDic.DEL_FLAG_NORMAL)) {
            //设为有效
            Map<String, String> map = new HashMap<>();
            for (TblDataDic tdd : dataDicDao.getDataDicByTerm(tblDataDic.getTermCode())) {
                map.put(tdd.getValueCode(), tdd.getValueName());
            }
            redisUtils.set(tblDataDic.getTermCode(), JsonUtil.toJson(map));
        } else if (tblDataDic.getStatus().equals(tblDataDic.DEL_FLAG_DELETE)) {
            //设为无效
            redisUtils.remove(tblDataDic.getTermCode());
        }
        SpringContextHolder.getBean(IDataDicService.class).updateStatus(tblDataDic,request);
    }


	@Override
	@DataSource(name = "tmpDataSource")
	@Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
	public void updateStatus(TblDataDic tblDataDic, HttpServletRequest request) {
		// TODO Auto-generated method stub
		tblDataDic.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
        tblDataDic.setLastUpdateDate(new Timestamp(new Date().getTime()));
        dataDicDao.updateStatusByTermCode(tblDataDic);
        if (tblDataDic.getStatus().equals(tblDataDic.DEL_FLAG_NORMAL)) {
            //设为有效
            Map<String, String> map = new HashMap<>();
            for (TblDataDic tdd : dataDicDao.getDataDicByTerm(tblDataDic.getTermCode())) {
                map.put(tdd.getValueCode(), tdd.getValueName());
            }
            redisUtils.set(tblDataDic.getTermCode(), JsonUtil.toJson(map));
        } else if (tblDataDic.getStatus().equals(tblDataDic.DEL_FLAG_DELETE)) {
            //设为无效
            redisUtils.remove(tblDataDic.getTermCode());
        }
	}


	

}
