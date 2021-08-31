CREATE EXTENSION postgres_fdw;

CREATE SERVER prod_mygarden
  FOREIGN DATA WRAPPER postgres_fdw
  OPTIONS (host 'o24biz.ru', port '8080', dbname 'mygarden');

  CREATE USER MAPPING FOR CURRENT_USER
  SERVER prod_mygarden
  OPTIONS (user 'mygarden', password 'Mygarden1');

CREATE SCHEMA prod;

IMPORT FOREIGN SCHEMA public
  FROM SERVER prod_mygarden
  INTO prod;