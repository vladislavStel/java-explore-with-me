package ru.practicum.ewm.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.enums.EventSort;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.statistic.StatisticService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatisticService statisticService;

    @GetMapping
    public List<EventShortDto> getEventsBySearchQueryAndSort(@RequestParam(required = false) @Length(min = 3) String text,
                                                             @RequestParam(required = false) List<Long> categories,
                                                             @RequestParam(required = false) Boolean paid,
                                                             @RequestParam(name = "rangeStart", required = false)
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                 LocalDateTime rangeStart,
                                                             @RequestParam(name = "rangeEnd", required = false)
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                 LocalDateTime rangeEnd,
                                                             @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                             @RequestParam(name = "sort", required = false) EventSort sort,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                             @Positive @RequestParam(defaultValue = "10") int size,
                                                             HttpServletRequest request) {
        statisticService.addHit(request);
        log.info("MainService: Get events by search query, test={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findEventsBySearchQueryAndSort(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventFullInfo(@PathVariable("id") long id, HttpServletRequest request) {
        statisticService.addHit(request);
        log.info("MainService: Get events full info, id={}", id);
        return eventService.findEventFullInfo(id);
    }

}