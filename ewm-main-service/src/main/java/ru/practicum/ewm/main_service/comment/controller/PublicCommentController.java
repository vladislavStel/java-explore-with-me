package ru.practicum.ewm.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.comment.dto.CommentDto;
import ru.practicum.ewm.main_service.comment.enums.CommentSort;
import ru.practicum.ewm.main_service.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllComments(@RequestParam(value = "sort", required = false) CommentSort commentSort,
                                           @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(value = "size", defaultValue = "10") @Positive int size,
                                           @RequestParam Long eventId) {
        log.info("MainService: Get all comments by eventId: {}", eventId);
        return commentService.getAllComments(eventId, commentSort, from, size);
    }

    @GetMapping("{commentId}")
    public CommentDto getCommentById(@PathVariable(name = "commentId") Long id) {
        log.info("MainService: Get comment by id={}", id);
        return commentService.getCommentById(id);
    }

}