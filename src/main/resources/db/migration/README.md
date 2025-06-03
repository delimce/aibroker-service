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

You can run Flyway commands manually using our custom script that automatically loads database configuration from your .env file. 
Make sure the `flyway.sh` script has execute permissions by running `chmod +x flyway.sh` if needed:

```bash
# See the migration status
./flyway.sh info

# Apply pending migrations
./flyway.sh migrate

# Clean the database (USE WITH CAUTION - DELETES ALL DATA)
./flyway.sh clean
```

Alternatively, if you prefer using Maven directly and specifying database parameters manually:

```bash
# See the migration status
./mvnw flyway:info -Dflyway.url=jdbc:mysql://127.0.0.1:3307/aibroker_db -Dflyway.user=admin -Dflyway.password=admin

# Apply pending migrations
./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://127.0.0.1:3307/aibroker_db -Dflyway.user=admin -Dflyway.password=admin

# Clean the database (USE WITH CAUTION - DELETES ALL DATA)
./mvnw flyway:clean -Dflyway.url=jdbc:mysql://127.0.0.1:3307/aibroker_db -Dflyway.user=admin -Dflyway.password=admin
```

For more information, see the [Flyway documentation](https://flywaydb.org/documentation/).
