package com.versioneye.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class ProxyAuthenticator extends Authenticator {

    private String user, pass;

    public ProxyAuthenticator(String user, String pass){
        this.user = user;
        this.pass = pass;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return (new PasswordAuthentication(user, pass.toCharArray()));
    }

}
