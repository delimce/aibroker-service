PRAGMA foreign_keys = ON;

ALTER TABLE tbl_model RENAME COLUMN cost_token TO cost_token_in;
ALTER TABLE tbl_model ADD COLUMN cost_token_out NUMERIC(10,6) NOT NULL DEFAULT 0.0;
