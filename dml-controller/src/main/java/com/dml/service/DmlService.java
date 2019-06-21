package com.dml.service;


import com.dml.model.UserInfo;
import com.dml.spring.framework.annotation.Service;

@Service
public class DmlService {


    public String getFullName(String name) {
        return "亲爱的" + name;
    }

    public UserInfo getUserInfoByName(String name) {

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(name);
        userInfo.setUserAge(18);
        userInfo.setUserHobby("宅男");

        return userInfo;
    }
}
