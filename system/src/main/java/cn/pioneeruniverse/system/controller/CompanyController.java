package cn.pioneeruniverse.system.controller;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import cn.pioneeruniverse.system.service.company.ICompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 13:35 2018/12/18
 * @Modified By:
 */
@RestController
@RequestMapping("company")
public class CompanyController {

    private final static Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private ICompanyService iCompanyService;

    @RequestMapping(value = "getAllCompanyInfo", method = RequestMethod.POST)
    public List<TblCompanyInfoDTO> getAllCompanyInfo() {
        return iCompanyService.getAllCompanyInfo();
    }
}
