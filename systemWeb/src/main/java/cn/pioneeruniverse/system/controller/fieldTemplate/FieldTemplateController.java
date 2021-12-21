package cn.pioneeruniverse.system.controller.fieldTemplate;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("fieldTemplate")
public class FieldTemplateController {

    @RequestMapping(value="toFieldTemplate")
    public ModelAndView RequirementManage(){
        ModelAndView view = new ModelAndView();
        view.setViewName("fieldTemplate/fieldTemplate");
        return view;
    }
}