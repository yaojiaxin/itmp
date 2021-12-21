package cn.pioneeruniverse.common.sonar.client;


import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class UsersApi extends AbstractApi{

    public UsersApi(String url, HTTPBasicAuthFilter auth) {
        super(url, auth);
    }

  

}
