package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_project_user")
public class TblProjectUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;

    private Long userId;

    private Integer userPost;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getUserPost() {
        return userPost;
    }

    public void setUserPost(Integer userPost) {
        this.userPost = userPost;
    }

}