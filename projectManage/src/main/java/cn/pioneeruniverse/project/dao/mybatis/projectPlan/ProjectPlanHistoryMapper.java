package cn.pioneeruniverse.project.dao.mybatis.projectPlan;

import cn.pioneeruniverse.project.entity.TblProjectPlanHistory;
import cn.pioneeruniverse.project.vo.JqueryGanttVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectPlanHistoryMapper {

    void insertProjectPlanHistory(TblProjectPlanHistory planHistory);

    Integer getMaxPlanNumber(Long projectId);

    List<TblProjectPlanHistory> getAllPlanNumber(Long projectId);

    List<JqueryGanttVO> getPlanNumberByNumber(@Param("projectId")Long projectId,
                                              @Param("planNumber")Integer planNumber);
}
