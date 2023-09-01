package ru.practicum.ewm.main_service.category.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50)
    String name;

}