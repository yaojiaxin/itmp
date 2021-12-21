package cn.pioneeruniverse.project.controller.programProject;

import cn.pioneeruniverse.project.controller.assetLibrary.ProjectHomeController;
import cn.pioneeruniverse.project.dto.*;
import cn.pioneeruniverse.project.service.programProjectHome.ProgramProjectHomeService;
import cn.pioneeruniverse.project.service.programProjectHome.impl.ProgramProjectHomeImpl;
import cn.pioneeruniverse.project.service.projectHome.ProjectHomeService;
import cn.pioneeruniverse.project.vo.*;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目群
 */
@RestController
@RequestMapping("program")
public class ProgramProjectController {



    private static final Logger logger = LoggerFactory.getLogger(ProgramProjectController.class);

    @Autowired
    private ProgramProjectHomeService programProjectHomeService;


    @Autowired
    private ProjectHomeService projectHomeService;

    @RequestMapping(value = "programProjectHome")
    public Map programProject(Integer id){
        Map<String ,Object> mapValues = new HashMap<>();
        if (id != null){
            ProgramInfoHomeVO programInfoHome = programProjectHomeService.selectProgramInfoHomeByID(id);
            List<ProgramProjectVO> programProject = programProjectHomeService.selectProgramProjectList(id);
            RiskInfoAndChangeInfoDTO riskInfoCount = programProjectHomeService.selectProgramRiskInfoCount(id);
            RiskInfoAndChangeInfoDTO riskWeekAdd = programProjectHomeService.selectProgramWeedAdd(id);
            RiskInfoAndChangeInfoDTO outStandingNumber = programProjectHomeService.selectProgramOutStandingNumber(id);
            RiskInfoAndChangeInfoDTO changeInfoCount = programProjectHomeService.selectProgramProjectChangeInfoCount(id);
            RiskInfoAndChangeInfoDTO changeWeekAdd = programProjectHomeService.selectProgramProjectChangeWeedAdd(id);
            RiskInfoAndChangeInfoDTO confirmationNumber = programProjectHomeService.selectProgramConfirmationNumber(id);

            //项目群变更 和 风险
            RiskInfoAndChangeInfoDTO riskInfoAndChangeInfo = new RiskInfoAndChangeInfoDTO();
            riskInfoAndChangeInfo.setRiskInfoCount(riskInfoCount.getRiskInfoCount());
            riskInfoAndChangeInfo.setRiskWeekAdd(riskWeekAdd.getRiskWeekAdd());
            riskInfoAndChangeInfo.setOutStandingNumber(outStandingNumber.getOutStandingNumber());
            riskInfoAndChangeInfo.setChangeInfoCount(changeInfoCount.getChangeInfoCount());
            riskInfoAndChangeInfo.setChangeWeekAdd(changeWeekAdd.getChangeWeekAdd());
            riskInfoAndChangeInfo.setConfirmationNumber(confirmationNumber.getConfirmationNumber());

            //项目群里程碑
            List<ProjectPlanDTO> programProjectPlan = programProjectHomeService.selectProgramProjectPlan(id);
            for (int i =0 ;i<programProject.size();i++){
                List<Milestones> milestonesList = new ArrayList<>();
                for (int j =0;j<programProjectPlan.size();j++){
                    if (programProject.get(i).getProjectId().equals(programProjectPlan.get(j).getProjectId())){
                        Milestones milestones = new Milestones();
                        milestones.setName(programProjectPlan.get(j).getPlanName());
                        milestones.setDate(programProjectPlan.get(j).getPlanStartDate()+"~"+programProjectPlan.get(j).getPlanEndDate());
                        milestones.setProgress(programProjectPlan.get(j).getCurrentProgress());
                        milestonesList.add(milestones);
                        programProject.get(i).setMilestones(milestonesList);
                    }
                }
            }

            //燃尽图周期
            SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());
            String currentDate = simpleFormatter.format(date);
            List<ProjectPlanNewDTO> program = programProjectHomeService.selectProgramProjectPeriod(currentDate, id);
            List<ProgramVO> plan= ProgramProjectHomeImpl.Date(program);
            //项目群燃尽图
            List<ProjectPlanNewDTO> projectPlanNew = programProjectHomeService.selectProgramProjectPlanNewList(currentDate, id, program);
            //数据处理
            plan = this.Program(plan, projectPlanNew,programProject);
            //项目群公告
            List<ProjectNoticeDTO> projectNotice = programProjectHomeService.selectProgramProjectNotice(currentDate, id);
            //获取项目群系统id
            List<PeripheralSystemDTO> peripheralSystemDTOList = programProjectHomeService.selectProgramSystemId(id);
            //项目群动态
            List<String> projectId = new ArrayList<>();
            List<MessageInfoVO> messageInfo = new ArrayList<>();
            if (programProject != null && programProject.size()>0){
                for (int i=0; i<programProject.size();i++){
                    projectId.add(String.valueOf(programProject.get(i).getProjectId()));
                }
                messageInfo =  programProjectHomeService.selectProgramMessageInfoList(projectId);
            }

            mapValues.put("plan",plan);
            mapValues.put("programInfoHome",programInfoHome);
            mapValues.put("programProject",programProject);
            mapValues.put("riskInfoAndChangeInfo",riskInfoAndChangeInfo);
            mapValues.put("projectNotice",projectNotice);
            mapValues.put("messageInfo",messageInfo);
            mapValues.put("message","success");
            return  mapValues;
        }
        mapValues.put("message","parameter is null");
        return mapValues;
    }


    private  List<ProgramVO> Program( List<ProgramVO> plan, List<ProjectPlanNewDTO> projectPlanNew,List<ProgramProjectVO> programProject){
        if (plan != null && projectPlanNew != null){
            try{
                for (int i =0 ;i<plan.size();i++){
                    double count =0;
                    List<SpringInfoVO> milestones = plan.get(i).getMilestones();
                    plan.get(i).setMinus(milestones.size()-1);
                    for (int j=0;j<projectPlanNew.size();j++){
                        if (plan.get(i).getProjectId().equals(projectPlanNew.get(j).getProjectId())){
                            for (int k =0;k<milestones.size();k++){
                                if (milestones.get(k).getDataTime().equals(projectPlanNew.get(j).getCreateDate().toString())){
                                    milestones.get(k).setEstimateWorkload(projectPlanNew.get(j).getEstimateWorkload());
                                    milestones.get(k).setEstimateRemainWorkload(projectPlanNew.get(j).getEstimateRemainWorkload());
                                }
                            }
                            count = count +projectPlanNew.get(j).getEstimateWorkload();
                        }
                    }
                    plan.get(i).setProjectCount(count);
                    plan.get(i).setDevelopmentMode(1);
                    plan.get(i).setMilestones(milestones);
                }

                for (int i =0; i<plan.size();i++){
                    for (int j =0; j<programProject.size();j++){
                        if (plan.get(i).getProjectId().equals(programProject.get(j).getProjectId())){
                            plan.get(i).setProjectName(programProject.get(j).getProjectName());
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                return plan;
            }

        }
        return plan;
    }

}
