package com.john.plexmediahelper.utilities;

import com.john.plexmediahelper.model.Data;
import com.john.plexmediahelper.model.Item;
import com.john.plexmediahelper.services.SSHService;
import com.john.plexmediahelper.services.TmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContentProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentProcessor.class);

    @Autowired
    Data data;
    @Autowired
    SSHService sshService;

    @Autowired
    TmdbService tmdbService;

    @Value("${nas.media.dir.name}")
    String nasTransmissionDirName;
    @Value("${nas.tvshows_link.dir.name}")
    String nasTVShowsLinkDirName;
    @Value("${nas.movies_link.dir.name}")
    String nasMoviesLinkDirName;

    @Value("${nas.kids_link.dir.name}")
    String nasKidsLinkDirName;

    @Value("#{'${filenames.exceptions}'.split(',')}")
    private List<String> filenamesExceptions;
    @Value("#{'${movies.extensions}'.split(',')}")
    private List<String> moviesExtensions;

    public void getAndProcessContent() {
        data.setAllItemsList(sshService.getContentOfDirWithType(nasTransmissionDirName));
        //data.setAllFilesAndFoldersList(sshService.getContentOfDir(nasTransmissionDirName));
        data.setCurrentTVShowsLinks(sshService.getContentOfDir(nasTVShowsLinkDirName));
        data.setCurrentMoviesLinks(sshService.getContentOfDir(nasMoviesLinkDirName));
        data.setCurrentKidsLinks(sshService.getContentOfDir(nasKidsLinkDirName));
        ArrayList<String> invalidLinks = sshService.getInvalidLinks(nasMoviesLinkDirName);
        invalidLinks.addAll(sshService.getInvalidLinks(nasTVShowsLinkDirName));
        invalidLinks.addAll(sshService.getInvalidLinks(nasKidsLinkDirName));
        data.setInvalidLinks(invalidLinks);
        //determine content type (Movie, TvShow, Other)
        for(final Item item : data.getAllItemsList()) {
            if(item.getType().equals("dir")) {
                String dirName = item.getName();
                boolean skip = false;
                for (String filenameException : filenamesExceptions) {
                    if(dirName.matches(filenameException)) {
                        LOGGER.info("This " + dirName + " matches a filename exception (" + filenameException + "), will be skipped.");
                        item.setContentType("Other");
                        item.setStatus("Not needed");
                        skip = true;
                    }
                }
                if (!skip) {
                    if (dirName.matches(".*(\\.|\\s)[sS]([0-9][1-9]|[0-9]0)(\\.|\\s).*")) {
                        LOGGER.info("This looks like a TV Show: " + dirName + ", checking if it is empty...");
                        ArrayList<String> filesInDir = sshService.getContentOfDir(dirName);
                        if (filesInDir.isEmpty() || (filesInDir.size() == 1 && filesInDir.get(0).equals(".thumb"))) {
                            LOGGER.info("This directory " + dirName + " is empty, not adding to list.");
                            item.setContentType("Empty dir");
                            item.setStatus("Not needed");
                        } else {
                            item.setContentType("TVShow");
                            if(data.getCurrentTVShowsLinks().contains(item.getName().substring(nasTransmissionDirName.length())) ||
                                    data.getCurrentKidsLinks().contains(item.getName().substring(nasTransmissionDirName.length()))) {
                                item.setStatus("Present");
                            } else {
                                item.setStatus("Missing");
                            }
                        }
                    } else {
                        ArrayList<String> filesInDir = sshService.getContentOfDir(dirName);
                        if (filesInDir.isEmpty() || (filesInDir.size() == 1 && filesInDir.get(0).equals(".thumb"))) {
                            LOGGER.info("This directory " + dirName + " is empty, not adding to list.");
                            item.setContentType("Empty dir");
                            item.setStatus("Not needed");
                        } else {
                            boolean containsMovieFile = false;
                            for (String file : filesInDir) {
                                for (String extension : moviesExtensions)
                                    if (file.endsWith(extension)) {
                                        containsMovieFile = true;
                                        break;
                                    }
                            }
                            if (containsMovieFile) {
                                item.setContentType("Movie");
                                if(data.getCurrentMoviesLinks().contains(item.getName().substring(nasTransmissionDirName.length())) ||
                                        data.getCurrentKidsLinks().contains(item.getName().substring(nasTransmissionDirName.length()))) {
                                    item.setStatus("Present");
                                } else {
                                    item.setStatus("Missing");
                                }
                            } else {
                                LOGGER.info("This " + dirName + " is not a tv show or a movie directory.");
                                item.setContentType("Other");
                                item.setStatus("Not needed");
                            }
                        }
                    }
                }
            } else {
                String file = item.getName();
                for(String extension : moviesExtensions) {
                    if (file.endsWith(extension)) {
                        item.setContentType("Movie");
                    }
                }
                if(item.getContentType().equals("")) {
                    item.setContentType("Other");
                    item.setStatus("Not needed");
                } else {
                    if(data.getCurrentMoviesLinks().contains(item.getName().substring(nasTransmissionDirName.length())) ||
                        data.getCurrentKidsLinks().contains(item.getName().substring(nasTransmissionDirName.length()))) {
                        item.setStatus("Present");
                    } else {
                        item.setStatus("Missing");
                    }
                }
            }
        }
        //determine genre for Movies and TvShows
        findGenreForMoviesAndTvShows();
    }

    public void findGenreForMoviesAndTvShows(){
        for(Item item : data.getAllItemsList()) {
            if(item.getContentType().equals("Movie") || item.getContentType().equals("TVShow")) {
                List<String> genreList = tmdbService.getGenres(item);
                item.setGenre(genreList);
                LOGGER.info("Genre for " + extractMovieOrShowTitle(item.getName()) + " is: " + genreList);
            }
        }
    }

    public static String extractMovieOrShowTitle(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        // Match from start until season marker (Sxx), year, or resolution
        Pattern pattern = Pattern.compile("^([A-Za-z0-9\\.']+?)(?:\\.S\\d{2}|\\.(?:19|20)\\d{2})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            // Replace dots with spaces, handle apostrophes
            return matcher.group(1).replace(".", " ");
        }
        return "";
    }

    public void createLinks() {
        LOGGER.info("Create commands to create links...");
        ArrayList<String> createLinksCommands = new ArrayList<>();
        String command;
        for(Item item : data.getAllItemsList()) {
            //Family movies and tvshows will have links created in the kids link dir
            if(item.getGenre().contains("Family")) {
                command = "ln -s \"" + item.getName() + "\" \"" + item.getName().replace(nasTransmissionDirName, nasKidsLinkDirName) + "\"";
                createLinksCommands.add(command);
            } else {
                if (item.getContentType().equals("TVShow")) {
                    command = "ln -s \"" + item.getName() + "\" \"" + item.getName().replace(nasTransmissionDirName, nasTVShowsLinkDirName) + "\"";
                    createLinksCommands.add(command);
                } else {
                    if (item.getContentType().equals("Movie")) {
                        command = "ln -s \"" + item.getName() + "\" \"" + item.getName().replace(nasTransmissionDirName, nasMoviesLinkDirName) + "\"";
                        createLinksCommands.add(command);
                    }
                }
            }
        }
        LOGGER.info("Created " + createLinksCommands.size() + " create links commands.");
        for(String comm : createLinksCommands) {
            LOGGER.info(comm);
        }
        sshService.executeRemoteCommands(createLinksCommands);
    }

    public void createMissingLinks() {
        LOGGER.info("Create commands to create missing links...");
        ArrayList<String> createMissingLinksCommands = new ArrayList<>();
        String command;
        for(Item item : data.getAllItemsList()) {
            if(item.getStatus().equals("Missing")) {
                if(item.getGenre().contains("Family")) {
                    command = "ln -s \"" + item.getName() + "\" \"" + item.getName().replace(nasTransmissionDirName, nasKidsLinkDirName) + "\"";
                    createMissingLinksCommands.add(command);
                } else {
                    if(item.getContentType().equals("TVShow")) {
                        command = "ln -s \"" + item.getName() + "\" \"" + item.getName().replace(nasTransmissionDirName, nasTVShowsLinkDirName) + "\"";
                        createMissingLinksCommands.add(command);
                    } else {
                        if(item.getContentType().equals("Movie") ) {
                            command = "ln -s \"" + item.getName() + "\" \"" + item.getName().replace(nasTransmissionDirName, nasMoviesLinkDirName) + "\"";
                            createMissingLinksCommands.add(command);
                        }
                    }
                }
            }
        }
        if(createMissingLinksCommands.size() > 0) {
            LOGGER.info("Created " + createMissingLinksCommands.size() + " create links commands.");
            for (String comm : createMissingLinksCommands) {
                LOGGER.info(comm);
            }
            sshService.executeRemoteCommands(createMissingLinksCommands);
        } else {
            LOGGER.info("There are no missing links to create.");
        }
    }

    public void deleteInvalidLinks() {
        sshService.deleteInvalidLinks(nasTVShowsLinkDirName);
        sshService.deleteInvalidLinks(nasMoviesLinkDirName);
        sshService.deleteInvalidLinks(nasKidsLinkDirName);
    }

    public void deleteAllLinks() {
        sshService.deleteLinks(nasTVShowsLinkDirName);
        sshService.deleteLinks(nasMoviesLinkDirName);
        sshService.deleteLinks(nasKidsLinkDirName);
    }
}
