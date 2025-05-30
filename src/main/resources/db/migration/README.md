# Database Migrations with Flyway

This directory contains database migration scripts for the aibroker-service application.

## Migration Naming Convention

Flyway follows a specific naming convention for migration files:

```
V<version>__<description>.sql
```

For example:
- `V1__create_initial_schema.sql`
- `V2__add_user_roles.sql`
- `V2.1__fix_user_constraints.sql`

## Adding New Migrations

To add a new migration:

1. Create a new SQL file in this directory following the naming convention
2. The version number should be higher than the previous migration
3. Write your SQL statements in the file
4. When the application starts, Flyway will automatically apply the new migration

## Important Notes

- Never modify existing migrations that have been applied to the database
- Always create a new migration file for schema changes
- The `baseline-on-migrate` property is set to true, which allows Flyway to work with existing databases
- For major changes, consider using repeatable migrations (R__name.sql)

## Commands

You can also run Flyway commands manually using Maven:

```bash
# See the migration status
mvn flyway:info

# Apply pending migrations
mvn flyway:migrate

# Clean the database (USE WITH CAUTION - DELETES ALL DATA)
mvn flyway:clean
```

For more information, see the [Flyway documentation](https://flywaydb.org/documentation/).
