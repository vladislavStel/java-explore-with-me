package ru.practicum.ewm.main_service.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.request.enums.RequestStatus;
import ru.practicum.ewm.main_service.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "requester_id")
    User requester;

    @Enumerated(EnumType.STRING)
    RequestStatus status;

    @Column(name = "created_date")
    @CreationTimestamp
    LocalDateTime created;

}