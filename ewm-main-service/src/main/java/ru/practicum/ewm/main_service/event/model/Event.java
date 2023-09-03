package ru.practicum.ewm.main_service.event.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.main_service.category.model.Category;
import ru.practicum.ewm.main_service.event.enums.EventState;
import ru.practicum.ewm.main_service.location.model.Location;
import ru.practicum.ewm.main_service.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    @Column(nullable = false)
    String annotation;

    @Column(nullable = false)
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "created_date")
    @CreationTimestamp
    LocalDateTime createdOn;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "location_id", nullable = false)
    Location location;

    boolean paid;

    @Column(name = "participant_limit")
    int participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    EventState state;

    @Transient
    long confirmedRequests;

    @Transient
    long views;

}