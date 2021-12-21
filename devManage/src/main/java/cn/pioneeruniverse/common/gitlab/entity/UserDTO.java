package cn.pioneeruniverse.common.gitlab.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 17:46 2019/7/11
 * @Modified By:
 */
@JsonNaming(PropertyNamingStrategy.class)
public class UserDTO extends User {
    private static final long serialVersionUID = -179463997228543862L;


    private Long userId;

    private Integer gitLabAccessLevel;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getGitLabAccessLevel() {
        return gitLabAccessLevel;
    }

    public void setGitLabAccessLevel(Integer gitLabAccessLevel) {
        this.gitLabAccessLevel = gitLabAccessLevel;
    }
}
