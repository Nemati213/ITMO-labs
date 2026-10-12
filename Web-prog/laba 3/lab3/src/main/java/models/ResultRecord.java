package models;

import java.io.Serializable;

public class ResultRecord implements Serializable {

    private double x;
    private double y;
    private double r;
    private boolean isHit;
    private String currentTime;
    private double executionTime;

    public ResultRecord() {
    }

    public ResultRecord(double x, double y, double r, boolean isHit, String currentTime, double executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.isHit = isHit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getR() { return r; }
    public void setR(double r) { this.r = r; }

    public boolean getIsHit() { return isHit; }
    public void setIsHit(boolean hit) { isHit = hit; }

    public String getCurrentTime() { return currentTime; }
    public void setCurrentTime(String currentTime) { this.currentTime = currentTime; }

    public double getExecutionTime() { return executionTime; }
    public void setExecutionTime(double executionTime) { this.executionTime = executionTime; }
}