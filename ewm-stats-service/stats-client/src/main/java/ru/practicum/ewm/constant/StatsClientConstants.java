package ru.practicum.ewm.constant;

import java.time.format.DateTimeFormatter;

public class StatsClientConstants {

    public static final String STATS_SERVER_URL = "http://stats-server:9090";
    public static final String PATH_HIT = "/hit";
    public static final String PATH_STATS = "/stats";

    public static final String PATH_DATE = "?start={start}&end={end}";

    public static final String PATH_URIS = "&uris={uris}";

    public static final String PATH_UNIQUE_IP = "&unique={unique}";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

}