package cn.pioneeruniverse.dev.service.taskFeatu;

import cn.pioneeruniverse.dev.dto.TaskFeatuDTO;

import java.util.List;

public interface ITaskFeatuService {

    List<TaskFeatuDTO> selectTaskFeatuByID(Long id);
}
