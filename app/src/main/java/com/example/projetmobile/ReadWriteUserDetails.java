package com.example.projetmobile;

public class ReadWriteUserDetails {
    public String fullName,gender,user,role;

    public ReadWriteUserDetails(){}
    public ReadWriteUserDetails(String textFullName,String textGender, String textUser ,String textRole) {
        this.fullName=textFullName;
        this.gender=textGender;
        this.user=textUser;
        this.role=textRole;

    }
}
