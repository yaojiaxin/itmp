package cn.pioneeruniverse.project.service.assetsLibrary;

import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryPostAuthority;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryUserAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 13:53 2019/12/5
 * @Modified By:
 */
public interface SystemDirectoryAuthorityService {

    BootStrapTablePage<TblSystemDirectoryUserAuthority> getDirectoryUserAuthority(BootStrapTablePage<TblSystemDirectoryUserAuthority> bootStrapTablePage, Long directoryId);

    BootStrapTablePage<TblSystemDirectoryPostAuthority> getDirectoryPostAuthority(BootStrapTablePage<TblSystemDirectoryPostAuthority> bootStrapTablePage, Long directoryId);

    void addOrUpdateDirectoryUserAuthority(List<TblSystemDirectoryUserAuthority> tblSystemDirectoryUserAuthorities, HttpServletRequest request);

    void deleteDirectoryUserAuthority(List<TblSystemDirectoryUserAuthority> tblSystemDirectoryUserAuthorities);

    void addOrUpdateDirectoryPostAuthority(List<TblSystemDirectoryPostAuthority> tblSystemDirectoryPostAuthorities, HttpServletRequest request);

    void deleteDirectoryPostAuthority(List<TblSystemDirectoryPostAuthority> tblSystemDirectoryPostAuthorities);

    Map<String, Boolean> getCurrentUserDirectoryAuthority(HttpServletRequest request, Long projectId, Long directoryId);

}
