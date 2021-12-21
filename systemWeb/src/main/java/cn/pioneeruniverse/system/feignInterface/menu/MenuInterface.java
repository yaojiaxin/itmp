package cn.pioneeruniverse.system.feignInterface.menu;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.system.feignFallback.menu.MenuFallback;
import cn.pioneeruniverse.system.vo.menu.TblMenuButtonInfo;

@FeignClient(value = "system", fallbackFactory = MenuFallback.class)
public interface MenuInterface {

    @RequestMapping(value = "menu/getUserMenu", method = RequestMethod.POST)
    List<Map<String, Object>> getUserMenu(@RequestParam("userId") Long userId);


    @RequestMapping(value = "menu/selectMenuById", method = RequestMethod.POST)
    Map<String, Object> selectMenuById(@RequestParam("id") Long id);

    @RequestMapping(value = "menu/getMenuByCode", method = RequestMethod.POST)
    Map<String, Object> getMenuByCode(@RequestParam("menuButtonCode") String menuButtonCode);


    @RequestMapping(value = "menu/selectMenuByParentId", method = RequestMethod.POST)
    Map<String, Object> selectMenuByParentId(@RequestParam("id") Long id);


    @RequestMapping(value = "menu/insertMenu", method = RequestMethod.POST)
    Map<String, Object> insertMenu(@RequestBody TblMenuButtonInfo menu);

    @RequestMapping(value = "menu/updateMenu", method = RequestMethod.POST)
    Map<String, Object> updateMenu(@RequestBody TblMenuButtonInfo menu);


    @RequestMapping(value = "menu/getMenusWithRole", method = RequestMethod.POST)
    Map<String, Object> getMenusWithRole();

    @RequestMapping(value = "menu/getAllMenu", method = RequestMethod.POST)
    Map<String, Object> getAllMenu();
}
