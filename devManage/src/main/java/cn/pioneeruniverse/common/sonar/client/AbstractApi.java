package cn.pioneeruniverse.common.sonar.client;

import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public abstract class AbstractApi {

    private String url;
    private HTTPBasicAuthFilter auth;

    public AbstractApi(String url, HTTPBasicAuthFilter auth) {
        this.url = url;
        this.auth = auth;
    }

    protected String getUrl() {
        return url;
    }

    protected HTTPBasicAuthFilter getAuth() {
        return auth;
    }
}
