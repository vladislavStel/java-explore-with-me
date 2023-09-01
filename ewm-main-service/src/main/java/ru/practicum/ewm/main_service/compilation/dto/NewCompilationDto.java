package ru.practicum.ewm.main_service.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50)
    String title;
    final List<Long> events = new ArrayList<>();

}