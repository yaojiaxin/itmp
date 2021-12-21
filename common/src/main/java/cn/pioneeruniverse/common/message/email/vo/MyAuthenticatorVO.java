package cn.pioneeruniverse.common.message.email.vo;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuthenticatorVO extends Authenticator{   
    String userName=null;   
    String password=null;   
        
    public MyAuthenticatorVO(){   
    }   
    public MyAuthenticatorVO(String username, String password) {    
        this.userName = username;    
        this.password = password;    
    }    
    protected PasswordAuthentication getPasswordAuthentication(){   
        return new PasswordAuthentication(userName, password);   
    }   
}   
