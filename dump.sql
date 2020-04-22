--
-- PostgreSQL database dump
--

-- Dumped from database version 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY public.spy_locations DROP CONSTRAINT spy_locations_users_id_fkey;
ALTER TABLE ONLY public.roles_to_users DROP CONSTRAINT roles_to_users_users_id_fkey;
ALTER TABLE ONLY public.roles_to_users DROP CONSTRAINT roles_to_users_roles_id_fkey;
ALTER TABLE ONLY public.messages_u_2_u DROP CONSTRAINT messages_u_2_u_users_id_to_fkey;
ALTER TABLE ONLY public.messages_u_2_u DROP CONSTRAINT messages_u_2_u_users_id_from_fkey;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.spy_locations DROP CONSTRAINT spy_locations_pkey;
ALTER TABLE ONLY public.roles_to_users DROP CONSTRAINT roles_to_users_pkey;
ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
ALTER TABLE ONLY public.messages_u_2_u DROP CONSTRAINT messages_u_2_u_pkey;
ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.spy_locations ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.roles_to_users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.roles ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.messages_u_2_u ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE public.users_id_seq;
DROP TABLE public.users;
DROP SEQUENCE public.user_ids;
DROP SEQUENCE public.spy_locations_id_seq;
DROP TABLE public.spy_locations;
DROP SEQUENCE public.roles_to_users_id_seq;
DROP TABLE public.roles_to_users;
DROP SEQUENCE public.roles_id_seq;
DROP TABLE public.roles;
DROP SEQUENCE public.messages_u_2_u_id_seq;
DROP TABLE public.messages_u_2_u;
DROP SEQUENCE public.hibernate_sequence;
DROP FUNCTION public.get_users_whith_roles();
DROP FUNCTION public.get_users_whith_role(_role character varying);
DROP FUNCTION public.get_user_and_role(_login character varying);
DROP FUNCTION public.get_spy_locations_and_login();
DROP FUNCTION public.get_spy_location_login(_location text);
DROP FUNCTION public.delete_user(_login text);
DROP FUNCTION public.delete_spy_location(_location text, login text);
DROP FUNCTION public.add_user(_login text, _password text, _role text);
DROP FUNCTION public.add_spy_location(_location text, _login text);
DROP EXTENSION plpgsql;
DROP SCHEMA users_manager;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: users_manager; Type: SCHEMA; Schema: -; Owner: karpach2000
--

CREATE SCHEMA users_manager;


ALTER SCHEMA users_manager OWNER TO karpach2000;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: add_spy_location(text, text); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.add_spy_location(_location text, _login text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    locationid INT;
    userid INT;
BEGIN
    FOR locationid IN
        SELECT id FROM spy_locations WHERE LOWER(location)= LOWER(_location)
        LOOP
            RETURN 'Location exists';
        END LOOP;
    FOR userid IN
        SELECT id FROM users WHERE login = _login
        LOOP
            INSERT INTO spy_locations (location, users_id) VALUES (_location, userid);
            RETURN 'OK';
        END LOOP;
    RETURN 'User does not exists';

END
$$;


ALTER FUNCTION public.add_spy_location(_location text, _login text) OWNER TO karpach2000;

--
-- Name: add_user(text, text, text); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.add_user(_login text, _password text, _role text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    userid INT;
    roleid INT;
BEGIN
    FOR userid IN
        SELECT id FROM users WHERE login = _login
    LOOP
        RETURN 'User exists';
    END LOOP;
    FOR roleid IN
        SELECT id FROM roles WHERE user_role = _role
    LOOP
        INSERT INTO users (login, password) VALUES (_login, _password);
        FOR userid IN
            SELECT id FROM users WHERE login = _login
            LOOP
                INSERT INTO roles_to_users(users_id, roles_id) VALUES (userid, roleid);
                RETURN 'OK';
            END LOOP;
    END LOOP;
    RETURN 'Role does not exists';

END
$$;


ALTER FUNCTION public.add_user(_login text, _password text, _role text) OWNER TO karpach2000;

--
-- Name: delete_spy_location(text, text); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.delete_spy_location(_location text, login text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
    DELETE FROM spy_locations WHERE location = _location;
    RETURN 'OK';

END
$$;


ALTER FUNCTION public.delete_spy_location(_location text, login text) OWNER TO karpach2000;

--
-- Name: delete_user(text); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.delete_user(_login text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    userid INT;
BEGIN
    FOR userid IN
        SELECT id FROM users WHERE login = _login
        LOOP
            DELETE FROM roles_to_users where users_id=userid;
    END LOOP;
    DELETE FROM users WHERE login = _login;
    RETURN 'OK';

END
$$;


ALTER FUNCTION public.delete_user(_login text) OWNER TO karpach2000;

--
-- Name: get_spy_location_login(text); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.get_spy_location_login(_location text) RETURNS TABLE(login character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
    FOR login IN
        SELECT users.login FROM users
               JOIN spy_locations ON users.id = spy_locations.users_id
               WHERE _location = spy_locations.location
        LOOP
            RETURN NEXT;
        END LOOP;
    RETURN;
END
$$;


ALTER FUNCTION public.get_spy_location_login(_location text) OWNER TO karpach2000;

--
-- Name: get_spy_locations_and_login(); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.get_spy_locations_and_login() RETURNS TABLE(location character varying, login character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
    FOR location, login IN
        SELECT spy_locations.location, users.login FROM spy_locations
              JOIN users ON users.id = spy_locations.users_id
        LOOP
            RETURN NEXT;
        END LOOP;
    RETURN;
END
$$;


ALTER FUNCTION public.get_spy_locations_and_login() OWNER TO karpach2000;

--
-- Name: get_user_and_role(character varying); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.get_user_and_role(_login character varying) RETURNS TABLE(user_login character varying, user_role character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    _users varchar(50);
    _roles varchar(50);
BEGIN
    FOR user_login, user_role IN
        SELECT users.login, roles.user_role FROM roles_to_users
                                                     JOIN users ON users.id = roles_to_users.users_id
                                                     JOIN roles ON roles.id = roles_to_users.roles_id
        WHERE users.login = _login
        LOOP
            RETURN NEXT;
        END LOOP;
    RETURN;
END
$$;


ALTER FUNCTION public.get_user_and_role(_login character varying) OWNER TO karpach2000;

--
-- Name: get_users_whith_role(character varying); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.get_users_whith_role(_role character varying) RETURNS SETOF text
    LANGUAGE plpgsql
    AS $$
DECLARE
	r varchar(50);
BEGIN
	FOR r IN
		SELECT users.login, users.password, roles.user_role FROM roles_to_users
		JOIN users ON users.id = roles_to_users.users_id
		JOIN roles ON roles.id = roles_to_users.roles_id
		WHERE roles.user_role = _role 
	LOOP 
		RETURN NEXT r; 
	END LOOP;
	RETURN;
END
$$;


ALTER FUNCTION public.get_users_whith_role(_role character varying) OWNER TO karpach2000;

--
-- Name: get_users_whith_roles(); Type: FUNCTION; Schema: public; Owner: karpach2000
--

CREATE FUNCTION public.get_users_whith_roles() RETURNS TABLE(user_login character varying, user_password character varying, user_role character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
 _users varchar(50);
 _password varchar(50);
 _roles varchar(50);
BEGIN
		FOR user_login, user_password, user_role IN
			SELECT users.login, users.password, roles.user_role FROM roles_to_users
			JOIN users ON users.id = roles_to_users.users_id
			JOIN roles ON roles.id = roles_to_users.roles_id
		LOOP
			RETURN NEXT;
		END LOOP;
		RETURN;
END
$$;


ALTER FUNCTION public.get_users_whith_roles() OWNER TO karpach2000;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO karpach2000;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: messages_u_2_u; Type: TABLE; Schema: public; Owner: karpach2000
--

CREATE TABLE public.messages_u_2_u (
    id integer NOT NULL,
    users_id_from integer,
    users_id_to integer,
    message text
);


ALTER TABLE public.messages_u_2_u OWNER TO karpach2000;

--
-- Name: messages_u_2_u_id_seq; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.messages_u_2_u_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_u_2_u_id_seq OWNER TO karpach2000;

--
-- Name: messages_u_2_u_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: karpach2000
--

ALTER SEQUENCE public.messages_u_2_u_id_seq OWNED BY public.messages_u_2_u.id;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: karpach2000
--

CREATE TABLE public.roles (
    id integer NOT NULL,
    user_role text,
    description text
);


ALTER TABLE public.roles OWNER TO karpach2000;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_id_seq OWNER TO karpach2000;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: karpach2000
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- Name: roles_to_users; Type: TABLE; Schema: public; Owner: karpach2000
--

CREATE TABLE public.roles_to_users (
    id integer NOT NULL,
    users_id integer,
    roles_id integer
);


ALTER TABLE public.roles_to_users OWNER TO karpach2000;

--
-- Name: roles_to_users_id_seq; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.roles_to_users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_to_users_id_seq OWNER TO karpach2000;

--
-- Name: roles_to_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: karpach2000
--

ALTER SEQUENCE public.roles_to_users_id_seq OWNED BY public.roles_to_users.id;


--
-- Name: spy_locations; Type: TABLE; Schema: public; Owner: karpach2000
--

CREATE TABLE public.spy_locations (
    id integer NOT NULL,
    location text,
    users_id integer
);


ALTER TABLE public.spy_locations OWNER TO karpach2000;

--
-- Name: spy_locations_id_seq; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.spy_locations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.spy_locations_id_seq OWNER TO karpach2000;

--
-- Name: spy_locations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: karpach2000
--

ALTER SEQUENCE public.spy_locations_id_seq OWNED BY public.spy_locations.id;


--
-- Name: user_ids; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.user_ids
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_ids OWNER TO karpach2000;

--
-- Name: users; Type: TABLE; Schema: public; Owner: karpach2000
--

CREATE TABLE public.users (
    id integer NOT NULL,
    login text,
    password text,
    active boolean DEFAULT true
);


ALTER TABLE public.users OWNER TO karpach2000;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: karpach2000
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO karpach2000;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: karpach2000
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: messages_u_2_u id; Type: DEFAULT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.messages_u_2_u ALTER COLUMN id SET DEFAULT nextval('public.messages_u_2_u_id_seq'::regclass);


--
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- Name: roles_to_users id; Type: DEFAULT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.roles_to_users ALTER COLUMN id SET DEFAULT nextval('public.roles_to_users_id_seq'::regclass);


--
-- Name: spy_locations id; Type: DEFAULT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.spy_locations ALTER COLUMN id SET DEFAULT nextval('public.spy_locations_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: messages_u_2_u; Type: TABLE DATA; Schema: public; Owner: karpach2000
--

COPY public.messages_u_2_u (id, users_id_from, users_id_to, message) FROM stdin;
1	1	1	Hay
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: karpach2000
--

COPY public.roles (id, user_role, description) FROM stdin;
1	USER	Обычный пользователь
2	ADMIN	Администратор
\.


--
-- Data for Name: roles_to_users; Type: TABLE DATA; Schema: public; Owner: karpach2000
--

COPY public.roles_to_users (id, users_id, roles_id) FROM stdin;
6	2	1
7	2	2
8	1	2
13	14	1
14	15	2
15	16	1
\.


--
-- Data for Name: spy_locations; Type: TABLE DATA; Schema: public; Owner: karpach2000
--

COPY public.spy_locations (id, location, users_id) FROM stdin;
2	десяточка	2
6	Коллайдер	14
8	Вертолет	2
11	мгту - рггу	14
13	Самолет	2
15	Жигули	2
20	жопад	16
22	МГУ им. Ломоносова	16
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: karpach2000
--

COPY public.users (id, login, password, active) FROM stdin;
1	afiskon	123456	t
2	karpach2000	256	t
7	Petr	1234	t
14	user	user	t
15	admin	admin	t
16	kola	kola	t
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);


--
-- Name: messages_u_2_u_id_seq; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.messages_u_2_u_id_seq', 1, true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.roles_id_seq', 2, true);


--
-- Name: roles_to_users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.roles_to_users_id_seq', 15, true);


--
-- Name: spy_locations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.spy_locations_id_seq', 23, true);


--
-- Name: user_ids; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.user_ids', 1, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: karpach2000
--

SELECT pg_catalog.setval('public.users_id_seq', 16, true);


--
-- Name: messages_u_2_u messages_u_2_u_pkey; Type: CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.messages_u_2_u
    ADD CONSTRAINT messages_u_2_u_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: roles_to_users roles_to_users_pkey; Type: CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.roles_to_users
    ADD CONSTRAINT roles_to_users_pkey PRIMARY KEY (id);


--
-- Name: spy_locations spy_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.spy_locations
    ADD CONSTRAINT spy_locations_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: messages_u_2_u messages_u_2_u_users_id_from_fkey; Type: FK CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.messages_u_2_u
    ADD CONSTRAINT messages_u_2_u_users_id_from_fkey FOREIGN KEY (users_id_from) REFERENCES public.users(id);


--
-- Name: messages_u_2_u messages_u_2_u_users_id_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.messages_u_2_u
    ADD CONSTRAINT messages_u_2_u_users_id_to_fkey FOREIGN KEY (users_id_to) REFERENCES public.roles(id);


--
-- Name: roles_to_users roles_to_users_roles_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.roles_to_users
    ADD CONSTRAINT roles_to_users_roles_id_fkey FOREIGN KEY (roles_id) REFERENCES public.roles(id);


--
-- Name: roles_to_users roles_to_users_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.roles_to_users
    ADD CONSTRAINT roles_to_users_users_id_fkey FOREIGN KEY (users_id) REFERENCES public.users(id);


--
-- Name: spy_locations spy_locations_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: karpach2000
--

ALTER TABLE ONLY public.spy_locations
    ADD CONSTRAINT spy_locations_users_id_fkey FOREIGN KEY (users_id) REFERENCES public.users(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA public TO PUBLIC;
GRANT USAGE ON SCHEMA public TO developer;


--
-- Name: FUNCTION add_spy_location(_location text, _login text); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.add_spy_location(_location text, _login text) TO developer;


--
-- Name: FUNCTION add_user(_login text, _password text, _role text); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.add_user(_login text, _password text, _role text) TO developer;


--
-- Name: FUNCTION delete_spy_location(_location text, login text); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.delete_spy_location(_location text, login text) TO developer;


--
-- Name: FUNCTION delete_user(_login text); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.delete_user(_login text) TO developer;


--
-- Name: FUNCTION get_spy_location_login(_location text); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.get_spy_location_login(_location text) TO developer;


--
-- Name: FUNCTION get_spy_locations_and_login(); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.get_spy_locations_and_login() TO developer;


--
-- Name: FUNCTION get_user_and_role(_login character varying); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.get_user_and_role(_login character varying) TO developer;


--
-- Name: FUNCTION get_users_whith_role(_role character varying); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.get_users_whith_role(_role character varying) TO postgres;
GRANT ALL ON FUNCTION public.get_users_whith_role(_role character varying) TO developer;


--
-- Name: FUNCTION get_users_whith_roles(); Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON FUNCTION public.get_users_whith_roles() TO developer;


--
-- Name: SEQUENCE hibernate_sequence; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.hibernate_sequence TO developer;


--
-- Name: TABLE messages_u_2_u; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON TABLE public.messages_u_2_u TO postgres;
GRANT ALL ON TABLE public.messages_u_2_u TO developer;


--
-- Name: SEQUENCE messages_u_2_u_id_seq; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.messages_u_2_u_id_seq TO postgres;
GRANT ALL ON SEQUENCE public.messages_u_2_u_id_seq TO developer;


--
-- Name: TABLE roles; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON TABLE public.roles TO postgres;
GRANT ALL ON TABLE public.roles TO developer;


--
-- Name: SEQUENCE roles_id_seq; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.roles_id_seq TO postgres;
GRANT ALL ON SEQUENCE public.roles_id_seq TO developer;


--
-- Name: TABLE roles_to_users; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON TABLE public.roles_to_users TO postgres;
GRANT ALL ON TABLE public.roles_to_users TO developer;


--
-- Name: SEQUENCE roles_to_users_id_seq; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.roles_to_users_id_seq TO postgres;
GRANT ALL ON SEQUENCE public.roles_to_users_id_seq TO developer;


--
-- Name: TABLE spy_locations; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON TABLE public.spy_locations TO developer;


--
-- Name: SEQUENCE spy_locations_id_seq; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.spy_locations_id_seq TO developer;


--
-- Name: SEQUENCE user_ids; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.user_ids TO postgres;
GRANT ALL ON SEQUENCE public.user_ids TO developer;


--
-- Name: TABLE users; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON TABLE public.users TO postgres;
GRANT ALL ON TABLE public.users TO developer;


--
-- Name: SEQUENCE users_id_seq; Type: ACL; Schema: public; Owner: karpach2000
--

GRANT ALL ON SEQUENCE public.users_id_seq TO postgres;
GRANT ALL ON SEQUENCE public.users_id_seq TO developer;


--
-- PostgreSQL database dump complete
--

