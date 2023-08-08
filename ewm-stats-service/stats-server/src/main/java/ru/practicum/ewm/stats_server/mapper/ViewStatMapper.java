package ru.practicum.ewm.stats_server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.common_dto.ViewStatDto;
import ru.practicum.ewm.stats_server.model.ViewStat;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewStatMapper {

    public static ViewStatDto toViewStatDto(ViewStat viewStat) {
        return ViewStatDto.builder()
                .app(viewStat.getApp())
                .uri(viewStat.getUri())
                .hits(viewStat.getHits())
                .build();
    }

}