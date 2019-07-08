package com.example.myapplication.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;


    public String getYetki() {
        return Yetki;
    }

    public void setYetki(String yetki) {
        Yetki = yetki;
    }

    private String Yetki;

    public User() {
    }

    public User(String name, String password,String yetki) {
        Name = name;
        Password = password;
        Yetki=yetki;

    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
