CREATE TABLE IF NOT EXISTS patients (
    id UUID NOT NULL PRIMARY KEY,
     patient JSONB NOT NULL DEFAULT '{}'
     );