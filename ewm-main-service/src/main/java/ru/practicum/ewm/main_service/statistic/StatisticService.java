package ru.practicum.ewm.main_service.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common_dto.EndpointHitDto;
import ru.practicum.ewm.common_dto.ViewStatDto;
import ru.practicum.ewm.stats_client.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatsClient statsClient;

    public void addHit(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String app = "ewm-main-service";
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now()).build();
        statsClient.addHit(endpointHitDto);
    }

    public List<ViewStatDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }

}