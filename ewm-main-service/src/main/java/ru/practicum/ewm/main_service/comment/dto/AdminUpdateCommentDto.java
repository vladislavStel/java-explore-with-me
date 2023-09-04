package ru.practicum.ewm.main_service.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.event.enums.StateAction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class AdminUpdateCommentDto {

    @NotBlank
    @Size(min = 10, max = 2000)
    String text;

    StateAction status;

}