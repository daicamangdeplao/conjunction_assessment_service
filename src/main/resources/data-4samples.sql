insert into public.space_object (norad_catalog_number, object_name, object_type, operational_status, owner_operator,
                                 launch_date, mass_kg, orbit_regime, ephemeris_source, last_ephemeris_update,
                                 ephemeris_accuracy_m, mean_altitude_km, inclination_deg, eccentricity, created_at,
                                 updated_at)
values (10001, 'SAT-1', 'SATELLITE', 'ACTIVE', 'NASA', '2000-01-02', 101, 'LEO', 'TLE', '2026-02-01 12:49:55.634063',
        51, 351, 1.25, 0.009658, '2026-02-01 13:49:55.634063', '2026-02-01 13:49:55.634063'),
       (10002, 'SAT-2', 'SATELLITE', 'ACTIVE', 'JAXA', '2000-01-03', 102, 'LEO', 'TLE', '2026-02-01 11:49:55.634063',
        52, 352, 2.65, 0.003203, '2026-02-01 13:49:55.634063', '2026-02-01 13:49:55.634063');

insert into public.space_object (norad_catalog_number, object_name, object_type, operational_status, owner_operator,
                                 launch_date, mass_kg, orbit_regime, ephemeris_source, last_ephemeris_update,
                                 ephemeris_accuracy_m, mean_altitude_km, inclination_deg, eccentricity, created_at,
                                 updated_at)
values (10015, 'SAT-15', 'SATELLITE', 'INACTIVE', 'NASA', '2000-01-16', 115, 'LEO', 'TLE',
        '2026-01-31 22:49:55.634063', 65, 365, 15.67, 0.000642, '2026-02-01 13:49:55.634063',
        '2026-02-01 13:49:55.634063'),
       (10030, 'DEBRIS-30', 'DEBRIS', 'INACTIVE', 'JAXA', '2000-01-31', 130, 'LEO', 'TLE',
        '2026-01-31 07:49:55.634063', 80, 380, 30.12, 0.000709, '2026-02-01 13:49:55.634063',
        '2026-02-01 13:49:55.634063');
