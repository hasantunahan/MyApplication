package com.example.myapplication.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String Yetki;

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    private String photourl;

    public String getYetki() {
        return Yetki;
    }

    public void setYetki(String yetki) {
        Yetki = yetki;
    }



    public User() {
    }

    public User(String name, String password,String yetki,String photourl) {
        Name = name;
        Password = password;
        Yetki=yetki;
        this.photourl=photourl;
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
