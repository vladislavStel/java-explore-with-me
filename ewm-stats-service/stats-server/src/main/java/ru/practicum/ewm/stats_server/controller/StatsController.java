package ru.practicum.ewm.stats_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common_dto.EndpointHitDto;
import ru.practicum.ewm.common_dto.ViewStatDto;
import ru.practicum.ewm.stats_server.service.StatsService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStatDto> getStatistic(@RequestParam(name = "start") String start,
                                          @RequestParam(name = "end") String end,
                                          @RequestParam(name = "uris", required = false) List<String> uris,
                                          @RequestParam(name = "unique", required = false,
                                                  defaultValue = "false") Boolean unique) {
        log.info("StatsServer: received GET request");
        return statsService.findStatistic(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postInfoRequestForStatistic(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("StatsServer: received POST request");
        statsService.saveInfoEvent(endpointHitDto);
    }

}