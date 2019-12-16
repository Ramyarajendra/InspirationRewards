package com.ramya.inspirationrewards;

public class RewardsBean {
    public String studentIdSource = "";
    public String usernameSource = "";
    public String passwordSource = "";
    public String studentIdTarget = "";
    public String usernameTarget = "";
    public String nameTarget = "";
    public String dateTarget = "";
    public String notesTarget = "";
    public int valueTarget = 0;

    public RewardsBean(String studentIdSource, String usernameSource, String passwordSource, String studentIdTarget, String usernameTarget, String nameTarget, String dateTarget, String notesTarget, int valueTarget) {
        this.studentIdSource = studentIdSource;
        this.usernameSource = usernameSource;
        this.passwordSource = passwordSource;
        this.studentIdTarget = studentIdTarget;
        this.usernameTarget = usernameTarget;
        this.nameTarget = nameTarget;
        this.dateTarget = dateTarget;
        this.notesTarget = notesTarget;
        this.valueTarget = valueTarget;
    }

    public String getStudentIdSource() {
        return studentIdSource;
    }

    public void setStudentIdSource(String studentIdSource) {
        this.studentIdSource = studentIdSource;
    }

    public String getUsernameSource() {
        return usernameSource;
    }

    public void setUsernameSource(String usernameSource) {
        this.usernameSource = usernameSource;
    }

    public String getPasswordSource() {
        return passwordSource;
    }

    public void setPasswordSource(String passwordSource) {
        this.passwordSource = passwordSource;
    }

    public String getStudentIdTarget() {
        return studentIdTarget;
    }

    public void setStudentIdTarget(String studentIdTarget) {
        this.studentIdTarget = studentIdTarget;
    }

    public String getUsernameTarget() {
        return usernameTarget;
    }

    public void setUsernameTarget(String usernameTarget) {
        this.usernameTarget = usernameTarget;
    }

    public String getNameTarget() {
        return nameTarget;
    }

    public void setNameTarget(String nameTarget) {
        this.nameTarget = nameTarget;
    }

    public String getDateTarget() {
        return dateTarget;
    }

    public void setDateTarget(String dateTarget) {
        this.dateTarget = dateTarget;
    }

    public String getNotesTarget() {
        return notesTarget;
    }

    public void setNotesTarget(String notesTarget) {
        this.notesTarget = notesTarget;
    }

    public int getValueTarget() {
        return valueTarget;
    }

    public void setValueTarget(int valueTarget) {
        this.valueTarget = valueTarget;
    }
}
