package com.dml.service;

import com.dml.model.UserInfo;

public interface BaseSevice {
    String getFullName(String name);

    UserInfo getUserInfoByName(String name);
}
