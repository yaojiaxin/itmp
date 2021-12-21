package cn.pioneeruniverse.dev.service.defect.dev;

import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.dev.entity.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author:liushan
 * Date: 2018/12/10 下午 1:44
 */
public interface DevDefectService {
    void syncDefect(String objectJson) throws Exception;

    void syncDefectAtt(String objectJson) throws Exception;

    void updateDevDefect(String objectJson);

    void syncDefectWithFiles(String objectJson) throws Exception;
}
