package ru.practicum.ewm.common_dto;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatDto {

    private String app;
    private String uri;
    private long hits;

}