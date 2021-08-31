-- ========================================================================
-- =======================  PATTERN AUTHOR  ===============================
-- ========================================================================
CREATE TABLE PATTERN_AUTHOR
(
  id   BIGINT                NOT NULL,
  name CHARACTER VARYING(50) NOT NULL,
  seq integer not null default 0,
  PRIMARY KEY (id)
);
INSERT INTO PATTERN_AUTHOR (id, name) VALUES (1, 'egardening.ru');

ALTER TABLE user_work
  ADD COLUMN author_id BIGINT REFERENCES PATTERN_AUTHOR (id);

-- ========================================================================
-- =======================  CATALOG <-> ITEM ==============================
-- ========================================================================


CREATE SEQUENCE CATALOG_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE CATALOG
(
  id        BIGINT                NOT NULL DEFAULT nextval('CATALOG_SEQ'),
  parent_id BIGINT REFERENCES CATALOG (id),
  owner_id  BIGINT REFERENCES AUSER (id),
  name      CHARACTER VARYING(50) NOT NULL,
  orig_id   BIGINT,
  PRIMARY KEY (id)
);

INSERT INTO CATALOG (id, name) VALUES
  (1, 'Удобрения'),
  (2, 'Растения'),
  (3, 'Инвентарь');

ALTER SEQUENCE CATALOG_SEQ RESTART WITH 4;

INSERT INTO CATALOG (parent_id, owner_id, name, orig_id)
  SELECT
    1,
    owner_id,
    name,
    id
  FROM fertilizer_catalog;

INSERT INTO CATALOG (parent_id, owner_id, name, orig_id)
  SELECT
    2,
    owner_id,
    name,
    id
  FROM species_catalog;

CREATE UNIQUE INDEX idx$unq$catalog$owner_parent_name
  ON catalog (parent_id, owner_id, lower(name))
  WHERE owner_id IS NOT NULL;
CREATE UNIQUE INDEX idx$unq$catalog$sysowner_parent_name
  ON catalog (parent_id, lower(name))
  WHERE owner_id IS NULL;

CREATE SEQUENCE ITEM_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE ITEM
(
  id          BIGINT                 NOT NULL DEFAULT nextval('ITEM_SEQ'),
  catalog_id  BIGINT REFERENCES CATALOG (id),
  owner_id    BIGINT REFERENCES AUSER (id),
  name        CHARACTER VARYING(100) NOT NULL,
  description TEXT,
  orig_id     BIGINT,
  PRIMARY KEY (id)
);

INSERT INTO ITEM (catalog_id, owner_id, name, description, orig_id)
  SELECT
    link.id AS cid,
    fc.owner_id,
    fc.name,
    NULL,
    fc.id
  FROM fertilizer fc
    INNER JOIN (
                 SELECT
                   id,
                   orig_id
                 FROM catalog
                 WHERE parent_id = 1
               ) link ON fc.fertilizer_catalog_id = link.orig_id;

INSERT INTO ITEM (catalog_id, owner_id, name, description, orig_id)
  SELECT
    link.id AS cid,
    fc.owner_id,
    fc.name,
    fc.description,
    fc.id
  FROM species fc
    INNER JOIN (
                 SELECT
                   id,
                   orig_id
                 FROM catalog
                 WHERE parent_id = 2
               ) link ON fc.species_catalog_id = link.orig_id;


CREATE UNIQUE INDEX idx$unq$item$catalog_owner_name
  ON item (catalog_id, owner_id, name)
  WHERE owner_id IS NOT NULL;
CREATE UNIQUE INDEX idx$unq$item$catalog_sysowner_name
  ON item (catalog_id, name)
  WHERE owner_id IS NULL;

DROP VIEW fertilizer_view;
DROP VIEW USER_WORK_VIEW;
DROP VIEW USER_TASK_VIEW;
DROP VIEW SPECIES_TASK_FERTILIZER_VIEW;

ALTER TABLE user_work
  DROP CONSTRAINT user_work_species_id_fkey;

UPDATE user_work
SET species_id = (
  SELECT i.id
  FROM item i
    INNER JOIN catalog c ON i.catalog_id = c.id
  WHERE i.orig_id = species_id AND c.parent_id = 2
);

ALTER TABLE user_work
  ADD FOREIGN KEY (species_id) REFERENCES item (id) ON DELETE SET NULL,
  ADD column species_name CHARACTER VARYING (100);

UPDATE user_work
SET species_name = (
  SELECT i.name
  FROM item i
  WHERE i.id = species_id
);

update user_work set author_id = 1 where user_id is null;

CREATE VIEW USER_WORK_VIEW AS
  SELECT
    uw.id                  AS user_work_id,
    uw.user_id             AS user_id,
    uw.author_id           AS author_id,
    pa.name                AS author,
    uw.species_id          AS species_id,
    uw.species_name        AS species_name,
    uw.pattern             AS pattern,
    uw.pattern_name        AS pattern_name,
    uw.pattern_description AS pattern_description
  FROM USER_WORK uw
    LEFT JOIN PATTERN_AUTHOR pa ON pa.id = uw.author_id;

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
    uw.species_name       AS species_name
  FROM USER_TASK ut
    INNER JOIN USER_WORK uw ON uw.id = ut.user_work_id;


ALTER TABLE species_task_fertilizer
  DROP CONSTRAINT species_task_fertilizer_species_id_fkey,
  DROP CONSTRAINT species_task_fertilizer_fertilizer_id_fkey;
DROP INDEX species_task_fertilizer_species_id_task_id_fertilizer_id_ow_idx;

UPDATE species_task_fertilizer
SET fertilizer_id = (
  SELECT i.id
  FROM item i
    INNER JOIN catalog c ON i.catalog_id = c.id
  WHERE i.orig_id = fertilizer_id AND c.parent_id = 1
),
  species_id      = (
    SELECT i.id
    FROM item i
      INNER JOIN catalog c ON i.catalog_id = c.id
    WHERE i.orig_id = species_id AND c.parent_id = 2
  );

ALTER TABLE species_task_fertilizer
  ADD FOREIGN KEY (species_id) REFERENCES item (id),
  ADD FOREIGN KEY (fertilizer_id) REFERENCES item (id);


CREATE VIEW SPECIES_TASK_FERTILIZER_VIEW AS
  SELECT
    stf.id              AS species_task_fertilizer_id,
    stf.species_id      AS species_id,
    stf.task_id         AS task_id,
    stf.fertilizer_id   AS fertilizer_id,
    stf.owner_id        AS owner_id,
    species.name        AS species_name,
    t.name              AS task_name,
    fertilizer.name     AS fertilizer_name,
    species.owner_id    AS species_owner_id,
    t.owner_id          AS task_owner_id,
    fertilizer.owner_id AS fertilizer_owner_id
  FROM species_task_fertilizer stf
    INNER JOIN item species ON species.id = stf.species_id
    INNER JOIN task t ON t.id = stf.task_id
    INNER JOIN item fertilizer ON fertilizer.id = stf.fertilizer_id;

DROP TABLE species;
DROP TABLE species_catalog;
DROP TABLE fertilizer;
DROP TABLE fertilizer_catalog;

ALTER TABLE catalog
  DROP COLUMN orig_id;
ALTER TABLE item
  DROP COLUMN orig_id;

CREATE VIEW ITEM_VIEW AS
  SELECT
    f.id        AS item_id,
    f.name      AS item_name,
    f.owner_id  AS item_owner_id,
    fc.id       AS catalog_id,
    fc.name     AS catalog_name,
    fc.owner_id AS catalog_owner_id
  FROM ITEM f
    INNER JOIN CATALOG fc ON fc.id = f.catalog_id;

CREATE UNIQUE INDEX idx$unq$species_task_fertilizer
  ON species_task_fertilizer (species_id, task_id, fertilizer_id, owner_id);

-- ========================================================================
-- =======================  ESHOP   =======================================
-- ========================================================================
CREATE SEQUENCE ORDER_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TYPE ORDER_STATUS AS ENUM ('CREATED', 'APPROVED', 'PAID', 'PENDING_CANCELLATION', 'CANCELLED', 'ERROR');

CREATE TABLE "ORDER"
(
  id           BIGINT       NOT NULL       DEFAULT nextval('ORDER_SEQ'),
  owner_id     BIGINT REFERENCES AUSER (id),
  status       ORDER_STATUS NOT NULL       DEFAULT 'CREATED',
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  comments     TEXT,
  PRIMARY KEY (id)
);

CREATE SEQUENCE ORDER_ITEM_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE ORDER_ITEM
(
  id              BIGINT           NOT NULL DEFAULT nextval('ORDER_ITEM_SEQ'),
  order_id        BIGINT REFERENCES "ORDER" (id),
  catalog_item_id BIGINT REFERENCES ITEM (id),
  price           DOUBLE PRECISION NOT NULL CHECK (price >= 0),
  amount          DOUBLE PRECISION NOT NULL CHECK (price > 0),
  PRIMARY KEY (id)
);

CREATE SEQUENCE ORDER_TRANSACTION_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE ORDER_TRANSACTION
(
  id          BIGINT       NOT NULL             DEFAULT nextval('ORDER_TRANSACTION'),
  order_id    BIGINT REFERENCES "ORDER" (id),
  date        TIMESTAMP WITHOUT TIME ZONE       DEFAULT now(),
  from_status ORDER_STATUS NOT NULL,
  to_status   ORDER_STATUS NOT NULL,
  info        TEXT         NOT NULL,
  PRIMARY KEY (id)
);

-- ========================================================================
-- =======================  USER PLANT  ===================================
-- ========================================================================

CREATE SEQUENCE USER_PLANT_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TABLE USER_PLANT
(
  id       BIGINT                 NOT NULL DEFAULT nextval('USER_PLANT_SEQ'),
  owner_id BIGINT REFERENCES AUSER (id),
  name     CHARACTER VARYING(100) NOT NULL,
  data     JSON,
  PRIMARY KEY (id)
);

-- ========================================================================
-- =======================  JSON UPDATE ===================================
-- ========================================================================
UPDATE user_task
SET fertilizers = replace(cast(fertilizers AS TEXT), 'fertilizerCatalogId', 'catalogId') :: JSON
WHERE cast(fertilizers AS TEXT) LIKE '%fertilizerCatalogId%';

-- ========================================================================
-- =======================  REFERRER STAT  ===================================
-- ========================================================================

CREATE SEQUENCE REFERER_STAT_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE UNLOGGED TABLE REFERER_STAT
(
  id          BIGINT                 NOT NULL DEFAULT nextval('REFERER_STAT_SEQ'),
  domain      CHARACTER VARYING(255),
  referer    CHARACTER VARYING(255),
  target     CHARACTER VARYING(255),
  access_time TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  PRIMARY KEY (id)
);

drop table feedback;

alter table species_task_fertilizer
  drop constraint species_task_fertilizer_fertilizer_id_fkey,
  add CONSTRAINT species_task_fertilizer_fertilizer_id_fkey FOREIGN KEY (fertilizer_id)
      REFERENCES item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;

  drop INDEX "auser$email$uniqeue";
  CREATE UNIQUE INDEX "auser$email$uniqeue" ON auser USING btree (lower(email));

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
    LEFT JOIN NET_CITY ci ON ci.id = up.city_id
    LEFT JOIN NET_COUNTRY co ON co.id = ci.country_id;

alter table NEWS add column newsletter_processed boolean not null default false;


CREATE SEQUENCE FILE_SEQ INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE TYPE ENTITY_TYPE AS ENUM ('CATALOG', 'FERTILIZER', 'ITEM', 'NEWS','SPECIES', 'TASK', 'USER');
CREATE TABLE FILE
(
  id          BIGINT NOT NULL DEFAULT nextval('FILE_SEQ'),
  owner_id    BIGINT REFERENCES AUSER (id),
  associated_entity_id BIGINT,
  associated_entity_type ENTITY_TYPE,
  mime_type   CHARACTER VARYING(50),
  file_name   CHARACTER VARYING(255),
  PRIMARY KEY (id)
);