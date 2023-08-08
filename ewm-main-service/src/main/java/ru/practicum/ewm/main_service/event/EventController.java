package ru.practicum.ewm.main_service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.stats_client.client.StatsClient;
import ru.practicum.ewm.common_dto.EndpointHitDto;
import ru.practicum.ewm.common_dto.ViewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/event")
public class EventController {

    private final StatsClient statsClient;

    @GetMapping("/hit")
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

    @GetMapping("/stats")
    public List<ViewStatDto> getStatistic() {
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now().plusYears(5);
        List<String> uris = List.of("/event/hit");
        boolean unique = true;
        return statsClient.getStats(start, end, uris, unique);
    }

    @GetMapping("/statsNotUri")
    public List<ViewStatDto> getStatisticNotUris() {
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now().plusYears(5);
        boolean unique = true;
        return statsClient.getStats(start, end, null, unique);
    }

    @GetMapping("/statsNotUnique")
    public List<ViewStatDto> getStatisticNotUnique() {
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now().plusYears(5);
        List<String> uris = List.of("/event/hit");
        return statsClient.getStats(start, end, uris, null);
    }

}