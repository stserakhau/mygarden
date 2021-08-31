-- mygarden / mygarder
-- CREATE ROLE mygarden LOGIN PASSWORD 'mygarden' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
--
-- CREATE DATABASE mygarden WITH OWNER = mygarden ENCODING = 'UTF8' CONNECTION LIMIT = 100;

CREATE OR REPLACE FUNCTION string_to_date_cast(VARCHAR)
  RETURNS DATE AS $$
SELECT
  to_date($1, 'YYYY-MM-DD')
$$ LANGUAGE SQL
IMMUTABLE
RETURNS NULL ON NULL INPUT;

-- ==================================================================================================
-- =========================        Statistics          =============================================
-- ==================================================================================================
CREATE SEQUENCE METHOD_STATISTIC_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE UNLOGGED TABLE METHOD_STATISTIC
(
  id             BIGINT NOT NULL             DEFAULT nextval('METHOD_STATISTIC_SEQ'),
  method         CHARACTER VARYING(150),
  execution_time INT,
  execution_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  PRIMARY KEY (id)
);

-- ==================================================================================================
-- =========================        Regions             =============================================
-- ==================================================================================================

CREATE TABLE NET_COUNTRY
(
  id      INTEGER NOT NULL,
  name_ru CHARACTER VARYING(100),
  name_en CHARACTER VARYING(100),
  code    CHARACTER VARYING(2),
  CONSTRAINT net_country_pkey PRIMARY KEY (id)
);

CREATE TABLE NET_CITY
(
  id          INTEGER NOT NULL,
  country_id  INTEGER REFERENCES NET_COUNTRY (id),
  name_ru     CHARACTER VARYING(100),
  name_en     CHARACTER VARYING(100),
  region      CHARACTER VARYING(2),
  postal_code CHARACTER VARYING(10),
  latitude    CHARACTER VARYING(10),
  longitude   CHARACTER VARYING(10),
  CONSTRAINT net_city_pkey PRIMARY KEY (id)
);

CREATE TABLE net_city_ip
(
  city_id  INTEGER,
  begin_ip BIGINT,
  end_ip   BIGINT
);
CREATE INDEX idx$net_city_ip$begin_ip$end_ip
ON net_city_ip USING BTREE (begin_ip, end_ip);

CREATE TABLE net_country_ip
(
  country_id INTEGER,
  begin_ip   BIGINT,
  end_ip     BIGINT
);
CREATE INDEX idx$net_country_ip$begin_ip$end_ip
ON net_country_ip USING BTREE (begin_ip, end_ip);

CREATE VIEW REGION_VIEW AS
  SELECT
    ci.id      AS city_id,
    ci.name_ru AS city_name,
    co.id      AS country_id,
    co.name_ru AS country_name
  FROM NET_CITY ci
    INNER JOIN NET_COUNTRY co ON co.id = ci.country_id;

-- ==================================================================================================
-- =============================        Weather         =============================================
-- ==================================================================================================

CREATE TABLE WEATHER
(
  city_id     INTEGER NOT NULL,
  net_city_id BIGINT REFERENCES NET_CITY (id),
  updated     DATE    NOT NULL DEFAULT '2000-01-01',
  day0        FLOAT,
  day1        FLOAT,
  day2        FLOAT,
  day3        FLOAT,
  day4        FLOAT,
  day5        FLOAT,
  day6        FLOAT,
  day7        FLOAT,
  CONSTRAINT weather_pkey PRIMARY KEY (city_id)
);

CREATE TABLE WEATHER_LOG
(
  city_id     INTEGER NOT NULL,
  net_city_id BIGINT,
  updated     DATE,
  temperature FLOAT,
  CONSTRAINT weather_log_pkey PRIMARY KEY (city_id, updated)
);

CREATE OR REPLACE FUNCTION log_weather_line()
  RETURNS TRIGGER AS $func$
BEGIN
  WITH
      day_temp AS (
        SELECT
          ROW_NUMBER()
          OVER (
            ORDER BY 1) AS daynum,
          temperature   AS temperature
        FROM (
               (
                 SELECT
                   temperature
                 FROM weather_log
                 WHERE city_id = NEW.city_id AND NEW.updated > now() - INTERVAL '30 day'
                 ORDER BY updated ASC
               )
               UNION ALL
               (
                 SELECT
                   t
                 FROM unnest(ARRAY [NEW.day1, NEW.day2, NEW.day3, NEW.day4, NEW.day5, NEW.day6, NEW.day7]) t
               )
             ) temp
    ),
      temperature AS (
        SELECT
          8 - ROW_NUMBER()
          OVER (
            ORDER BY dt.daynum DESC) AS day,
          dt.temperature,
          (
            SELECT
          avg(dta.temperature) AS av
            FROM day_temp dta
            WHERE dta.daynum BETWEEN dt.daynum - 30 AND dt.daynum
          )                          AS average
        FROM day_temp dt
        ORDER BY daynum DESC
        LIMIT 7
    ),
      ut AS (
        SELECT
          ut.id                                    AS id,
          NEW.updated + (INTERVAL '1 day') * t.day AS calculated_date
        FROM user_task ut
          INNER JOIN USER_PROFILE up
            ON ut.owner_id = up.user_id
               AND up.city_id = NEW.net_city_id
          INNER JOIN user_work uw ON uw.id = ut.user_work_id AND uw.pattern = FALSE
          INNER JOIN temperature t ON t.day = (
            SELECT
              day
            FROM temperature
            WHERE temperature >= ut.avg_temperature
            ORDER BY t ASC
            LIMIT 1
          )
        WHERE
          ut.notification_ready = FALSE
          AND ut.planing_type = 'planByWeather'
          AND NEW.updated + (INTERVAL '1 day') * t.day BETWEEN ut.start_date AND ut.end_date
    )
  UPDATE user_task
  SET
    calculated_date    = ut.calculated_date,
    notification_ready = TRUE
  FROM ut
  WHERE ut.id = user_task.id;

  INSERT INTO WEATHER_LOG (city_id, net_city_id, updated, temperature)
  VALUES (NEW.city_id, NEW.net_city_id, NEW.updated, NEW.day0);

  RETURN NEW;
END; $func$ LANGUAGE plpgsql;

CREATE TRIGGER WEATHER_LOG_TRIGGER
AFTER UPDATE ON WEATHER
FOR EACH ROW
EXECUTE PROCEDURE log_weather_line();

-- ==================================================================================================
-- =========================        CONTACT US          =============================================
-- ==================================================================================================
CREATE TABLE FEEDBACK
(
  id      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  name    CHARACTER VARYING(50),
  email   CHARACTER VARYING(50),
  content TEXT,
  PRIMARY KEY (id)
);

--  ==================================================================================================
-- =========================            EMAIL           =============================================
-- ==================================================================================================
CREATE SEQUENCE EMAIL_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE EMAIL
(
  id      BIGINT NOT NULL DEFAULT nextval('EMAIL_SEQ'),
  content BYTEA  NOT NULL,
  send    BOOLEAN,
  PRIMARY KEY (id)
);
CREATE INDEX idx$email$send
ON EMAIL (send);
--  ==================================================================================================
-- =========================        Users               =============================================
-- ==================================================================================================
CREATE SEQUENCE USER_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TYPE USER_ROLE AS ENUM ('ROLE_USER');

CREATE TABLE AUSER
(
  id                     BIGINT                      NOT NULL DEFAULT nextval('USER_SEQ'),
  email                  CHARACTER VARYING(50)       NOT NULL UNIQUE,
  password               CHARACTER(32),
  role                   USER_ROLE                   NOT NULL,
  registration_code      CHARACTER(40),
  registration_confirmed BOOLEAN                     NOT NULL DEFAULT FALSE,
  registration_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  last_login_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX auser$email$uniqeue
ON AUSER (email);
CREATE UNIQUE INDEX auser$registration_code$uniqeue
ON AUSER (email)
  WHERE registration_code IS NOT NULL;

CREATE TABLE USER_PROFILE
(
  user_id                                        BIGINT  NOT NULL,
  first_name                                     CHARACTER VARYING(100),
  last_name                                      CHARACTER VARYING(100),
  occupation                                     CHARACTER VARYING(100),
  city_id                                        INTEGER REFERENCES net_city (id),
  user_agreement_accepted                        BOOLEAN NOT NULL,
  allow_advertisement                            BOOLEAN NOT NULL,
  job_notification_days_before                   INTEGER,
  temperature_low_level_notification             INTEGER,
  temperature_low_level_notification_days_before INTEGER,
  newsletter                                     BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (user_id)
);

CREATE INDEX "idx$user_profile$city_id"
ON public.user_profile USING BTREE (city_id);

CREATE VIEW USER_VIEW AS
  SELECT
    u.id                              AS id,
    u.email                           AS email,
    cast(u.role AS CHARACTER VARYING) AS role,
    up.first_name                     AS first_name,
    up.last_name                      AS last_name,
    ci.name_ru                        AS city_name,
    co.name_ru                        AS country_name
  FROM AUSER u
    INNER JOIN USER_PROFILE up ON up.user_id = u.id
    LEFT JOIN NET_CITY ci ON ci.id = up.city_id
    LEFT JOIN NET_COUNTRY co ON co.id = ci.country_id;

-- =====================================================
-- =================   Fertilizer   ====================
-- =====================================================
CREATE SEQUENCE FERTILIZER_CATALOG_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE FERTILIZER_CATALOG
(
  id       BIGINT                NOT NULL DEFAULT nextval('FERTILIZER_CATALOG_SEQ'),
  owner_id BIGINT REFERENCES AUSER (id),
  name     CHARACTER VARYING(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_unq$fertilizer_catalog$owner_id$name
ON FERTILIZER_CATALOG (owner_id, upper(name));

CREATE SEQUENCE FERTILIZER_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE FERTILIZER
(
  id                    BIGINT                NOT NULL DEFAULT nextval('FERTILIZER_SEQ'),
  fertilizer_catalog_id BIGINT                NOT NULL REFERENCES FERTILIZER_CATALOG (id),
  owner_id              BIGINT REFERENCES AUSER (id),
  name                  CHARACTER VARYING(80) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_unq$fertilizer$owner_id$name
ON FERTILIZER (owner_id, upper(name));

CREATE VIEW FERTILIZER_VIEW AS
  SELECT
    f.id        AS fertilizer_id,
    f.name      AS fertilizer_name,
    f.owner_id  AS fertilizer_owner_id,
    fc.id       AS fertilizer_catalog_id,
    fc.name     AS fertilizer_catalog_name,
    fc.owner_id AS fertilizer_catalog_owner_id
  FROM FERTILIZER f
    INNER JOIN FERTILIZER_CATALOG fc ON fc.id = f.fertilizer_catalog_id;

-- =====================================================
-- =================     SPECIES     ====================
-- =====================================================
CREATE SEQUENCE SPECIES_CATALOG_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE SPECIES_CATALOG
(
  id       BIGINT                NOT NULL DEFAULT nextval('SPECIES_CATALOG_SEQ'),
  owner_id BIGINT REFERENCES AUSER (id),
  name     CHARACTER VARYING(50) NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX unq_idx2$species_catalog$name
ON SPECIES_CATALOG (upper(name))
  WHERE owner_id IS NULL;
CREATE UNIQUE INDEX unq_idx1$species_catalog$name$owner_id
ON SPECIES_CATALOG (owner_id, upper(name))
  WHERE owner_id IS NOT NULL;


CREATE SEQUENCE SPECIES_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE SPECIES
(
  id                 BIGINT                NOT NULL DEFAULT nextval('SPECIES_SEQ'),
  species_catalog_id BIGINT                NOT NULL REFERENCES SPECIES_CATALOG (id) ON DELETE CASCADE,
  owner_id           BIGINT REFERENCES AUSER (id),
  name               CHARACTER VARYING(50) NOT NULL,
  description        TEXT,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX unq_idx1$species$name$owner_id
ON SPECIES (owner_id, upper(name))
  WHERE owner_id IS NOT NULL;

-- =====================================================
-- ==================     TASK     ====================
-- =====================================================
CREATE SEQUENCE TASK_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE TASK
(
  id       BIGINT                NOT NULL DEFAULT nextval('TASK_SEQ'),
  name     CHARACTER VARYING(50) NOT NULL,
  owner_id BIGINT REFERENCES AUSER (id),
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX unq_idx2$task$name
ON TASK (upper(name))
  WHERE owner_id IS NULL;
CREATE UNIQUE INDEX unq_idx1$task$name$owner_id
ON TASK (owner_id, upper(name))
  WHERE owner_id IS NOT NULL;

-- =====================================================
-- ============ SPECIES_TASK_FERTILIZER     ============
-- =====================================================
CREATE SEQUENCE SPECIES_TASK_FERTILIZER_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE SPECIES_TASK_FERTILIZER
(
  id            BIGINT NOT NULL DEFAULT nextval('SPECIES_TASK_FERTILIZER_SEQ'),
  species_id    BIGINT NOT NULL REFERENCES SPECIES (id) ON DELETE CASCADE,
  task_id       BIGINT NOT NULL REFERENCES TASK (id),
  fertilizer_id BIGINT NOT NULL REFERENCES FERTILIZER (id) ON DELETE CASCADE,
  owner_id      BIGINT REFERENCES AUSER (id),
  PRIMARY KEY (id)
);

CREATE VIEW SPECIES_TASK_FERTILIZER_VIEW AS
  SELECT
    stf.id            AS species_task_fertilizer_id,
    stf.species_id    AS species_id,
    stf.task_id       AS task_id,
    stf.fertilizer_id AS fertilizer_id,
    stf.owner_id      AS owner_id,
    s.name            AS species_name,
    t.name            AS task_name,
    f.name            AS fertilizer_name,
    s.owner_id        AS species_owner_id,
    t.owner_id        AS task_owner_id,
    f.owner_id        AS fertilizer_owner_id
  FROM species_task_fertilizer stf
    INNER JOIN species s ON s.id = stf.species_id
    INNER JOIN task t ON t.id = stf.task_id
    INNER JOIN fertilizer f ON f.id = stf.fertilizer_id;

-- =====================================================
-- ===================  USER_WORK  =====================
-- =====================================================
CREATE SEQUENCE USER_WORK_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE USER_WORK
(
  id                  BIGINT                NOT NULL DEFAULT nextval('USER_WORK_SEQ'),
  user_id             BIGINT REFERENCES AUSER (id),
  species_id          BIGINT REFERENCES SPECIES (id) ON DELETE SET NULL,
  pattern             BOOLEAN               NOT NULL DEFAULT FALSE,
  pattern_name        CHARACTER VARYING(50) NULL,
  pattern_description TEXT,
  PRIMARY KEY (id)
);

-- unique pattrn name in scope of the user and species
CREATE UNIQUE INDEX idx_unq$user_work$pattern$name
ON user_work (user_id, species_id, upper(pattern_name))
  WHERE pattern = TRUE;

CREATE VIEW USER_WORK_VIEW AS
  SELECT
    uw.id                  AS user_work_id,
    uw.user_id             AS user_id,
    uw.species_id          AS species_id,
    uw.pattern             AS pattern,
    uw.pattern_name        AS pattern_name,
    uw.pattern_description AS pattern_description,
    s.name                 AS species_name
  FROM USER_WORK uw
    INNER JOIN SPECIES s ON s.id = uw.species_id;

-- =====================================================
-- ===================  USER_TASK  =====================
-- =====================================================
CREATE SEQUENCE USER_TASK_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TYPE USER_TASK_STATUS AS ENUM ('CREATED', 'COMPLETED', 'CANCELLED');
CREATE TYPE PLANING_TYPE AS ENUM ('planByDate', 'planByWeather', 'planByAnotherTask');

CREATE TABLE USER_TASK
(
  id                      BIGINT                NOT NULL DEFAULT nextval('USER_TASK_SEQ'),
  user_work_id            BIGINT                NOT NULL REFERENCES USER_WORK (id) ON DELETE CASCADE,
  status                  USER_TASK_STATUS      NOT NULL DEFAULT 'CREATED',
  task_id                 BIGINT REFERENCES TASK (id) ON DELETE SET NULL,
  task_name               CHARACTER VARYING(50) NOT NULL,
  owner_id                BIGINT REFERENCES AUSER (id),

  planing_type            PLANING_TYPE          NOT NULL,

  avg_temperature         INTEGER,

  depends_from_task_id    BIGINT REFERENCES USER_TASK (id),
  cnt_days_after_prev_evt INTEGER,

  start_date              DATE,
  calculated_date         DATE,
  end_date                DATE,

  comment                 TEXT,
  fertilizers             JSON,
  notification_ready      BOOLEAN               NOT NULL DEFAULT TRUE,
  notification_sent       BOOLEAN,
  PRIMARY KEY (id)
);

CREATE INDEX "idx$user_task$calculation_process"
ON user_task USING BTREE (owner_id, notification_sent, planing_type);

CREATE VIEW USER_TASK_VIEW AS
  SELECT
    ut.id                 AS user_task_id,
    ut.status             AS user_task_status,
    ut.task_name          AS task_name,
    ut.start_date         AS start_date,
    ut.end_date           AS end_date,
    ut.calculated_date    AS calculated_date,
    ut.user_work_id       AS user_work_id,
    ut.owner_id           AS owner_id,
    ut.fertilizers        AS fertilizers,
    ut.comment            AS task_comment,
    ut.notification_ready AS notification_ready,
    ut.notification_sent  AS notification_sent,
    uw.species_id         AS species_id,
    uw.pattern            AS pattern,
    uw.pattern_name       AS pattern_name,
    s.name                AS species_name
  FROM USER_TASK ut
    INNER JOIN USER_WORK uw ON uw.id = ut.user_work_id
    LEFT JOIN SPECIES s ON s.id = uw.species_id;

-- =====================================================
-- =====================  NEWS  ========================
-- =====================================================
CREATE TYPE NEWS_STATE AS ENUM ('READY_TO_DEPLOY', 'DEPLOYED', 'READY_TO_UNDEPLOY', 'UNDEPLOYED', 'READY_TO_DELETE');
CREATE TYPE NEWS_GROUP AS ENUM ('EGARDENING', 'PARTNER', 'USER');

CREATE SEQUENCE NEWS_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE NEWS
(
  id              BIGINT     NOT NULL DEFAULT nextval('NEWS_SEQ'),
  news_group      NEWS_GROUP NOT NULL,
  user_id         BIGINT REFERENCES AUSER (id),
  state           NEWS_STATE NOT NULL,
  publishing_date DATE,
  zip_archive     BYTEA      NOT NULL,
  title           CHARACTER VARYING(200),
  PRIMARY KEY (id)
);
