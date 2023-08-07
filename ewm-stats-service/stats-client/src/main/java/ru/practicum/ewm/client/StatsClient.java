package ru.practicum.ewm.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.commonDto.dto.EndpointHitDto;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;

import java.util.*;

@Service
public class StatsClient {

    private static final String STATS_SERVER_URL = "http://stats-server:9090";
    private static final String PATH_HIT = "/hit";
    private static final String PATH_STATS = "/stats";

    private final RestTemplate rest;

    public StatsClient() {
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(STATS_SERVER_URL))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        rest.postForObject(PATH_HIT, endpointHitDto, EndpointHitDto.class);
    }

    public List<ViewStatDto> getStats(String start, String end, List<String> uris, boolean unique) {

        String requestUri = PATH_STATS + "?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> param = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );

        ResponseEntity<ViewStatDto[]> entity = rest.getForEntity(requestUri, ViewStatDto[].class, param);

        return entity.getBody() != null ? Arrays.asList(entity.getBody()) : Collections.emptyList();
    }

}