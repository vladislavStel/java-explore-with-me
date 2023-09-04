package ru.practicum.ewm.main_service.comment.service;

import ru.practicum.ewm.main_service.comment.dto.AdminUpdateCommentDto;
import ru.practicum.ewm.main_service.comment.dto.CommentDto;
import ru.practicum.ewm.main_service.comment.dto.NewCommentDto;
import ru.practicum.ewm.main_service.comment.dto.UserUpdateCommentDto;
import ru.practicum.ewm.main_service.comment.enums.CommentSort;
import ru.practicum.ewm.main_service.comment.model.Comment;

import java.util.List;

public interface CommentService {

    CommentDto saveComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UserUpdateCommentDto updateCommentDto);

    void deleteComment(Long userId, Long commentId);

    CommentDto patchCommentsAdminRequest(Long commentId, AdminUpdateCommentDto adminUpdateCommentDto);

    List<CommentDto> getAllComments(Long eventId, CommentSort commentSort, int from, int size);

    CommentDto getCommentById(Long id);

    Comment getById(Long commentId);

}