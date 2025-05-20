package com.john.plexmediahelper.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Data {

    ArrayList<Item> allItemsList;

    ArrayList<String> allFilesAndFoldersList;
    ArrayList<String> filesList;
    ArrayList<String> dirList;
    ArrayList<String> TVShowsDirList;
    ArrayList<String> MoviesList;
    ArrayList<String> currentMoviesLinks;
    ArrayList<String> currentTVShowsLinks;

    ArrayList<String> invalidLinks;

    public Data() {
        allItemsList = new ArrayList<>();
        filesList = new ArrayList<>();
        dirList = new ArrayList<>();
        TVShowsDirList = new ArrayList<>();
        MoviesList = new ArrayList<>();
        currentMoviesLinks = new ArrayList<>();
        currentTVShowsLinks = new ArrayList<>();
        invalidLinks = new ArrayList<>();
    }

    public ArrayList<Item> getAllItemsList() {
        return allItemsList;
    }

    public void setAllItemsList(ArrayList<Item> allItemsList) {
        this.allItemsList = allItemsList;
    }

    public ArrayList<String> getAllFilesAndFoldersList() {
        return allFilesAndFoldersList;
    }

    public void setAllFilesAndFoldersList(ArrayList<String> allFilesAndFoldersList) {
        this.allFilesAndFoldersList = allFilesAndFoldersList;
    }

    public ArrayList<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(ArrayList<String> filesList) {
        this.filesList = filesList;
    }

    public ArrayList<String> getDirList() {
        return dirList;
    }

    public void setDirList(ArrayList<String> dirList) {
        this.dirList = dirList;
    }

    public ArrayList<String> getTVShowsDirList() {
        return TVShowsDirList;
    }

    public void setTVShowsDirList(ArrayList<String> TVShowsDirList) {
        this.TVShowsDirList = TVShowsDirList;
    }

    public ArrayList<String> getMoviesList() {
        return MoviesList;
    }

    public void setMoviesList(ArrayList<String> moviesList) {
        MoviesList = moviesList;
    }

    public ArrayList<String> getCurrentMoviesLinks() {
        return currentMoviesLinks;
    }

    public void setCurrentMoviesLinks(ArrayList<String> currentMoviesLinks) {
        this.currentMoviesLinks = currentMoviesLinks;
    }

    public ArrayList<String> getCurrentTVShowsLinks() {
        return currentTVShowsLinks;
    }

    public void setCurrentTVShowsLinks(ArrayList<String> currentTVShowsLinks) {
        this.currentTVShowsLinks = currentTVShowsLinks;
    }

    public ArrayList<String> getInvalidLinks() {
        return invalidLinks;
    }

    public void setInvalidLinks(ArrayList<String> invalidLinks) {
        this.invalidLinks = invalidLinks;
    }
}
