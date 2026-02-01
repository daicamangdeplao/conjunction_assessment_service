DROP TABLE IF EXISTS space_object;

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
