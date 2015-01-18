-- to be run as j8ql_user/j8ql_db
SET search_path = public;

-- --------- user --------- --
CREATE TABLE "user"
(
	id bigserial NOT NULL,  
	username character varying(255),
	"firstName" character varying(64),
	"lastName" character varying(64),
	since int,
	title character varying(255),
	email character varying(255),
	create_date timestamp without time zone,
	pref hstore,
	CONSTRAINT user_pkey PRIMARY KEY (id)
);
ALTER TABLE "user"
	OWNER TO j8ql_user;
-- --------- /user --------- --  

-- --------- project --------- --
CREATE TABLE project
(
	id bigserial NOT NULL,
	name character varying(255),
	description text,

	-- Timestamp data
	"creatorId" bigInt,
	"createTime" timestamp with time zone,
	"updateTime" timestamp  with time zone,
		
	CONSTRAINT project_pk PRIMARY KEY (id)
);
ALTER TABLE project
	OWNER TO j8ql_user;	
-- --------- /project --------- --  

-- --------- ticket --------- --
CREATE TABLE ticket
(
	id bigserial NOT NULL,
	"projectId" bigint,
	type character varying(16),
	subject character varying(255),
	environment character varying(512),
	description text,
	"dueDate" date,

	-- Timestamp data
	"creatorId" bigInt,
	"createTime" timestamp with time zone,
	"updateTime" timestamp  with time zone,
		
	CONSTRAINT ticket_pk PRIMARY KEY (id)
);
ALTER TABLE ticket
	OWNER TO j8ql_user;
-- --------- /ticket --------- --  

-- --------- label --------- --

CREATE TABLE label
(
	id serial NOT NULL,
	name character varying(255),

	-- Timestamp data
	"creatorId" bigInt,
	"createTime" timestamp with time zone,
	"updateTime" timestamp  with time zone,
		
	CONSTRAINT label_pk PRIMARY KEY (id)
);
ALTER TABLE label
	OWNER TO j8ql_user;
	
-- --------- /label --------- --  

-- --------- ticketlabel --------- --

CREATE TABLE ticketlabel
(
	"ticketId" bigint NOT NULL,
	"labelId" bigint NOT NULL,
	CONSTRAINT ticketlabel_pk PRIMARY KEY ("ticketId", "labelId")
);
ALTER TABLE ticketlabel
	OWNER TO j8ql_user;
	
-- --------- /ticketlabel --------- --




-------------------------------------------------------------------------
--------------------------- Deprecated Tables ---------------------------

-- --------- contact --------- --
CREATE TABLE contact
(
	id bigint NOT NULL,
	name character varying(255),
	title character varying(255),
	email character varying(255),
	create_date timestamp without time zone,
	CONSTRAINT contact_pkey PRIMARY KEY (id)
);
ALTER TABLE contact
	OWNER TO j8ql_user;
-- --------- /contact --------- --  

-- --------- label_contact --------- --

CREATE TABLE contactlabel
(
	"contactId" bigint NOT NULL,
	"labelId" bigint NOT NULL,
	CONSTRAINT contactlabel_pk PRIMARY KEY ("contactId", "labelId")
);
ALTER TABLE contactlabel
	OWNER TO j8ql_user;	
-- --------- /team_contact --------- --