package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.commonDto.dto.EndpointHitDto;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;

import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.ewm.constant.StatsClientConstants.*;

@Slf4j
@Service
@Validated
public class StatsClient {

    private final RestTemplate rest;

    public StatsClient() {
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(STATS_SERVER_URL))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        rest.postForObject(STATS_SERVER_URL + PATH_HIT, endpointHitDto, EndpointHitDto.class);
        log.info("StatsClient: create POST request in StatsServer");
    }

    public List<ViewStatDto> getStats(@NotNull LocalDateTime start, @NotNull LocalDateTime end,
                                      List<String> uris, Boolean unique) {
        StringBuilder requestUri = new StringBuilder(STATS_SERVER_URL + PATH_STATS + PATH_DATE);
        String startDate = dateEncoder(start);
        String endDate = dateEncoder(end);

        Map<String, Object> param = new HashMap<>();
        param.put("start", startDate);
        param.put("end", endDate);

        if (uris != null) {
            param.put("uris", String.join(",", uris));
            requestUri.append(PATH_URIS);
        }

        if (unique != null) {
            param.put("unique", unique);
            requestUri.append(PATH_UNIQUE_IP);
        }

        ResponseEntity<ViewStatDto[]> entity = rest.getForEntity(requestUri.toString(), ViewStatDto[].class, param);
        log.info("StatsClient: create GET request in StatsServer");
        return entity.getBody() != null ? Arrays.asList(entity.getBody()) : Collections.emptyList();
    }

    public String dateEncoder(LocalDateTime date) {
        return URLEncoder.encode(date.format(FORMATTER), StandardCharsets.UTF_8);
    }

}