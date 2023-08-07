package ru.practicum.ewm.statsServer.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.commonDto.dto.ViewStatDto;
import ru.practicum.ewm.statsServer.model.ViewStat;

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