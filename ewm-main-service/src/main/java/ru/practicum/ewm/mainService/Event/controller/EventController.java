package ru.practicum.ewm.mainService.Event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.commonDto.dto.EndpointHitDto;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/event")
public class EventController {

    private final StatsClient statsClient;

    @GetMapping
    public void addHit(EndpointHitDto endpointHitDto) {
        statsClient.addHit(endpointHitDto);
    }

    @GetMapping("/{id}")
    public List<ViewStatDto> getStatistic(@PathVariable String id) {
        String start = "2020-05-05 00:00:00";
        String end = "2035-05-05 00:00:00";
        List<String> uris = List.of("/event/" + id);
        boolean unique = true;
        return statsClient.getStats(start, end, uris, unique);
    }

}