

DROP USER IF EXISTS j8ql_user;
CREATE USER j8ql_user PASSWORD 'welcome';

DROP DATABASE IF EXISTS j8ql_db;
CREATE DATABASE j8ql_db;

ALTER DATABASE j8ql_db OWNER TO j8ql_user;

-- We connect here, because the assumption is to enter the password and then copy/paset
-- josql_test_01...sql
\c j8ql_db j8ql_user