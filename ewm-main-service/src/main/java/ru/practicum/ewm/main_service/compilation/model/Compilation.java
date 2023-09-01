package ru.practicum.ewm.main_service.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.event.model.Event;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilation")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    boolean pinned;

    @Column(nullable = false)
    String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
    Set<Event> events = new HashSet<>();

}