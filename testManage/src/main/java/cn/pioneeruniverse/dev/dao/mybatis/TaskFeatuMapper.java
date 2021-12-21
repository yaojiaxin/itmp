package cn.pioneeruniverse.dev.dao.mybatis;

import cn.pioneeruniverse.dev.dto.TaskFeatuDTO;

import java.util.List;

public interface TaskFeatuMapper {

    List<TaskFeatuDTO> selectTaskFeatuByID(Long id);

}
