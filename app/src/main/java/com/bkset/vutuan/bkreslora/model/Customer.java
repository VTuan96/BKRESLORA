package com.bkset.vutuan.bkreslora.model;

import java.io.Serializable;

/**
 * Created by Phung Dinh Phuc on 28/07/2017.
 */
@SuppressWarnings("serial")
public class Customer implements Serializable {
    public String Id;
    public String Username;
    public String Email;
    public String PhoneNumber;
    public String Password;
    public String FullName;
    public int HoDanId;

    public Customer(String id, String username, String email, String phoneNumber, String password, String fullName, int hoDanId) {
        Id = id;
        Username = username;
        Email = email;
        PhoneNumber = phoneNumber;
        Password = password;
        FullName = fullName;
        HoDanId = hoDanId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public int getHoDanId() {
        return HoDanId;
    }

    public void setHoDanId(int hoDanId) {
        HoDanId = hoDanId;
    }
}