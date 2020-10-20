package com.rayson.eldoretonlinemarketadmin.Common;

public class StaffDetails {
    String name,role,email;
    int status;

    public StaffDetails() {
    }

    public StaffDetails(String name, String role, String email, int status) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
