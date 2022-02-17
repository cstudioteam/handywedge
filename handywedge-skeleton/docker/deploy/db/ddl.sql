CREATE TABLE skeleton
(
  id bigserial NOT NULL,
  value varchar(256),
  create_datetime timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
) WITHOUT OIDS;
