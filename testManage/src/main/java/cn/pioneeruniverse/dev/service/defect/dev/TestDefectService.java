package cn.pioneeruniverse.dev.service.defect.dev;

/**
 * Description:
 * Author:liushan
 * Date: 2018/12/10 下午 1:44
 */
public interface TestDefectService {
    void syncDefect(String objectJson) throws Exception;

    void syncDefectAtt(String objectJson) throws Exception;

    void insertDefectAttachement(String objectJson);
}
