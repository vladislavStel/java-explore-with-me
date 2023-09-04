package ru.practicum.ewm.main_service.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main_service.comment.dto.CommentDto;
import ru.practicum.ewm.main_service.comment.dto.NewCommentDto;
import ru.practicum.ewm.main_service.comment.enums.CommentState;
import ru.practicum.ewm.main_service.comment.model.Comment;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.user.model.User;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toNewComment(NewCommentDto newCommentDto, Event event, User author) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .status(CommentState.PENDING)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getId())
                .event(comment.getEvent().getId())
                .status(comment.getStatus())
                .created(comment.getCreated())
                .build();
    }

}