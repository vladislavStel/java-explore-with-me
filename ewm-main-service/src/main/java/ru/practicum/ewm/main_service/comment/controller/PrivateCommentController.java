package ru.practicum.ewm.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.comment.dto.CommentDto;
import ru.practicum.ewm.main_service.comment.dto.NewCommentDto;
import ru.practicum.ewm.main_service.comment.dto.UserUpdateCommentDto;
import ru.practicum.ewm.main_service.comment.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("userId") Long userId,
                                    @RequestParam("eventId") Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("MainService: Create comment by eventId: {}, userId: {}, comment: {}", eventId, userId, newCommentDto);
        return commentService.saveComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("{commentId}")
    public CommentDto updateComment(@PathVariable("userId") Long userId,
                                    @PathVariable("commentId") Long commentId,
                                    @Valid @RequestBody UserUpdateCommentDto updateCommentDto) {
        log.info("MainService: Update comment by commentId: {}, userId: {}, comment: {}", commentId, userId,
                updateCommentDto);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("commentId") Long commentId) {
        log.info("MainService: Delete comment by commentId: {}, userId: {}", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }

}