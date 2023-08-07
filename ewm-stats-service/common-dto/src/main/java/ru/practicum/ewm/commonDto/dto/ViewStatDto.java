package ru.practicum.ewm.commonDto.dto;

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