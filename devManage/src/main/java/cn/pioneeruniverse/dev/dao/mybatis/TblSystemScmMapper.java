package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblDataDic;
import cn.pioneeruniverse.dev.entity.TblSystemScm;
import org.apache.ibatis.annotations.Param;

public interface TblSystemScmMapper extends BaseMapper<TblSystemScm> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblSystemScm record);

    TblSystemScm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblSystemScm record);

    int updateByPrimaryKey(TblSystemScm record);

    List<TblSystemScm> getBySystemId(Long id);

    List<TblSystemScm> getScmByParam(Map<String, Object> map);

    void deleteBySystemId(Long id);

    void insertSystemScm(TblSystemScm scm);

    void delete(Long id);

    List<TblSystemScm> selectBuildingBySystemid(Long id);

    List<TblSystemScm> judgeScmBuildStatus(Long id);

    int countScmBuildStatus(Map<String, Object> map);

    List<TblSystemScm> findStatus2(Long id);

	void deleteBySystemIds(List<Long> deleteIds);

	List<Map<String, Object>> getSystemVersionBranch(Long systemId);

	List<TblSystemScm> selectBuildingBySystemidDeploy(Long id);

	List<String> getEnvTypes(Long systemId);

	List<Long> getEnvTypesBySyetemId(Long id);

	void updateSystemScm(Map<String, Object> map);

    List<TblSystemScm> selectBreakName(Long systemId);



    List<TblSystemScm> getScmBySystemId(Long systemId);
    void deleteScmById(TblSystemScm scm);
    TblSystemScm findScmDoesItExist(@Param("systemId")Long systemId,@Param("envType")Integer envType);
    void insertScm(TblSystemScm scm);
    void updateScm(TblSystemScm scm);
}