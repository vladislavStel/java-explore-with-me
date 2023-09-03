package ru.practicum.ewm.main_service.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main_service.event.dto.SearchCriteriaObject;
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

    public List<Event> searchEventsByQueryAndSort(SearchCriteriaObject searchCriteriaObject) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteria = builder.createQuery(Event.class);
        Root<Event> eventRoot = criteria.from(Event.class);
        eventRoot.join("initiator");
        eventRoot.join("category");

        List<Predicate> searchByCriteria = new ArrayList<>();
        searchByCriteria.add(builder.equal(eventRoot.get("state"), EventState.PUBLISHED));
        if (searchCriteriaObject.getText() != null) {
            String query = "%" + searchCriteriaObject.getText().toUpperCase() + "%";
            Predicate searchInAnnotation = builder.like(builder.upper(eventRoot.get("annotation")), query);
            Predicate searchInDescription = builder.like(builder.upper(eventRoot.get("description")), query);
            searchByCriteria.add(builder.or(searchInAnnotation, searchInDescription));
        }
        if (searchCriteriaObject.getCategories() != null && !searchCriteriaObject.getCategories().isEmpty()) {
            searchByCriteria.add(eventRoot.get("category").in(searchCriteriaObject.getCategories()));
        }
        if (searchCriteriaObject.getPaid() != null) {
            searchByCriteria.add(builder.equal(eventRoot.get("paid"), searchCriteriaObject.getPaid()));
        }
        if (searchCriteriaObject.getRangeStart() == null || searchCriteriaObject.getRangeEnd() == null) {
            searchByCriteria.add(builder.greaterThan(eventRoot.get("eventDate"), LocalDateTime.now()));
        } else {
            if (searchCriteriaObject.getRangeStart().isAfter(searchCriteriaObject.getRangeEnd())) {
                throw new IncorrectlyRequestException("Invalid date range request");
            }
            searchByCriteria.add(builder.between(eventRoot.get("eventDate"), searchCriteriaObject.getRangeStart(),
                    searchCriteriaObject.getRangeEnd()));
        }
        if (searchCriteriaObject.getSort() != null && searchCriteriaObject.getSort().equals(EventSort.EVENT_DATE)) {
            criteria = criteria.where(searchByCriteria.toArray(new Predicate[0]))
                    .orderBy(builder.asc(eventRoot.get("eventDate")));
        } else {
            criteria = criteria.where(searchByCriteria.toArray(new Predicate[0]));
        }
        TypedQuery<Event> tq = em.createQuery(criteria)
                .setFirstResult(searchCriteriaObject.getFrom())
                .setMaxResults(searchCriteriaObject.getSize());
        return tq.getResultList();
    }

    public List<Event> searchAllEvents(SearchCriteriaObject searchCriteriaObject) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteria = cb.createQuery(Event.class);
        Root<Event> eventRoot = criteria.from(Event.class);
        eventRoot.join("initiator");
        eventRoot.join("category");

        List<Predicate> searchByCriteria = new ArrayList<>();
        if (searchCriteriaObject.getUsers() != null && !searchCriteriaObject.getUsers().isEmpty()) {
            searchByCriteria.add(eventRoot.get("initiator").in(searchCriteriaObject.getUsers()));
        }
        if (searchCriteriaObject.getStates() != null && !searchCriteriaObject.getStates().isEmpty()) {
            List<EventState> eventStates = searchCriteriaObject.getStates().stream()
                    .map(s -> EventState.from(s).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            searchByCriteria.add(eventRoot.get("state").in(eventStates));
        }
        if (searchCriteriaObject.getCategories() != null && !searchCriteriaObject.getCategories().isEmpty()) {
            searchByCriteria.add(eventRoot.get("category").in(searchCriteriaObject.getCategories()));
        }
        if (searchCriteriaObject.getRangeStart() != null && searchCriteriaObject.getRangeEnd() != null
                && searchCriteriaObject.getRangeStart().isAfter(searchCriteriaObject.getRangeEnd())) {
            searchByCriteria.add(cb.greaterThanOrEqualTo(eventRoot.get("created_date"),
                    searchCriteriaObject.getRangeStart()));
            searchByCriteria.add(cb.greaterThanOrEqualTo(eventRoot.get("event_date"),
                    searchCriteriaObject.getRangeEnd()));
        }

        criteria.select(eventRoot).where(cb.and(searchByCriteria.toArray(new Predicate[]{})));
        TypedQuery<Event> tq = em.createQuery(criteria)
                .setFirstResult(searchCriteriaObject.getFrom())
                .setMaxResults(searchCriteriaObject.getSize());
        return tq.getResultList();
    }

}