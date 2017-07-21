BEGIN TRANSACTION;
-- Table: public."User"

-- DROP TABLE public."User";

CREATE TABLE public."User"
(
    id integer NOT NULL DEFAULT nextval('userid'::regclass),
    "username " text COLLATE pg_catalog."default",
    password text COLLATE pg_catalog."default",
    CONSTRAINT id PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public."User"
    OWNER to postgres;
COMMIT;

-- Table: public."Post"

-- DROP TABLE public."Post";
BEGIN TRANSACTION;
CREATE TABLE public."Post"
(
    id integer NOT NULL DEFAULT nextval('postid'::regclass),
    description text COLLATE pg_catalog."default",
    user_id integer,
    CONSTRAINT post_id PRIMARY KEY (id),
    CONSTRAINT fk_user_post FOREIGN KEY (user_id)
        REFERENCES public."User" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
COMMIT;
BEGIN TRANSACTION;
