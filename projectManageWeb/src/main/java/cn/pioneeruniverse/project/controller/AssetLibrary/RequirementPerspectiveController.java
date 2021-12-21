package cn.pioneeruniverse.project.controller.AssetLibrary;

import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 10:29 2019/11/11
 * @Modified By:
 */
@RestController
@RequestMapping("requirementPerspective")
public class RequirementPerspectiveController {

    @RequestMapping(value = "/toSystem", method = RequestMethod.GET)
    public ModelAndView toSystem() {
        ModelAndView view = new ModelAndView();
        view.setViewName("assetsLibrary/chooseSystem");
        return view;
    }
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ModelAndView history() {
        ModelAndView view = new ModelAndView();
        view.setViewName("assetsLibrary/history");
        return view;
    }
    @RequestMapping(value = "/relevanceInfo", method = RequestMethod.GET)
    public ModelAndView relevanceInfo() {
        ModelAndView view = new ModelAndView();
        view.setViewName("assetsLibrary/relevanceInfo");
        return view;
    }

}
