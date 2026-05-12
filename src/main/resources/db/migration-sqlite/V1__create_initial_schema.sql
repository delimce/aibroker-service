PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS tbl_user (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  email TEXT NOT NULL,
  password TEXT NOT NULL,
  temp_token TEXT,
  token_ts INTEGER,
  status TEXT NOT NULL CHECK (status IN ('ACTIVE','INACTIVE','PENDING')),
  created_at DATETIME NOT NULL,
  updated_at DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS user_email_index ON tbl_user (email);

CREATE TABLE IF NOT EXISTS tbl_provider (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  base_url TEXT NOT NULL,
  api_key TEXT NOT NULL,
  enabled INTEGER NOT NULL CHECK (enabled IN (0,1)),
  created_at DATETIME NOT NULL,
  updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS tbl_model (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  provider_id INTEGER NOT NULL,
  type TEXT NOT NULL CHECK (type IN ('CHAT','EMBEDDING')),
  cost_token NUMERIC(10,6) NOT NULL,
  cost_token_unit TEXT,
  enabled INTEGER NOT NULL CHECK (enabled IN (0,1)),
  created_at DATETIME NOT NULL,
  updated_at DATETIME,
  CONSTRAINT model_provider_fk FOREIGN KEY (provider_id) REFERENCES tbl_provider (id)
);

CREATE INDEX IF NOT EXISTS model_provider_index ON tbl_model (provider_id);
