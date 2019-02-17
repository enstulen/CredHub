package com.uc3m.credhub;

public class PasswordEntity {
    String ID;
    String description;
    String username;
    String password;

    public PasswordEntity(String ID, String description, String username, String password) {
        this.ID = ID;
        this.description = description;
        this.username = username;
        this.password = password;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
