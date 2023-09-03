package ru.practicum.ewm.main_service.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main_service.comment.dto.AdminUpdateCommentDto;
import ru.practicum.ewm.main_service.comment.dto.CommentDto;
import ru.practicum.ewm.main_service.comment.dto.NewCommentDto;
import ru.practicum.ewm.main_service.comment.dto.UserUpdateCommentDto;
import ru.practicum.ewm.main_service.comment.enums.CommentSort;
import ru.practicum.ewm.main_service.comment.enums.CommentState;
import ru.practicum.ewm.main_service.comment.mapper.CommentMapper;
import ru.practicum.ewm.main_service.comment.model.Comment;
import ru.practicum.ewm.main_service.comment.repository.CommentRepository;
import ru.practicum.ewm.main_service.event.enums.EventState;
import ru.practicum.ewm.main_service.event.enums.StateAction;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.exception.error.InvalidObjectStatusException;
import ru.practicum.ewm.main_service.exception.error.InvalidRequestParameterException;
import ru.practicum.ewm.main_service.exception.error.ObjectNotFoundException;
import ru.practicum.ewm.main_service.request.enums.RequestStatus;
import ru.practicum.ewm.main_service.request.repository.RequestRepository;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;
    private final RequestRepository requestRepository;

    @Override
    public CommentDto saveComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userService.findUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new InvalidObjectStatusException("Posting comments is allowed only for events" +
                    " with the status PUBLISHED");
        }
        boolean isRequester = requestRepository.findAllByEventId(eventId).stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .anyMatch(r -> r.getRequester().equals(user));
        if (!Objects.equals(event.getInitiator().getId(), user.getId()) && !isRequester) {
            throw new InvalidObjectStatusException("Comments can only be left by the initiator" +
                    " or participant of the event");
        }
        return CommentMapper.toCommentDto(commentRepository
                .save(CommentMapper.toNewComment(newCommentDto, event, user)));
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UserUpdateCommentDto updateCommentDto) {
        User user = userService.findUserById(userId);
        Comment comment = getById(commentId);
        if (!Objects.equals(comment.getAuthor(), user)) {
            throw new InvalidObjectStatusException("Comments can only be edited by the author");
        }
        if (Objects.equals(comment.getStatus(), CommentState.PUBLISHED)) {
            throw new InvalidRequestParameterException("You can't edit a comment with the status PUBLISHED");
        }
        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        if (!commentRepository.existsByIdAndAuthor_Id(commentId, userId)) {
            throw new ObjectNotFoundException(String.format("Comment not found: id=%d", commentId));
        }
        log.info("Comment={} deleted by userId={}", userId, commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto patchCommentsAdminRequest(Long commentId, AdminUpdateCommentDto adminUpdateCommentDto) {
        Comment comment = getById(commentId);
        if (Objects.equals(CommentState.PUBLISHED, comment.getStatus()) ||
                Objects.equals(CommentState.CANCELED, comment.getStatus())) {
            throw new InvalidObjectStatusException("Post comment with PENDING status only");
        }
        if (Objects.equals(StateAction.PUBLISH_COMMENT, adminUpdateCommentDto.getStatus())) {
            comment.setStatus(CommentState.PUBLISHED);
        }
        if (Objects.equals(StateAction.REJECT_COMMENT, adminUpdateCommentDto.getStatus())) {
            comment.setStatus(CommentState.CANCELED);
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments(Long eventId, CommentSort commentSort, int from, int size) {
        Sort sort;
        if (Objects.equals(commentSort, CommentSort.CREATE_DATE)) {
            sort = Sort.by("created").descending();
        } else {
            sort = Sort.by("author_id");
        }
        PageRequest page = PageRequest.of(from / size, size, sort);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(eventId, CommentState.PUBLISHED, page);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        Comment comment = getById(id);
        if (!Objects.equals(comment.getStatus(), CommentState.PUBLISHED)) {
            throw new InvalidObjectStatusException("Comment not published");
        }
        log.info("Got a comment in the repository, id={}", id);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(Long commentId) {
        log.error("Comment not found, id={}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Comment not found: id=%d", commentId)));
    }

}