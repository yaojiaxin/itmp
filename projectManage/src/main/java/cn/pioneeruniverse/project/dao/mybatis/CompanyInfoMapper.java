package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

public interface CompanyInfoMapper {

	String selectCompanyNameById(Long companyId);

	List<String> selectCompanyName();

	List<Long> selectCompanyId(String companyName);

	
}
