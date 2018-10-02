package com.example.harithaliman.bruservev2;

class User {
    private String Displayname;
    private String Email;
    private long createdAt;
    private String phoneNumber;

    public User() {}

    public User(String displayname, String email, long createdAt, String phoneNumber) {
        this.Displayname = displayname;
        this.Email = email;
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;


    }

    public String getDisplayname() {
        return Displayname;
    }

    public void setDisplayname(String displayname) {
        Displayname = displayname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
