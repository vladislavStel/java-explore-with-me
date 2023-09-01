package ru.practicum.ewm.main_service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main_service.request.enums.RequestStatus;
import ru.practicum.ewm.main_service.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    Integer countRequestsByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

}