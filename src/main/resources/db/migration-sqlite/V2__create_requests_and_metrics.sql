PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS tbl_user_request (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  model_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  prompt TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT user_request_model_fk FOREIGN KEY (model_id) REFERENCES tbl_model (id),
  CONSTRAINT user_request_user_fk FOREIGN KEY (user_id) REFERENCES tbl_user (id)
);

CREATE INDEX IF NOT EXISTS user_request_model_index ON tbl_user_request (model_id);
CREATE INDEX IF NOT EXISTS user_request_user_index ON tbl_user_request (user_id);

CREATE TABLE IF NOT EXISTS tbl_request_metric (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  request_id INTEGER NOT NULL,
  completion_tokens INTEGER,
  prompt_tokens INTEGER,
  total_tokens INTEGER,
  prompt_cache_hit_tokens INTEGER,
  prompt_cache_miss_tokens INTEGER,
  created_at DATETIME NOT NULL,
  CONSTRAINT request_metric_fk FOREIGN KEY (request_id) REFERENCES tbl_user_request (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS request_metric_index ON tbl_request_metric (request_id);
