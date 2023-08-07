package ru.practicum.ewm.statsServer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.commonDto.dto.EndpointHitDto;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;
import ru.practicum.ewm.statsServer.service.StatsService;

import java.util.List;

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
        return statsService.findStatistic(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postInfoRequestForStatistic(@RequestBody EndpointHitDto endpointHitDto) {
        statsService.saveInfoEvent(endpointHitDto);
    }

}