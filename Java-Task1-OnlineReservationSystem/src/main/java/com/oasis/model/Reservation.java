package com.oasis.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Reservation {
    private int id;
    private String pnr;
    private int userId;
    private String passengerName;
    private int trainNumber;
    private String trainName;
    private String classType;
    private Date journeyDate;
    private String sourceStation;
    private String destinationStation;
    private Timestamp createdAt;
    private String username; // Attached for display/joins
    private String status;

    public Reservation() {
    }

    public Reservation(int id, String pnr, int userId, String passengerName, int trainNumber, String trainName, 
                       String classType, Date journeyDate, String sourceStation, String destinationStation, 
                       Timestamp createdAt, String username, String status) {
        this.id = id;
        this.pnr = pnr;
        this.userId = userId;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.createdAt = createdAt;
        this.username = username;
        this.status = status;
    }

    public Reservation(String pnr, int userId, String passengerName, int trainNumber, String trainName, 
                       String classType, Date journeyDate, String sourceStation, String destinationStation) {
        this.pnr = pnr;
        this.userId = userId;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.status = "CONFIRMED";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Date getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Date journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
