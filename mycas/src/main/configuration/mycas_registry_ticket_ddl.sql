-- drop SEQUENCE mycas.hibernate_sequence;
-- drop TABLE mycas.locks;
-- drop TABLE mycas.rs_attributes;
-- drop table mycas.serviceticket;
-- drop TABLE mycas.registeredserviceimpl;
-- drop TABLE mycas.ticketgrantingticket;



--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.8
-- Dumped by pg_dump version 9.1.11
-- Started on 2014-02-05 10:37:34 CET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


CREATE SEQUENCE mycas.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE myportal.hibernate_sequence OWNER TO myportal;


SET default_with_oids = false;

--
-- TOC entry 164 (class 1259 OID 2304196)
-- Dependencies: 5
-- Name: locks; Type: TABLE; Schema: myportal; Owner: myportal; Tablespace: 
--

CREATE TABLE mycas.locks (
    application_id character varying(255) NOT NULL,
    expiration_date timestamp without time zone,
    unique_id character varying(255)
);


ALTER TABLE mycas.locks OWNER TO myportal;

--
-- TOC entry 161 (class 1259 OID 2304171)
-- Dependencies: 1807 5
-- Name: registeredserviceimpl; Type: TABLE; Schema: myportal; Owner: myportal; Tablespace: 
--

CREATE TABLE mycas.registeredserviceimpl (
    expression_type character varying(15) DEFAULT 'ant'::character varying NOT NULL,
    id bigint NOT NULL,
    allowedtoproxy boolean NOT NULL,
    anonymousaccess boolean NOT NULL,
    description character varying(255),
    enabled boolean NOT NULL,
    evaluation_order integer NOT NULL,
    ignoreattributes boolean NOT NULL,
    name character varying(255),
    serviceid character varying(255),
    ssoenabled boolean NOT NULL,
    theme character varying(255),
    username_attr character varying(256)
);


ALTER TABLE mycas.registeredserviceimpl OWNER TO myportal;

--
-- TOC entry 165 (class 1259 OID 2304204)
-- Dependencies: 5
-- Name: rs_attributes; Type: TABLE; Schema: myportal; Owner: myportal; Tablespace: 
--

CREATE TABLE mycas.rs_attributes (
    registeredserviceimpl_id bigint NOT NULL,
    a_name character varying(255) NOT NULL,
    a_id integer NOT NULL
);


ALTER TABLE mycas.rs_attributes OWNER TO myportal;

--
-- TOC entry 162 (class 1259 OID 2304180)
-- Dependencies: 5
-- Name: serviceticket; Type: TABLE; Schema: myportal; Owner: myportal; Tablespace: 
--

CREATE TABLE mycas.serviceticket (
    id character varying(255) NOT NULL,
    number_of_times_used integer,
    creation_time bigint,
    expiration_policy oid NOT NULL,
    last_time_used bigint,
    previous_last_time_used bigint,
    from_new_login boolean NOT NULL,
    ticket_already_granted boolean NOT NULL,
    service oid NOT NULL,
    ticketgrantingticket_id character varying(255)
);


ALTER TABLE mycas.serviceticket OWNER TO myportal;

--myportal
-- TOC entry 163 (class 1259 OID 2304188)
-- Dependencies: 5
-- Name: ticketgrantingticket; Type: TABLE; Schema: myportal; Owner: myportal; Tablespace: 
--

CREATE TABLE mycas.ticketgrantingticket (
    id character varying(255) NOT NULL,
    number_of_times_used integer,
    creation_time bigint,
    expiration_policy oid NOT NULL,
    last_time_used bigint,
    previous_last_time_used bigint,
    authentication oid NOT NULL,
    expired boolean NOT NULL,
    services_granted_access_to oid NOT NULL,
    ticketgrantingticket_id character varying(255)
);


ALTER TABLE mycas.ticketgrantingticket OWNER TO myportal;

--
-- TOC entry 1936 (class 0 OID 0)
-- Dependencies: 166
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: myportal; Owner: myportal
--

SELECT pg_catalog.setval('mycas.hibernate_sequence', 1, false);


--

ALTER TABLE ONLY mycas.locks
    ADD CONSTRAINT locks_pkey PRIMARY KEY (application_id);


--
-- TOC entry 1809 (class 2606 OID 2304179)
-- Dependencies: 161 161 1929
-- Name: registeredserviceimpl_pkey; Type: CONSTRAINT; Schema: myportal; Owner: myportal; Tablespace: 
--

ALTER TABLE ONLY mycas.registeredserviceimpl
    ADD CONSTRAINT registeredserviceimpl_pkey PRIMARY KEY (id);


--
-- TOC entry 1817 (class 2606 OID 2304208)
-- Dependencies: 165 165 165 1929
-- Name: rs_attributes_pkey; Type: CONSTRAINT; Schema: myportal; Owner: myportal; Tablespace: 
--

ALTER TABLE ONLY mycas.rs_attributes
    ADD CONSTRAINT rs_attributes_pkey PRIMARY KEY (registeredserviceimpl_id, a_id);


--
-- TOC entry 1811 (class 2606 OID 2304187)
-- Dependencies: 162 162 1929
-- Name: serviceticket_pkey; Type: CONSTRAINT; Schema: myportal; Owner: myportal; Tablespace: 
--

ALTER TABLE ONLY mycas.serviceticket
    ADD CONSTRAINT serviceticket_pkey PRIMARY KEY (id);


--
-- TOC entry 1813 (class 2606 OID 2304195)
-- Dependencies: 163 163 1929
-- Name: ticketgrantingticket_pkey; Type: CONSTRAINT; Schema: myportal; Owner: myportal; Tablespace: 
--

ALTER TABLE ONLY mycas.ticketgrantingticket
    ADD CONSTRAINT ticketgrantingticket_pkey PRIMARY KEY (id);


--
-- TOC entry 1820 (class 2606 OID 2304219)
-- Dependencies: 165 1808 161 1929
-- Name: fk4322e153c595e1f; Type: FK CONSTRAINT; Schema: myportal; Owner: myportal
--

ALTER TABLE ONLY mycas.rs_attributes
    ADD CONSTRAINT fk4322e153c595e1f FOREIGN KEY (registeredserviceimpl_id) REFERENCES mycas.registeredserviceimpl(id);


--
-- TOC entry 1818 (class 2606 OID 2304209)
-- Dependencies: 162 163 1812 1929
-- Name: fk7645ade132a2c0e5; Type: FK CONSTRAINT; Schema: myportal; Owner: myportal
--

ALTER TABLE ONLY mycas.serviceticket
    ADD CONSTRAINT fk7645ade132a2c0e5 FOREIGN KEY (ticketgrantingticket_id) REFERENCES mycas.ticketgrantingticket(id);


--
-- TOC entry 1819 (class 2606 OID 2304214)
-- Dependencies: 1812 163 163 1929
-- Name: fkb4c4cdde32a2c0e5; Type: FK CONSTRAINT; Schema: myportal; Owner: myportal
--

ALTER TABLE ONLY mycas.ticketgrantingticket
    ADD CONSTRAINT fkb4c4cdde32a2c0e5 FOREIGN KEY (ticketgrantingticket_id) REFERENCES mycas.ticketgrantingticket(id);



