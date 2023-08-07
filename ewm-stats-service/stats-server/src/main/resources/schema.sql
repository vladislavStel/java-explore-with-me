DROP TABLE IF EXISTS stats;

CREATE TABLE IF NOT EXISTS stats (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app CHARACTER VARYING NOT NULL,
    uri CHARACTER VARYING NOT NULL,
    ip CHARACTER VARYING NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_stat_id PRIMARY KEY (id)
);