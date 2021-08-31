ALTER TABLE item ADD COLUMN options jsonb;
ALTER TABLE user_plant
    ADD COLUMN width integer not null default 0,
    ADD COLUMN height integer not null default 0;

CREATE TABLE fertilizer_restriction (
  fertilizer_id1 BIGINT NOT NULL REFERENCES item (id) on delete cascade,
  fertilizer_id2 BIGINT NOT NULL REFERENCES item (id) on delete cascade,
  PRIMARY KEY (fertilizer_id1, fertilizer_id2)
);


drop view USER_VIEW;
 CREATE VIEW USER_VIEW AS
  SELECT
    u.id                              AS id,
    u.email                           AS email,
    cast(u.role AS CHARACTER VARYING) AS role,
    up.first_name                     AS first_name,
    up.last_name                      AS last_name,
    ci.name_ru                        AS city_name,
    co.name_ru                        AS country_name,
    up.newsletter                     AS newsletter
  FROM AUSER u
    INNER JOIN USER_PROFILE up ON up.user_id = u.id
    LEFT JOIN CITY ci ON ci.id = up.city_id
    LEFT JOIN NET_COUNTRY co ON co.id = ci.country_id;

