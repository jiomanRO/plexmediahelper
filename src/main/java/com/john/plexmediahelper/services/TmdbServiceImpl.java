package com.john.plexmediahelper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.john.plexmediahelper.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TmdbServiceImpl implements TmdbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmdbServiceImpl.class);

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getTMDBID(Item item) {
        int movieId = 0;
        String searchUrl = "";
        String encodedTitle = "";

        try {
            String movieOrShowTitle = extractMovieOrShowTitle(item.getName());
            if(movieOrShowTitle.equals("")) {
                LOGGER.error("Couldnt extract movie or tvShow name from " + item.getName());
                return 0;
            }
            encodedTitle = URLEncoder.encode(movieOrShowTitle, StandardCharsets.UTF_8);
            if (item.getContentType().equals("Movie")) {
                searchUrl = baseUrl + "/search/movie?api_key=" + apiKey + "&query=" + encodedTitle;
            }
            if (item.getContentType().equals("TVShow")) {
                searchUrl = baseUrl + "/search/tv?api_key=" + apiKey + "&query=" + encodedTitle;
            }

            HttpRequest searchRequest = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .GET()
                    .build();

            HttpResponse<String> searchResponse = httpClient.send(searchRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode searchRoot = objectMapper.readTree(searchResponse.body());
            JsonNode results = searchRoot.get("results");

            if (results == null || !results.isArray() || results.isEmpty()) {
                LOGGER.error("No results returned from querying TMDB by " + encodedTitle);
                return 0;  // Return empty list if not found
            }
            LOGGER.info(results.asText());
            movieId = results.get(0).get("id").asInt();
        } catch (Exception e) {
            LOGGER.error("TMDb lookup failed: " + e.getMessage());
            //System.err.println("TMDb lookup failed: " + e.getMessage());
        }
        return movieId;
    }

    @Override
    public List<String> getGenres(Item item) {
        List<String> genreNames = new ArrayList<>();
        int movieId = getTMDBID(item);
        String url = "";
        if (movieId != 0) {
            if (item.getContentType().equals("Movie")) {
                url = baseUrl + "/movie/" + movieId + "?api_key=" + apiKey;
            }
            if (item.getContentType().equals("TVShow")) {
                url = baseUrl + "/tv/" + movieId + "?api_key=" + apiKey;
            }
            if (url != "") {
                try {

                    HttpRequest movieRequest = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

                    HttpResponse<String> movieResponse = httpClient.send(movieRequest, HttpResponse.BodyHandlers.ofString());
                    JsonNode movieRoot = objectMapper.readTree(movieResponse.body());
                    JsonNode genres = movieRoot.get("genres");
                    LOGGER.debug(genres.asText());

                    if (genres != null && genres.isArray()) {
                        for (JsonNode genre : genres) {
                            genreNames.add(genre.get("name").asText());
                        }
                    }

                } catch (Exception e) {
                    LOGGER.error("TMDb lookup failed: " + e.getMessage());
                    System.err.println("TMDb lookup failed: " + e.getMessage());
                }
            }
        }
        return genreNames;
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
}