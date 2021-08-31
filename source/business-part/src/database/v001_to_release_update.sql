DROP VIEW SPECIES_TASK_FERTILIZER_VIEW;

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


UPDATE auser
  SET password = md5(password)
  WHERE password IS NOT NULL;

ALTER TABLE auser
  ALTER COLUMN password TYPE CHARACTER(32),
  ADD COLUMN registration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  ADD COLUMN last_login_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();

ALTER TABLE USER_PROFILE
  ALTER COLUMN first_name drop not null,
  ALTER COLUMN last_name drop not null;

CREATE UNIQUE INDEX ON SPECIES_TASK_FERTILIZER (species_id, task_id, fertilizer_id, owner_id);