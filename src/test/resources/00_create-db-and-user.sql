-- to be run as postgres/postgres
DROP DATABASE IF EXISTS j8ql_db;
DROP USER IF EXISTS j8ql_user;
CREATE USER j8ql_user PASSWORD 'welcome';
CREATE DATABASE j8ql_db owner j8ql_user ENCODING = 'UTF-8';

\c j8ql_db
CREATE EXTENSION hstore;