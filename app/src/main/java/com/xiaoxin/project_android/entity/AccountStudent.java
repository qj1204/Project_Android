package com.xiaoxin.project_android.entity;

import java.io.Serializable;

public class AccountStudent implements Serializable {
    private final String name;
    private final String account;
    private final String password;
    private final Integer sex;
    private final String major;
    private final String phone;
    private final String email;

    public AccountStudent(String name, String account, String password, Integer sex, String major, String phone, String email) {
        this.name = name;
        this.account = account;
        this.password = password;
        this.sex = sex;
        this.major = major;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public Integer getSex() {
        return sex;
    }

    public String getMajor() {
        return major;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
