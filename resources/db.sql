CREATE TABLE IF NOT EXISTS users (
       uuid     uuid PRIMARY KEY,

       username varchar(32) NOT NULL,
       email    varchar(60) NOT NULL,
       password varchar(60) NOT NULL,
       salt     varchar(29) NOT NULL
);

CREATE TABLE IF NOT EXISTS deployment (
       uuid      uuid PRIMARY KEY,

       namespace varchar(32),
       name      varchar(32),
       version   varchar(24),
       payload   bytea,

       author    uuid,

       FOREIGN KEY (author) REFERENCES users (uuid)
);

CREATE TABLE IF NOT EXISTS dependencies (
       project    uuid,
       dependency uuid,

       FOREIGN KEY (project) REFERENCES deployment (uuid),
       FOREIGN KEY (dependency) REFERENCES deployment (uuid),

       CONSTRAINT pk PRIMARY KEY (project, dependency)
);
