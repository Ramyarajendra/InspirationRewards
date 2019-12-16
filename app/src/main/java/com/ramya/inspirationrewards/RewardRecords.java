package com.ramya.inspirationrewards;

import java.io.Serializable;

public class RewardRecords implements Serializable {
    public String studentId = "";
    public String usernameRecord = "";
    public String passwordRecord = "";
    public String name= "";
    public String date = "";
    public String notes = "";
    public int value = 0;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUsernameRecord() {
        return usernameRecord;
    }

    public void setUsernameRecord(String usernameRecord) {
        this.usernameRecord = usernameRecord;
    }

    public String getPasswordRecord() {
        return passwordRecord;
    }

    public void setPasswordRecord(String passwordRecord) {
        this.passwordRecord = passwordRecord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
