-- grant all on medicine_test to postgres
-- GRANT ALL PRIVILEGES ON DATABASE  medicine_test TO postgres;
-- grant all on medicine to postgres
-- GRANT ALL PRIVILEGES ON DATABASE medicine TO postgres;
CREATE TABLE IF NOT EXISTS patients (
     id UUID NOT NULL PRIMARY KEY,
     patient JSONB NOT NULL DEFAULT '{}'
     );
TRUNCATE TABLE patients;