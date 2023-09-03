package ru.practicum.ewm.main_service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main_service.comment.enums.CommentState;
import ru.practicum.ewm.main_service.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndStatus(Long eventId, CommentState state, Pageable page);

    Boolean existsByIdAndAuthor_Id(Long commentId, Long authorId);

}