package cn.pioneeruniverse.system.service.company.impl;

import java.util.List;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import cn.pioneeruniverse.system.dao.mybatis.company.CompanyDao;
import cn.pioneeruniverse.system.entity.Company;
import cn.pioneeruniverse.system.service.company.ICompanyService;
import org.springframework.transaction.annotation.Transactional;

@Service("iCompanyService")
public class CompanyServiceImpl extends ServiceImpl<CompanyDao, Company> implements ICompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Override
    public List<Company> getCompany() {
        List<Company> list = companyDao.getCompany();
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblCompanyInfoDTO> getAllCompanyInfo() {
        return companyDao.getAllCompanyInfo();
    }

}
