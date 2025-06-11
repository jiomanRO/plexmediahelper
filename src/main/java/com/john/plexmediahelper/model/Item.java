package com.john.plexmediahelper.model;

import java.util.List;

public class Item {
    private String name;
    private String type;

    private String contentType;

    private List<String> genre;
    private String status;

    public Item (String name, String type, String contentType, List<String> genre, String status) {
        this.name = name;
        this.type = type;
        this.contentType = contentType;
        this.genre = genre;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
