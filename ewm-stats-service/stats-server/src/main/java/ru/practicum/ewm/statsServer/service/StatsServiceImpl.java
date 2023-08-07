package ru.practicum.ewm.statsServer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.commonDto.dto.EndpointHitDto;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;
import ru.practicum.ewm.statsServer.mapper.EndpointHitMapper;
import ru.practicum.ewm.statsServer.mapper.ViewStatMapper;
import ru.practicum.ewm.statsServer.model.ViewStat;
import ru.practicum.ewm.statsServer.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatDto> findStatistic(String start, String end, List<String> uris, Boolean unique) {
        List<ViewStat> statList;
        LocalDateTime startDate = dateDecoder(start);
        LocalDateTime endDate = dateDecoder(end);
        if (uris == null) {
            if (unique) {
                statList = statsRepository.findStatsBetweenStartAndEndAndUniqueIp(startDate, endDate);
            } else {
                statList = statsRepository.findStatsBetweenStartAndEnd(startDate, endDate);
            }
        } else {
            if (unique) {
                statList = statsRepository.findStatsBetweenStartAndEndByUriAndUniqueIp(startDate, endDate, uris);
            } else {
                statList = statsRepository.findStatsBetweenStartAndEndByUri(startDate, endDate, uris);
            }
        }
        log.info("StatsServer: data received and sent");
        return statList.stream().map(ViewStatMapper::toViewStatDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveInfoEvent(EndpointHitDto endpointHitDto) {
        log.info("StatsServer: data saved in database");
        statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    private LocalDateTime dateDecoder(String date) {
        return LocalDateTime.parse(URLDecoder.decode(date, StandardCharsets.UTF_8), FORMATTER);
    }

}