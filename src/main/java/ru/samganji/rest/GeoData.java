package ru.samganji.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//основной класс для запроса к OSM и его парсинга
public class GeoData {

    private String name;
    private String type;
    private String response;

    public GeoData() {
    }

    GeoData(String type, String name) {
        this.name = name;
        this.type = type;
        query();
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

    public String getResponse() {
        return response;
    }

    //Запрос к OSM
    private void query() {
        String url = "https://nominatim.openstreetmap.org/search?"
                + type + "=" + name + "&country=russia&format=json&polygon_geojson=1";
        HttpURLConnection connection = null;
        StringBuilder response = null;
        try {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(Objects.requireNonNull(connection).getInputStream()))) {
                    String inputLine;
                    response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
                }
            } else throw new NotFoundException();
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { if (connection != null) connection.disconnect(); }

        this.response = (response==null ? null : response.toString());
    }

    //поиск в запросе по рег.выражению
    private String match(String regex, int groupNum) {
        String result;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(response);
        try {
            m.find();
            result = m.group(groupNum);
        }
        catch (Exception e) { throw new NotFoundException();}

        return result;
    }

    //парсинг запроса
    String getCenterPoint() {
        String x = match("lat\":\"(\\d+.\\d+)\"", 1);
        String y = match("lon\":\"(\\d+.\\d+)\"", 1);

        return "["+x+", "+y+"]";
    }

    String getCoordinates() { return match("coordinates\":(\\S+)}", 1); }

    String getObjectName() { return match("display_name\":\"(.+?)\",", 1);}
}
