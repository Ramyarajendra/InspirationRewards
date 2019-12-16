package com.ramya.inspirationrewards;

import java.io.Serializable;

public class CreateProfile implements Serializable, Comparable<CreateProfile> {
    public String stdId;
    public String uname;
    public String psswd;
    public String dept;
    public String fName;
    public String pos;
    public boolean admin;
    public String lName;
    public int pts;
    public String stry;
    public String loc;
    public String imaBytes;

    public CreateProfile(String studentId, String username, String password, String firstName, String lastName, int pointsToAward, String department, String story, String position, boolean admin, String location, String imageBytes) {
        this.stdId = studentId;
        this.uname = username;
        this.dept = department;
        this.stry = story;
        this.psswd = password;
        this.fName = firstName;
        this.lName = lastName;
        this.pts = pointsToAward;
        this.loc = location;
        this.pos = position;
        this.admin = admin;
        this.imaBytes = imageBytes;
    }

    public String getStudentId() {
        return stdId;
    }

    public void setStudentId(String studentId) {
        this.stdId = studentId;
    }

    public String getUsername() {
        return uname;
    }

    public void setUsername(String username) {
        this.uname = username;
    }

    public String getPassword() {
        return psswd;
    }

    public void setPassword(String password) {
        this.psswd = password;
    }

    public String getFirstName() {
        return fName;
    }

    public void setFirstName(String firstName) {
        this.fName = firstName;
    }

    public String getLastName() {
        return lName;
    }

    public void setLastName(String lastName) {
        this.lName = lastName;
    }

    public int getPointsToAward() {
        return pts;
    }

    public void setPointsToAward(int pointsToAward) {
        this.pts = pointsToAward;
    }

    public String getDepartment() {
        return dept;
    }

    public void setDepartment(String department) {
        this.dept = department;
    }

    public String getStory() {
        return stry;
    }

    public void setStory(String story) {
        this.stry = story;
    }

    public String getPosition() {
        return pos;
    }

    public void setPosition(String position) {
        this.pos = position;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getLocation() {
        return loc;
    }

    public void setLocation(String location) {
        this.loc = location;
    }

    public String getImageBytes() {
        return imaBytes;
    }

    public void setImageBytes(String imageBytes) {
        this.imaBytes = imageBytes;
    }
    
    @Override
    public int compareTo(CreateProfile o) {
        if (this.pts > o.pts)
            return -1;
        if (this.pts < o.pts)
            return 1;
        else
            return 0;
    }
}
