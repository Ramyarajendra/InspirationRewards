
package com.ramya.inspirationrewards;

import java.io.Serializable;
import java.util.List;

public class LeaderBoardInspiration implements Serializable, Comparable<LeaderBoardInspiration> {
    public String stdid;
    public String username;
    public String password;
    public String firstName;
    public String lastName;
    public int pointsToAward;
    public String department;
    public String story;
    public String position;
    public boolean admin;
    public String location;
    public String imageBytes;
    public int rewardPtAward = 0;
    public List<RewardRecords> recordsArrList;
    public LeaderBoardInspiration(String studentId, String username, String password, String firstName, String lastName, int pointsToAward, String department, String story, String position, boolean admin, String location, String imageBytes, List<RewardRecords> recordsArrList) {
        this.stdid = studentId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pointsToAward = pointsToAward;
        this.department = department;
        this.story = story;
        this.position = position;
        this.admin = admin;
        this.location = location;
        this.imageBytes = imageBytes;
        this.recordsArrList=recordsArrList;
        computePtAwarded();
    }
    public void computePtAwarded(){

        if (recordsArrList!=null) {
            for (int i = 0; i < recordsArrList.size(); i++) {
                RewardRecords rewardRec = recordsArrList.get(i);
                rewardPtAward += rewardRec.getValue();
            }
        }
    }
    public List<RewardRecords> getRecordsArrList() {
        return recordsArrList;
    }

    public void setRecordsArrList(List<RewardRecords> recordsArrList) {
        this.recordsArrList = recordsArrList;
    }

    public String getStudentId() {
        return stdid;
    }

    public void setStudentId(String studentId) {
        this.stdid = studentId;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPointsToAward() {
        return pointsToAward;
    }

    public void setPointsToAward(int pointsToAward) {
        this.pointsToAward = pointsToAward;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public int compareTo(LeaderBoardInspiration o) {

        if (this.rewardPtAward > o.rewardPtAward)
            return -1;
        if (this.rewardPtAward < o.rewardPtAward)
            return 1;
        else
            return 0;
    }
}
