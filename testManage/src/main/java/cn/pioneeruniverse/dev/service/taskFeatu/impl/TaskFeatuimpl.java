package cn.pioneeruniverse.dev.service.taskFeatu.impl;

import cn.pioneeruniverse.dev.dao.mybatis.TaskFeatuMapper;
import cn.pioneeruniverse.dev.dto.TaskFeatuDTO;
import cn.pioneeruniverse.dev.service.taskFeatu.ITaskFeatuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskFeatuimpl implements ITaskFeatuService {

    public final static Logger logger = LoggerFactory.getLogger(TaskFeatuimpl.class);

    @Autowired
    private TaskFeatuMapper taskFeatuMapper;

    @Override
    public List<TaskFeatuDTO> selectTaskFeatuByID(Long id) {
        return taskFeatuMapper.selectTaskFeatuByID(id);
    }
}
