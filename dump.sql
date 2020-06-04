--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2 (Ubuntu 12.2-4)
-- Dumped by pg_dump version 12.2 (Ubuntu 12.2-4)

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

ALTER TABLE ONLY public.thirty_years_events DROP CONSTRAINT thirty_years_events_users_id_fkey;
ALTER TABLE ONLY public.spy_locations DROP CONSTRAINT spy_locations_users_id_fkey;
ALTER TABLE ONLY public.roles_to_users DROP CONSTRAINT roles_to_users_users_id_fkey;
ALTER TABLE ONLY public.roles_to_users DROP CONSTRAINT roles_to_users_roles_id_fkey;
ALTER TABLE ONLY public.messages_u_2_u DROP CONSTRAINT messages_u_2_u_users_id_to_fkey;
ALTER TABLE ONLY public.messages_u_2_u DROP CONSTRAINT messages_u_2_u_users_id_from_fkey;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.thirty_years_events DROP CONSTRAINT thirty_years_events_pkey;
ALTER TABLE ONLY public.spy_locations DROP CONSTRAINT spy_locations_pkey;
ALTER TABLE ONLY public.roles_to_users DROP CONSTRAINT roles_to_users_pkey;
ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
ALTER TABLE ONLY public.messages_u_2_u DROP CONSTRAINT messages_u_2_u_pkey;
ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.thirty_years_events ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.spy_locations ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.roles_to_users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.roles ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.messages_u_2_u ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE public.users_id_seq;
DROP TABLE public.users;
DROP SEQUENCE public.thirty_years_events_id_seq;
DROP TABLE public.thirty_years_events;
DROP SEQUENCE public.spy_locations_id_seq;
DROP TABLE public.spy_locations;
DROP SEQUENCE public.roles_to_users_id_seq;
DROP TABLE public.roles_to_users;
DROP SEQUENCE public.roles_id_seq;
DROP TABLE public.roles;
DROP SEQUENCE public.messages_u_2_u_id_seq;
DROP TABLE public.messages_u_2_u;
DROP FUNCTION public.get_users_whith_roles();
DROP FUNCTION public.get_users_whith_role(_role character varying);
DROP FUNCTION public.get_user_and_role(_login character varying);
DROP FUNCTION public.get_thirty_years_events_login(_event text);
DROP FUNCTION public.get_thirty_years_event_and_login();
DROP FUNCTION public.get_spy_locations_and_login();
DROP FUNCTION public.get_spy_location_login(_location text);
DROP FUNCTION public.delete_user(_login text);
DROP FUNCTION public.delete_spy_location(_location text, login text);
DROP FUNCTION public.add_user(_login text, _password text, _role text);
DROP FUNCTION public.add_thirty_years_event(_event text, _login text);
DROP FUNCTION public.add_spy_location(_location text, _login text);
--
-- Name: add_spy_location(text, text); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.add_spy_location(_location text, _login text) OWNER TO developer;

--
-- Name: add_thirty_years_event(text, text); Type: FUNCTION; Schema: public; Owner: developer
--

CREATE FUNCTION public.add_thirty_years_event(_event text, _login text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    event INT;
    userid INT;
BEGIN
    FOR event IN
        SELECT id FROM thirty_years_events WHERE LOWER(thirty_years_events.event)= LOWER(_event)
        LOOP
            RETURN 'Event exists';
        END LOOP;
    FOR userid IN
        SELECT id FROM users WHERE login = _login
        LOOP
            INSERT INTO thirty_years_events (event, users_id, available) VALUES (_event, userid, TRUE);
            RETURN 'OK';
        END LOOP;
    RETURN 'User does not exists';

END
$$;


ALTER FUNCTION public.add_thirty_years_event(_event text, _login text) OWNER TO developer;

--
-- Name: add_user(text, text, text); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.add_user(_login text, _password text, _role text) OWNER TO developer;

--
-- Name: delete_spy_location(text, text); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.delete_spy_location(_location text, login text) OWNER TO developer;

--
-- Name: delete_user(text); Type: FUNCTION; Schema: public; Owner: developer
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
            DELETE FROM spy_locations where users_id=userid;
            DELETE FROM messages_u_2_u where users_id_from=userid or users_id_to=userid;

        END LOOP;
    DELETE FROM users WHERE login = _login;
    RETURN 'OK';

END
$$;


ALTER FUNCTION public.delete_user(_login text) OWNER TO developer;

--
-- Name: get_spy_location_login(text); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.get_spy_location_login(_location text) OWNER TO developer;

--
-- Name: get_spy_locations_and_login(); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.get_spy_locations_and_login() OWNER TO developer;

--
-- Name: get_thirty_years_event_and_login(); Type: FUNCTION; Schema: public; Owner: developer
--

CREATE FUNCTION public.get_thirty_years_event_and_login() RETURNS TABLE(event character varying, login character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
    FOR event, login IN
        SELECT thirty_years_events.event, users.login FROM thirty_years_events
                                                            JOIN users ON users.id = thirty_years_events.users_id
        LOOP
            RETURN NEXT;
        END LOOP;
    RETURN;
END
$$;


ALTER FUNCTION public.get_thirty_years_event_and_login() OWNER TO developer;

--
-- Name: get_thirty_years_events_login(text); Type: FUNCTION; Schema: public; Owner: developer
--

CREATE FUNCTION public.get_thirty_years_events_login(_event text) RETURNS TABLE(login character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
    FOR login IN
        SELECT users.login FROM users
                                    JOIN thirty_years_events ON users.id = thirty_years_events.users_id
        WHERE _event = thirty_years_events.event
        LOOP
            RETURN NEXT;
        END LOOP;
    RETURN;
END
$$;


ALTER FUNCTION public.get_thirty_years_events_login(_event text) OWNER TO developer;

--
-- Name: get_user_and_role(character varying); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.get_user_and_role(_login character varying) OWNER TO developer;

--
-- Name: get_users_whith_role(character varying); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.get_users_whith_role(_role character varying) OWNER TO developer;

--
-- Name: get_users_whith_roles(); Type: FUNCTION; Schema: public; Owner: developer
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


ALTER FUNCTION public.get_users_whith_roles() OWNER TO developer;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: messages_u_2_u; Type: TABLE; Schema: public; Owner: developer
--

CREATE TABLE public.messages_u_2_u (
    id integer NOT NULL,
    users_id_from integer,
    users_id_to integer,
    message text
);


ALTER TABLE public.messages_u_2_u OWNER TO developer;

--
-- Name: messages_u_2_u_id_seq; Type: SEQUENCE; Schema: public; Owner: developer
--

CREATE SEQUENCE public.messages_u_2_u_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_u_2_u_id_seq OWNER TO developer;

--
-- Name: messages_u_2_u_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: developer
--

ALTER SEQUENCE public.messages_u_2_u_id_seq OWNED BY public.messages_u_2_u.id;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: developer
--

CREATE TABLE public.roles (
    id integer NOT NULL,
    user_role text,
    description text
);


ALTER TABLE public.roles OWNER TO developer;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: developer
--

CREATE SEQUENCE public.roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_id_seq OWNER TO developer;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: developer
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- Name: roles_to_users; Type: TABLE; Schema: public; Owner: developer
--

CREATE TABLE public.roles_to_users (
    id integer NOT NULL,
    users_id integer,
    roles_id integer
);


ALTER TABLE public.roles_to_users OWNER TO developer;

--
-- Name: roles_to_users_id_seq; Type: SEQUENCE; Schema: public; Owner: developer
--

CREATE SEQUENCE public.roles_to_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_to_users_id_seq OWNER TO developer;

--
-- Name: roles_to_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: developer
--

ALTER SEQUENCE public.roles_to_users_id_seq OWNED BY public.roles_to_users.id;


--
-- Name: spy_locations; Type: TABLE; Schema: public; Owner: developer
--

CREATE TABLE public.spy_locations (
    id integer NOT NULL,
    location text,
    users_id integer
);


ALTER TABLE public.spy_locations OWNER TO developer;

--
-- Name: spy_locations_id_seq; Type: SEQUENCE; Schema: public; Owner: developer
--

CREATE SEQUENCE public.spy_locations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.spy_locations_id_seq OWNER TO developer;

--
-- Name: spy_locations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: developer
--

ALTER SEQUENCE public.spy_locations_id_seq OWNED BY public.spy_locations.id;


--
-- Name: thirty_years_events; Type: TABLE; Schema: public; Owner: developer
--

CREATE TABLE public.thirty_years_events (
    id integer NOT NULL,
    event text,
    users_id integer,
    available boolean
);


ALTER TABLE public.thirty_years_events OWNER TO developer;

--
-- Name: thirty_years_events_id_seq; Type: SEQUENCE; Schema: public; Owner: developer
--

CREATE SEQUENCE public.thirty_years_events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.thirty_years_events_id_seq OWNER TO developer;

--
-- Name: thirty_years_events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: developer
--

ALTER SEQUENCE public.thirty_years_events_id_seq OWNED BY public.thirty_years_events.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: developer
--

CREATE TABLE public.users (
    id integer NOT NULL,
    login text,
    password text,
    active boolean
);


ALTER TABLE public.users OWNER TO developer;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: developer
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO developer;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: developer
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: messages_u_2_u id; Type: DEFAULT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.messages_u_2_u ALTER COLUMN id SET DEFAULT nextval('public.messages_u_2_u_id_seq'::regclass);


--
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- Name: roles_to_users id; Type: DEFAULT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.roles_to_users ALTER COLUMN id SET DEFAULT nextval('public.roles_to_users_id_seq'::regclass);


--
-- Name: spy_locations id; Type: DEFAULT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.spy_locations ALTER COLUMN id SET DEFAULT nextval('public.spy_locations_id_seq'::regclass);


--
-- Name: thirty_years_events id; Type: DEFAULT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.thirty_years_events ALTER COLUMN id SET DEFAULT nextval('public.thirty_years_events_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: messages_u_2_u; Type: TABLE DATA; Schema: public; Owner: developer
--

COPY public.messages_u_2_u (id, users_id_from, users_id_to, message) FROM stdin;
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: developer
--

COPY public.roles (id, user_role, description) FROM stdin;
1	ADMIN	ADMIN
2	USER	USER
\.


--
-- Data for Name: roles_to_users; Type: TABLE DATA; Schema: public; Owner: developer
--

COPY public.roles_to_users (id, users_id, roles_id) FROM stdin;
1	2	1
2	3	2
\.


--
-- Data for Name: spy_locations; Type: TABLE DATA; Schema: public; Owner: developer
--

COPY public.spy_locations (id, location, users_id) FROM stdin;
1	жопа	3
2	баня	3
3	пионерский лагерь	3
4	МГУ им. Ломоносова	3
\.


--
-- Data for Name: thirty_years_events; Type: TABLE DATA; Schema: public; Owner: developer
--

COPY public.thirty_years_events (id, event, users_id, available) FROM stdin;
1	Полет на параплане	2	t
2	Поход в туалет	2	t
3	Фоткать голых баб на забросе	3	t
4	Поход в пещеры	2	t
5	Частится в зуме	2	t
6	Кальянная на яблочково	2	t
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: developer
--

COPY public.users (id, login, password, active) FROM stdin;
2	admin	admin	t
3	user	user	t
\.


--
-- Name: messages_u_2_u_id_seq; Type: SEQUENCE SET; Schema: public; Owner: developer
--

SELECT pg_catalog.setval('public.messages_u_2_u_id_seq', 1, false);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: developer
--

SELECT pg_catalog.setval('public.roles_id_seq', 2, true);


--
-- Name: roles_to_users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: developer
--

SELECT pg_catalog.setval('public.roles_to_users_id_seq', 4, true);


--
-- Name: spy_locations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: developer
--

SELECT pg_catalog.setval('public.spy_locations_id_seq', 4, true);


--
-- Name: thirty_years_events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: developer
--

SELECT pg_catalog.setval('public.thirty_years_events_id_seq', 6, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: developer
--

SELECT pg_catalog.setval('public.users_id_seq', 5, true);


--
-- Name: messages_u_2_u messages_u_2_u_pkey; Type: CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.messages_u_2_u
    ADD CONSTRAINT messages_u_2_u_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: roles_to_users roles_to_users_pkey; Type: CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.roles_to_users
    ADD CONSTRAINT roles_to_users_pkey PRIMARY KEY (id);


--
-- Name: spy_locations spy_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.spy_locations
    ADD CONSTRAINT spy_locations_pkey PRIMARY KEY (id);


--
-- Name: thirty_years_events thirty_years_events_pkey; Type: CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.thirty_years_events
    ADD CONSTRAINT thirty_years_events_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: messages_u_2_u messages_u_2_u_users_id_from_fkey; Type: FK CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.messages_u_2_u
    ADD CONSTRAINT messages_u_2_u_users_id_from_fkey FOREIGN KEY (users_id_from) REFERENCES public.users(id);


--
-- Name: messages_u_2_u messages_u_2_u_users_id_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.messages_u_2_u
    ADD CONSTRAINT messages_u_2_u_users_id_to_fkey FOREIGN KEY (users_id_to) REFERENCES public.roles(id);


--
-- Name: roles_to_users roles_to_users_roles_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.roles_to_users
    ADD CONSTRAINT roles_to_users_roles_id_fkey FOREIGN KEY (roles_id) REFERENCES public.roles(id);


--
-- Name: roles_to_users roles_to_users_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.roles_to_users
    ADD CONSTRAINT roles_to_users_users_id_fkey FOREIGN KEY (users_id) REFERENCES public.users(id);


--
-- Name: spy_locations spy_locations_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.spy_locations
    ADD CONSTRAINT spy_locations_users_id_fkey FOREIGN KEY (users_id) REFERENCES public.users(id);


--
-- Name: thirty_years_events thirty_years_events_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: developer
--

ALTER TABLE ONLY public.thirty_years_events
    ADD CONSTRAINT thirty_years_events_users_id_fkey FOREIGN KEY (users_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

