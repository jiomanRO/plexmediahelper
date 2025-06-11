package com.john.plexmediahelper.scheduling.model;

import java.util.Date;

public class ScheduledExecutionRun {
    int ID;
    String runStatus;
    Date startDate;
    Date endDate;
    String nasTransmissionDirName;
    int totalItemsCount;
    int moviesCount;
    int tvShowsCount;
    int otherCount;
    int moviesLinksCountBefore;
    int moviesLinksCountAfter;
    int tvShowsLinksCountBefore;
    int tvShowsLinksCountAfter;
    int kidsLinksCountBefore;
    int kidsLinksCountAfter;
    int missingLinksCountBefore;
    int missingLinksCountAfter;
    int invalidLinksCountBefore;
    int invalidLinksCountAfter;

    String detail;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNasTransmissionDirName() {
        return nasTransmissionDirName;
    }

    public void setNasTransmissionDirName(String nasTransmissionDirName) {
        this.nasTransmissionDirName = nasTransmissionDirName;
    }

    public int getTotalItemsCount() {
        return totalItemsCount;
    }

    public void setTotalItemsCount(int totalItemsCount) {
        this.totalItemsCount = totalItemsCount;
    }

    public int getMoviesCount() {
        return moviesCount;
    }

    public void setMoviesCount(int moviesCount) {
        this.moviesCount = moviesCount;
    }

    public int getTvShowsCount() {
        return tvShowsCount;
    }

    public void setTvShowsCount(int tvShowsCount) {
        this.tvShowsCount = tvShowsCount;
    }

    public int getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(int otherCount) {
        this.otherCount = otherCount;
    }

    public int getMoviesLinksCountBefore() {
        return moviesLinksCountBefore;
    }

    public void setMoviesLinksCountBefore(int moviesLinksCountBefore) {
        this.moviesLinksCountBefore = moviesLinksCountBefore;
    }

    public int getMoviesLinksCountAfter() {
        return moviesLinksCountAfter;
    }

    public void setMoviesLinksCountAfter(int moviesLinksCountAfter) {
        this.moviesLinksCountAfter = moviesLinksCountAfter;
    }

    public int getTvShowsLinksCountBefore() {
        return tvShowsLinksCountBefore;
    }

    public void setTvShowsLinksCountBefore(int tvShowsLinksCountBefore) {
        this.tvShowsLinksCountBefore = tvShowsLinksCountBefore;
    }

    public int getTvShowsLinksCountAfter() {
        return tvShowsLinksCountAfter;
    }

    public void setTvShowsLinksCountAfter(int tvShowsLinksCountAfter) {
        this.tvShowsLinksCountAfter = tvShowsLinksCountAfter;
    }

    public int getMissingLinksCountBefore() {
        return missingLinksCountBefore;
    }

    public void setMissingLinksCountBefore(int missingLinksCountBefore) {
        this.missingLinksCountBefore = missingLinksCountBefore;
    }

    public int getMissingLinksCountAfter() {
        return missingLinksCountAfter;
    }

    public void setMissingLinksCountAfter(int missingLinksCountAfter) {
        this.missingLinksCountAfter = missingLinksCountAfter;
    }

    public int getInvalidLinksCountBefore() {
        return invalidLinksCountBefore;
    }

    public void setInvalidLinksCountBefore(int invalidLinksCountBefore) {
        this.invalidLinksCountBefore = invalidLinksCountBefore;
    }

    public int getInvalidLinksCountAfter() {
        return invalidLinksCountAfter;
    }

    public void setInvalidLinksCountAfter(int invalidLinksCountAfter) {
        this.invalidLinksCountAfter = invalidLinksCountAfter;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getKidsLinksCountBefore() {
        return kidsLinksCountBefore;
    }

    public void setKidsLinksCountBefore(int kidsLinksCountBefore) {
        this.kidsLinksCountBefore = kidsLinksCountBefore;
    }

    public int getKidsLinksCountAfter() {
        return kidsLinksCountAfter;
    }

    public void setKidsLinksCountAfter(int kidsLinksCountAfter) {
        this.kidsLinksCountAfter = kidsLinksCountAfter;
    }
}
