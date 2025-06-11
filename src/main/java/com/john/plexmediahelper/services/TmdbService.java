package com.john.plexmediahelper.services;

import com.john.plexmediahelper.model.Item;

import java.util.List;

public interface TmdbService {
    int getTMDBID(Item item);
    List<String> getGenres(Item item);
    //List<String> getGenresForMovie(String movieTitle);
}
