package com.example.myapplication.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String Yetki;
    private String email;
    private String photourl;

    public User() {
    }

    public User(String name, String password, String phone, String yetki, String email, String photourl) {
        Name = name;
        Password = password;
        Phone = phone;
        Yetki = yetki;
        this.email = email;
        this.photourl = photourl;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getYetki() {
        return Yetki;
    }

    public void setYetki(String yetki) {
        Yetki = yetki;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
