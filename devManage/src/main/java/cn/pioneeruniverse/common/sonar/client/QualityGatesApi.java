package cn.pioneeruniverse.common.sonar.client;


import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class QualityGatesApi extends AbstractApi{

    public QualityGatesApi(String url, HTTPBasicAuthFilter auth) {
        super(url, auth);
    }

  

}
