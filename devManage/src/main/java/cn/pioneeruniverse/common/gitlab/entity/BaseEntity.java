package cn.pioneeruniverse.common.gitlab.entity;

import java.io.Serializable;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 13:57 2019/7/4
 * @Modified By:
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 9118036486802878764L;

    private String baseUri;

    private String gitApiToken;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getGitApiToken() {
        return gitApiToken;
    }

    public void setGitApiToken(String gitApiToken) {
        this.gitApiToken = gitApiToken;
    }
}
