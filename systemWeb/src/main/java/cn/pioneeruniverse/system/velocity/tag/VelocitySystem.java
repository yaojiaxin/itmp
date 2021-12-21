package cn.pioneeruniverse.system.velocity.tag;

import cn.pioneeruniverse.common.dto.TblDataDicDTO;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.system.feignInterface.dic.DataDicInterface;
import cn.pioneeruniverse.system.feignInterface.menu.MenuInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 10:19 2018/12/26
 * @Modified By:
 */
public class VelocitySystem {

    private static DataDicInterface dataDicInterface = SpringContextHolder.getBean(DataDicInterface.class);

    private static MenuInterface menuInterface = SpringContextHolder.getBean(MenuInterface.class);

    public List<TblDataDicDTO> getDataDicListBytermCode(String termCode) {
        return dataDicInterface.getDataDicByTermCode(termCode);
    }

    public Map<String, Object> getMenuByCode(String code) {
        return menuInterface.getMenuByCode(code);
    }

    public List<Map<String, Object>> getMyMenuList(String userId) {
        if (StringUtils.isNotEmpty(userId)) {
            return menuInterface.getUserMenu(Long.valueOf(userId));
        } else {
            return null;
        }
    }
}
