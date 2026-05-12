# SQLite Migrations (Home Profile)

This directory contains Flyway migration scripts for the SQLite database used by the `home` profile.

## Naming Convention

Use the standard Flyway versioned format:

V<version>__<description>.sql

## Notes

- These migrations are SQLite-specific.
- Keep them aligned with the MySQL migrations under `db/migration`.
- Do not modify already-applied migrations; create a new versioned file instead.
