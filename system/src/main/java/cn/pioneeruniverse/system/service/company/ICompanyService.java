package cn.pioneeruniverse.system.service.company;


import java.util.List;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import com.baomidou.mybatisplus.service.IService;

import cn.pioneeruniverse.system.entity.Company;

public interface ICompanyService extends IService<Company> {

    List<Company> getCompany();

    List<TblCompanyInfoDTO> getAllCompanyInfo();

}
