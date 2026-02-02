INSERT INTO space_object (norad_catalog_number,
                           object_name,
                           object_type,
                           operational_status,
                           owner_operator,
                           launch_date,
                           mass_kg,
                           orbit_regime,
                           ephemeris_source,
                           last_ephemeris_update,
                           ephemeris_accuracy_m,
                           mean_altitude_km,
                           inclination_deg,
                           eccentricity,
                           created_at,
                           updated_at)
SELECT 10000 + gs                      AS norad_catalog_number,
       CASE
           WHEN gs % 10 = 0 THEN 'DEBRIS-' || gs
           ELSE 'SAT-' || gs
           END                         AS object_name,
       CASE
           WHEN gs % 10 = 0 THEN 'DEBRIS'
           ELSE 'SATELLITE'
           END                         AS object_type,
       CASE
           WHEN gs % 15 = 0 THEN 'INACTIVE'
           ELSE 'ACTIVE'
           END                         AS operational_status,
       CASE
           WHEN gs % 7 = 0 THEN 'ESA'
           WHEN gs % 7 = 1 THEN 'NASA'
           WHEN gs % 7 = 2 THEN 'JAXA'
           WHEN gs % 7 = 3 THEN 'CNSA'
           WHEN gs % 7 = 4 THEN 'ISRO'
           WHEN gs % 7 = 5 THEN 'SpaceX'
           ELSE 'OneWeb'
           END                         AS owner_operator,
       DATE '2000-01-01' + (gs % 8000) AS launch_date,
       100 + (gs % 1500)               AS mass_kg,
       CASE
           WHEN gs % 20 = 0 THEN 'GEO'
           WHEN gs % 8 = 0 THEN 'MEO'
           ELSE 'LEO'
           END                         AS orbit_regime,
       'TLE'                           AS ephemeris_source,
       now() - (gs % 72) * INTERVAL '1 hour' AS last_ephemeris_update, 50 + (gs % 200) AS ephemeris_accuracy_m, CASE
    WHEN gs % 20 = 0 THEN 35786
    WHEN gs % 8 = 0 THEN 20200
    ELSE 350 + (gs % 800)
END
AS mean_altitude_km,
    ROUND((gs % 98 + random())::numeric, 2)                      AS inclination_deg,
    ROUND((random() * 0.01)::numeric, 6)                         AS eccentricity,
    now(),
    now()
FROM generate_series(1, 1000) gs;
