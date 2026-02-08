-- create extension if not exists vector;

DROP TABLE IF EXISTS space_object;
drop table if exists conjunction_assessment;

DROP INDEX IF EXISTS idx_space_object_id;
DROP INDEX IF EXISTS idx_space_object_norad_catalog_number;

CREATE TABLE IF NOT EXISTS space_object
(
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    norad_catalog_number  INTEGER UNIQUE NOT NULL,
    object_name           VARCHAR(128)   NOT NULL,
    object_type           VARCHAR(32)    NOT NULL,
    -- SATELLITE | DEBRIS | ROCKET_BODY

    operational_status    VARCHAR(32)    NOT NULL,
    -- ACTIVE | INACTIVE | DECAYED | UNKNOWN

    owner_operator        VARCHAR(64),
    launch_date           DATE,
    mass_kg               DOUBLE PRECISION,

    orbit_regime          VARCHAR(16)    NOT NULL,
    -- LEO | MEO | GEO | HEO

    ephemeris_source      VARCHAR(32)    NOT NULL,
    -- TLE | OEM | OMM | INTERNAL

    last_ephemeris_update TIMESTAMP      NOT NULL,
    ephemeris_accuracy_m  DOUBLE PRECISION,

    mean_altitude_km      DOUBLE PRECISION,
    inclination_deg       DOUBLE PRECISION,
    eccentricity          DOUBLE PRECISION,

    created_at            TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP      NOT NULL DEFAULT now()
);

ALTER TABLE space_object OWNER TO postgres;
CREATE INDEX IF NOT EXISTS idx_space_object_id ON space_object (id);
CREATE INDEX IF NOT EXISTS idx_space_object_norad_catalog_number ON space_object (norad_catalog_number);


CREATE TABLE IF NOT EXISTS conjunction_assessment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    primary_object_id BIGINT NOT NULL,
    secondary_object_id BIGINT NOT NULL,
    status VARCHAR,
    collision_probability DOUBLE PRECISION,
    requested_at TIMESTAMP,
    completed_at TIMESTAMP,
    priority_level INT NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    time_step_minutes INT NOT NULL
);

ALTER TABLE conjunction_assessment OWNER TO postgres;
CREATE INDEX IF NOT EXISTS idx_conjunction_assessment_id ON conjunction_assessment (id);
CREATE INDEX IF NOT EXISTS idx_conjunction_assessment_primary_object_id_secondary_object_id ON conjunction_assessment (primary_object_id, secondary_object_id);

alter sequence conjunction_assessment_id_seq restart with 1;
alter sequence space_object_id_seq restart with 1;

-- LLM Integration
-- create table conjunction_report (
--     id bigint generated always as identity primary key,
--     object_a_id bigint not null,
--     object_b_id bigint not null,
--     report_text text not null,
--     embedding vector(1536),
--     created_at timestamp default now()
-- )
--
-- create index if not exists on conjunction_report using ivfflat (embedding vector_12_ops) with (lists = 100);
