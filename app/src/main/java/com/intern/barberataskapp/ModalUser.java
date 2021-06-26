package com.intern.barberataskapp;

public class ModalUser {
    private String uid, name, phone, mail, profileImage, timestamp;

    public ModalUser() {
    }

    public ModalUser(String uid, String name, String phone, String mail, String profileImage, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
