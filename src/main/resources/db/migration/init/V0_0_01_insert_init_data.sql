BEGIN TRANSACTION;
INSERT INTO public."User"(username , password) VALUES ('Vania','Vania');
INSERT INTO "public".Post(description, user_id) VALUES ('desc', 0);
COMMIT;