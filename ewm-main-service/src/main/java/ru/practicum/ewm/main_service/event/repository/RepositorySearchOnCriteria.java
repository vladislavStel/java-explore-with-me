package ru.practicum.ewm.main_service.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main_service.event.enums.EventSort;
import ru.practicum.ewm.main_service.event.enums.EventState;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.exception.error.IncorrectlyRequestException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RepositorySearchOnCriteria {

    private final EntityManager em;

    public List<Event> searchEventsByQueryAndSort(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  EventSort sort, int from, int size) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteria = builder.createQuery(Event.class);
        Root<Event> eventRoot = criteria.from(Event.class);
        eventRoot.join("initiator");
        eventRoot.join("category");

        List<Predicate> searchByCriteria = new ArrayList<>();
        searchByCriteria.add(builder.equal(eventRoot.get("state"), EventState.PUBLISHED));
        if (text != null) {
            String query = "%" + text.toUpperCase() + "%";
            Predicate searchInAnnotation = builder.like(builder.upper(eventRoot.get("annotation")), query);
            Predicate searchInDescription = builder.like(builder.upper(eventRoot.get("description")), query);
            searchByCriteria.add(builder.or(searchInAnnotation, searchInDescription));
        }
        if (categories != null && !categories.isEmpty()) {
            searchByCriteria.add(eventRoot.get("category").in(categories));
        }
        if (paid != null) {
            searchByCriteria.add(builder.equal(eventRoot.get("paid"), paid));
        }
        if (rangeStart == null || rangeEnd == null) {
            searchByCriteria.add(builder.greaterThan(eventRoot.get("eventDate"), LocalDateTime.now()));
        } else {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IncorrectlyRequestException("Invalid date range request");
            }
            searchByCriteria.add(builder.between(eventRoot.get("eventDate"), rangeStart, rangeEnd));
        }
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            criteria = criteria.where(searchByCriteria.toArray(new Predicate[0]))
                    .orderBy(builder.asc(eventRoot.get("eventDate")));
        } else {
            criteria = criteria.where(searchByCriteria.toArray(new Predicate[0]));
        }
        TypedQuery<Event> tq = em.createQuery(criteria)
                .setFirstResult(from)
                .setMaxResults(size);
        return tq.getResultList();
    }

    public List<Event> searchAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteria = cb.createQuery(Event.class);
        Root<Event> eventRoot = criteria.from(Event.class);
        eventRoot.join("initiator");
        eventRoot.join("category");

        List<Predicate> searchByCriteria = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            searchByCriteria.add(eventRoot.get("initiator").in(users));
        }
        if (states != null && !states.isEmpty()) {
            List<EventState> eventStates = states.stream().map(s -> EventState.from(s).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            searchByCriteria.add(eventRoot.get("state").in(eventStates));
        }
        if (categories != null && !categories.isEmpty()) {
            searchByCriteria.add(eventRoot.get("category").in(categories));
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            searchByCriteria.add(cb.greaterThanOrEqualTo(eventRoot.get("created_date"), rangeStart));
            searchByCriteria.add(cb.greaterThanOrEqualTo(eventRoot.get("event_date"), rangeEnd));
        }

        criteria.select(eventRoot).where(cb.and(searchByCriteria.toArray(new Predicate[]{})));
        TypedQuery<Event> tq = em.createQuery(criteria).setFirstResult(from).setMaxResults(size);
        return tq.getResultList();
    }

}