package ru.practicum.ewm.statsServer.service;

import ru.practicum.ewm.commonDto.dto.EndpointHitDto;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;

import java.util.List;

public interface StatsService {

    List<ViewStatDto> findStatistic(String start, String end, List<String> uris, Boolean unique);

    void saveInfoEvent(EndpointHitDto endpointHitDto);

}