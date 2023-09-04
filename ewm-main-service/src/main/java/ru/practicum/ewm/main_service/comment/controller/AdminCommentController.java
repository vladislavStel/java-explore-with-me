package ru.practicum.ewm.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.comment.dto.AdminUpdateCommentDto;
import ru.practicum.ewm.main_service.comment.dto.CommentDto;
import ru.practicum.ewm.main_service.comment.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto patchCommentAdminRequest(@PathVariable("commentId") Long commentId,
                                               @Valid @RequestBody AdminUpdateCommentDto adminUpdateCommentDto) {
        log.info("MainService: Admin patch comment request, commentId={}, adminUpdateComment={}",
                commentId, adminUpdateCommentDto);
        return commentService.patchCommentsAdminRequest(commentId, adminUpdateCommentDto);
    }

}