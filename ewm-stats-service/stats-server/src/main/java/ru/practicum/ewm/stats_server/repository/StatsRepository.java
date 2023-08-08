package ru.practicum.ewm.stats_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats_server.model.EndpointHit;
import ru.practicum.ewm.stats_server.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.ewm.stats_server.model.ViewStat(" +
            "stats.app, stats.uri, COUNT(stats.ip) AS hits) " +
            "FROM EndpointHit stats " +
            "WHERE stats.timestamp BETWEEN :start AND :end " +
            "GROUP BY stats.app, stats.uri " +
            "ORDER BY hits DESC")
    List<ViewStat> findStatsBetweenStartAndEnd(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.ewm.stats_server.model.ViewStat(" +
            "stats.app, stats.uri, COUNT(DISTINCT stats.ip) AS hits) " +
            "FROM EndpointHit stats " +
            "WHERE stats.timestamp BETWEEN :start AND :end " +
            "GROUP BY stats.app, stats.uri " +
            "ORDER BY hits DESC")
    List<ViewStat> findStatsBetweenStartAndEndAndUniqueIp(@Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.ewm.stats_server.model.ViewStat(" +
            "stats.app, stats.uri, COUNT(stats.ip) AS hits) " +
            "FROM EndpointHit stats " +
            "WHERE stats.timestamp BETWEEN :start AND :end " +
            "AND uri IN ( :uris ) " +
            "GROUP BY stats.app, stats.uri " +
            "ORDER BY hits DESC")
    List<ViewStat> findStatsBetweenStartAndEndByUri(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.ewm.stats_server.model.ViewStat(" +
            "stats.app, stats.uri, COUNT(DISTINCT stats.ip) AS hits) " +
            "FROM EndpointHit stats " +
            "WHERE stats.timestamp BETWEEN :start AND :end " +
            "AND uri IN ( :uris ) " +
            "GROUP BY stats.app, stats.uri " +
            "ORDER BY hits DESC")
    List<ViewStat> findStatsBetweenStartAndEndByUriAndUniqueIp(@Param("start") LocalDateTime start,
                                                               @Param("end") LocalDateTime end,
                                                               @Param("uris") List<String> uris);

}