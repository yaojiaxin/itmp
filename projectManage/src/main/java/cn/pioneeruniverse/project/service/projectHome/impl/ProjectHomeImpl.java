package cn.pioneeruniverse.project.service.projectHome.impl;

import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.project.dao.mybatis.projectHome.ProjectHomeMapper;
import cn.pioneeruniverse.project.dto.*;
import cn.pioneeruniverse.project.service.projectHome.ProjectHomeService;
import cn.pioneeruniverse.project.vo.MessageInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class ProjectHomeImpl implements ProjectHomeService {


    @Autowired
    private ProjectHomeMapper projectHomeMapper;

    /**
     * 根据项目id获取项目基本信息
     * @param id
     * @return
     */
    @Override
    public ProjectHomeDTO selectSystemInfoByID(Integer id) {
        return projectHomeMapper.selectSystemInfoByID(id);
    }

    /**
     *  根据系统名获取周边系统
     * @param projectId
     * @return
     */
    @Override
    public List<PeripheralSystemDTO> selectSystemInfoByNameList(@Param("projectId") Integer projectId) {
        return projectHomeMapper.selectSystemInfoByNameList(projectId);
    }

    /**
     *  根据项目id获取项目成员
     * @param projectId
     * @return
     */
    @Override
    public Map<String,Object> selectSystemInfoUserNameByIdList(Integer projectId,Long uid) {
        Map<String,Object> mapValues = new HashMap<>();
        List<PeripheralSystemDTO> systemUserNameList = projectHomeMapper.selectSystemInfoUserNameByIdList(projectId);
        mapValues.put("systemUserNameList",systemUserNameList);
        List<ProjectNoticeDTO> notice = null;
        String format = DateUtil.formatDate(new java.sql.Date(System.currentTimeMillis()), "yyyy-MM-dd");
        if (systemUserNameList != null && systemUserNameList.size() >0){
            for (int i =0 ; i<systemUserNameList.size(); i++){
                if (uid == systemUserNameList.get(i).getId()){
                    //项目公告
                    notice = projectHomeMapper.selectProjectNoticeList(format,projectId);
                }
            }
        }else if (uid == 1){
            notice = projectHomeMapper.selectProjectNoticeList(format,projectId);
        }
        mapValues.put("notice",notice);
        return mapValues;
    }

    /**
     * 任务实施情况(瀑布模型)
     * @param projectId
     * @return
     */
    @Override
    public List<TaskForceDTO> selectRequirementFeatureCount(Integer projectId) {
        return projectHomeMapper.selectRequirementFeatureCount(projectId);
    }

    /**
     * 任务实施情况(敏捷模型)
     * @param currentDate
     * @return
     */
    @Override
    public List<TaskForceDTO> selectRequirementFeatureAgilityCount(@Param("currentDate") String currentDate,@Param("projectId") Integer projectId) {
        return projectHomeMapper.selectRequirementFeatureAgilityCount(currentDate,projectId);
    }

    /**
     *  下周冲刺任务
     * @param currentDate
     * @param systemId
     * @return
     */
    @Override
    public List<TaskForceDTO> springNextWork(@Param("currentDate") String currentDate,@Param("systemId") Integer systemId) {
        return projectHomeMapper.springNextWork(currentDate,systemId);
    }

    /**
     * 风险信息总数
     * @param projectId
     * @return
     */
    @Override
    public RiskInfoAndChangeInfoDTO selectRiskInfoCount(Integer projectId) {
        return projectHomeMapper.selectRiskInfoCount(projectId);
    }

    /**
     * 本周新增
     * @param projectId
     * @return
     */
    @Override
    public RiskInfoAndChangeInfoDTO selectWeedAdd(Integer projectId) {
        return projectHomeMapper.selectWeedAdd(projectId);
    }

    /**
     *  未解决数
     * @param projectId
     * @return
     */
    @Override
    public RiskInfoAndChangeInfoDTO selectOutStandingNumber(Integer projectId) {
        return projectHomeMapper.selectOutStandingNumber(projectId);
    }


    /**
     * 项目变更总数
     * @param projectId
     * @return
     */
    @Override
    public RiskInfoAndChangeInfoDTO selectProjectChangeInfoCount(Integer projectId) {
        return projectHomeMapper.selectProjectChangeInfoCount(projectId);
    }

    /**
     * 本周新增（变更）
     * @param projectId
     * @return
     */
    @Override
    public RiskInfoAndChangeInfoDTO selectProjectChangeWeedAdd(Integer projectId) {
        return projectHomeMapper.selectProjectChangeWeedAdd(projectId);
    }

    /**
     *  未解决数（变更）
     * @param projectId
     * @return
     */
    @Override
    public RiskInfoAndChangeInfoDTO selectConfirmationNumber(Integer projectId) {
        return projectHomeMapper.selectConfirmationNumber(projectId);
    }

    /**
     *  项目动态
     * @param projectId
     * @return
     */
    @Override
    public List<ProjectNoticeDTO> selectProjectNoticeList( String currentDate,Integer projectId) {
        return projectHomeMapper.selectProjectNoticeList(currentDate,projectId);
    }


    /**
     *  项目计划（里程碑）
     * @param projectId
     * @return
     */
    @Override
    public List<ProjectPlanDTO> selectProjectPlanById(Integer projectId) {
        return projectHomeMapper.selectProjectPlanById(projectId);
    }

    /**
     *  获取冲刺表项目周期
     * @param currentDate
     * @param projectId
     * @return
     */
    @Override
    public ProjectPlanNewDTO selectProjectPeriod(@Param("currentDate") String currentDate, @Param("projectId") Integer projectId ) {
        List<ProjectPlanNewDTO> projectPlan = projectHomeMapper.selectProjectPeriod(currentDate, projectId);
        ProjectPlanNewDTO projectPlanNew = new ProjectPlanNewDTO();
        if (projectPlan != null && projectPlan.size()>0){
            String beginTime = null;
            String endTime = null;
            List<Long> sprintId = new ArrayList<>();
            Long[] begin = new Long[projectPlan.size()];
            Long[] end = new Long[projectPlan.size()];
            for (int i =0;i<projectPlan.size();i++){
                try{
                    sprintId.add(projectPlan.get(i).getSprintId());
                    begin[i] = this.simpleDateFormatMethod(projectPlan.get(i).getPlanNewStartDate().toString());
                    end[i] = this.simpleDateFormatMethod(projectPlan.get(i).getPlanNewEndDate().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            projectPlanNew.setPlanNewStartDate(this.getMin(begin));
            projectPlanNew.setPlanNewEndDate(this.getMax(end));
            projectPlanNew.setSprintListId(sprintId);
        }
        return projectPlanNew;
    }

    /**
     *  日期转换
     * @return
     */
    private static long simpleDateFormatMethod(String date) throws  Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long timeValue = format.parse(date).getTime();
        return timeValue;
    }


    /**
     * 燃尽图
     * @param currentDate
     * @param projectId
     * @return
     */
    @Override
    public Map selectProjectPlanNewList(@Param("currentDate") String currentDate, @Param("projectId") Integer projectId, @Param("projectPeriod") ProjectPlanNewDTO projectPeriod, @Param("sprintId") List<Long> sprintId) {
        Map<String,Object> mapValues = new HashMap<>();
        if(projectPeriod != null){
            List<ProjectPlanNewDTO> projectPlanNew = projectHomeMapper.selectProjectPlanNewList(currentDate, projectId,projectPeriod);
            if (projectPlanNew != null && projectPlanNew.size()>0){
                List<ProjectPlanNewDTO> period = this.Date(projectPeriod);
                //获取预计工作量
                for (int i =0;i<projectPlanNew.size();i++){
                    for(int j=0;j<period.size();j++){
                        if (projectPlanNew.get(i).getCreateDate().toString().equals(period.get(j).getDate())){
                            period.get(j).setEstimateWorkload(projectPlanNew.get(i).getEstimateWorkload());
                        }
                    }
                }

                //燃尽图历史数据
                List<ProjectPlanNewDTO> featureHistory = projectHomeMapper.selectFeatureHistoryList(currentDate, projectId, sprintId);
                if(featureHistory != null && featureHistory.size()>0){
                    for (int i =0; i<featureHistory.size(); i++){ //获取实际工作量数据
                        for (int j =0; j<period.size(); j++){
                            if (period.get(j).getDate().equals(featureHistory.get(i).getCreateDate().toString())){
                                period.get(j).setEstimateRemainWorkload(featureHistory.get(i).getEstimateRemainWorkload());
                            }
                        }
                    }
                }


                double count = 0;
                if (projectPlanNew != null && projectPlanNew.size()>0){
                    for (int i = 0; i<projectPlanNew.size(); i++){
                        count = count +projectPlanNew.get(i).getEstimateWorkload(); //获取总工时
                    }
                }

                mapValues.put("projectPeriod",period);
                mapValues.put("period",period.size());
                mapValues.put("countWorkload",count);
                return mapValues;
            }
        }

        return mapValues;
    }

    /**
     *  项目主页信息
     * @param url
     * @return
     */
    @Override
    public ProjectHomeIndexDTO selectMenuButtonInfo(String url) {
        return projectHomeMapper.selectMenuButtonInfo(url);
    }

    /**
     *  查询项目关联系统
     * @param project
     * @return
     */
    @Override
    public List<ProjectHomeDTO> projectSystemList(Integer project) {
        return projectHomeMapper.projectSystemList(project);
    }

    /**
     * 项目动态
     * @param projectId
     * @return
     */
    @Override
    public List<MessageInfoVO> selectMessageInfoList(Integer projectId) {
        return projectHomeMapper.selectMessageInfoList(projectId);
    }


    /**
     * 获取冲刺项目周期
     * @return
     */
    public static List<ProjectPlanNewDTO> Date(ProjectPlanNewDTO projectPeriod){
        List<ProjectPlanNewDTO> slots = new ArrayList<ProjectPlanNewDTO>();
        if ( projectPeriod != null){
            try {
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                Date start = null;
                Date stop = null;
                start = new SimpleDateFormat("yyyy-MM-dd").parse(projectPeriod.getPlanNewStartDate());
                stop = new SimpleDateFormat("yyyy-MM-dd").parse(projectPeriod.getPlanNewEndDate());
                from.setTime(start);
                to.setTime(stop);
                to.add(Calendar.DAY_OF_YEAR, 1);
                while (from.getTime().getTime()!= to.getTime().getTime()) {
                    ProjectPlanNewDTO projectPlanNewDTO = new ProjectPlanNewDTO();
                    projectPlanNewDTO.setDate(new SimpleDateFormat("yyyy-MM-dd").format(from.getTime()));
                    slots.add(projectPlanNewDTO);
                    from.add(Calendar.DAY_OF_YEAR, 1);
                }
                to.add(Calendar.DAY_OF_YEAR, -1);
                return slots;
            } catch (ParseException e) {
                e.printStackTrace();
                return slots;
            }
        }
       return slots;
    }

    /**
     *  获取数组最大值
     * @param arr
     * @return
     */
    private static String getMax(Long[] arr) {
        // 定义一个参照物
        Long max = arr[0];
        //遍历数组
        for (int i = 0; i < arr.length; i++) {
            //判断大小
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return DateUtil.formatDate(new Date(max), "yyyy-MM-dd");
    }

    /**
     *  获取数组最小值
     * @param arr
     * @return
     */
    private static String getMin(Long[] arr){
        // 定义一个参照物
        Long min = arr[0];
        //遍历数组
        for (int i = 0; i < arr.length; i++) {
            //判断大小
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return DateUtil.formatDate(new Date(min), "yyyy-MM-dd");
    }


}
