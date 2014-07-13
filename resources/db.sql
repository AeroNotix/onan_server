CREATE EXTENSION "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
       uuid     uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),

       username varchar(32) NOT NULL,
       email    varchar(60) NOT NULL,
       password varchar(60) NOT NULL,
       salt     varchar(29) NOT NULL
);

CREATE TABLE IF NOT EXISTS deployment (
       uuid     uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),

       namespace varchar(32) NOT NULL,
       name      varchar(32) NOT NULL,
       version   varchar(24) NOT NULL,
       payload   bytea NOT NULL

);

CREATE TABLE IF NOT EXISTS dependencies (
       project    uuid NOT NULL,
       dependency uuid NOT NULL,


       FOREIGN KEY (project) REFERENCES deployment (uuid),
       FOREIGN KEY (dependency) REFERENCES deployment (uuid),

       CONSTRAINT pk PRIMARY KEY (project, dependency)
);
