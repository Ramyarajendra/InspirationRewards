package com.ramya.inspirationrewards;

public class Leaderboard implements Comparable<Leaderboard> {

    public String inspName;
    public String inspPosDept;
    public String inspImage;
    public String points;

    public Leaderboard(String inspName, String inspDept, String inspImg, String pts) {
        this.inspName = inspName;
        this.inspPosDept = inspDept;
        this.inspImage = inspImg;
        this.points = pts;
    }

    public String getInspName() {
        return inspName;
    }

    public void setInspName(String inspName) {
        this.inspName = inspName;
    }

    public String getInspPosDept() {
        return inspPosDept;
    }

    public void setInspPosDept(String inspPosDept) {
        this.inspPosDept = inspPosDept;
    }

    public String getInspImage() {
        return inspImage;
    }

    public void setInspImage(String inspImage) {
        this.inspImage = inspImage;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public int compareTo(Leaderboard o) {
        return getPoints().compareTo(o.getPoints());
    }
}
