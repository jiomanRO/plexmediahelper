package com.john.plexmediahelper.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class AppConfiguration {
    @Value("${nas.media.dir.name}")
    String transmissionDirName;
    @Value("${nas.media.dir.name}")
    String nasTransmissionDirName;
    @Value("${nas.tvshows_link.dir.name}")
    String nasTVShowsLinkDirName;
    @Value("${nas.movies_link.dir.name}")
    String nasMoviesLinkDirName;
    @Value("#{'${movies.extensions}'.split(',')}")
    private List<String> moviesExtensions;
    @Value("#{'${filenames.exceptions}'.split(',')}")
    private List<String> filenamesExceptions;
    ArrayList<String> filesList = new ArrayList<>();
}
