# Database Migrations with Liquibase

This project uses Liquibase to manage database schema changes in a version-controlled manner.

## Directory Structure

```
src/main/resources/db/changelog/
├── db.changelog-master.json         # Master changelog file
└── changes/
    └── 001-initial-schema.json      # Initial database schema
```

## How It Works

1. **Master Changelog** (`db.changelog-master.json`): Includes all changesets in order
2. **Individual Changesets** (`changes/xxx-*.json`): Each file contains one or more related database changes

## Key Features

- ✅ **Version Control**: All schema changes are tracked in Git
- ✅ **Rollback Support**: Each changeset includes rollback instructions
- ✅ **Environment Safety**: Only applies changes that haven't been run
- ✅ **Team Collaboration**: Multiple developers can work on schema changes safely

## Current Schema

### Tables
- **product**: E-shop product catalog
  - id, name, genre, unit_price, unit_in_stock, release_date, image_uri
  
- **app_user**: User authentication and authorization
  - id, firstname, lastname, email, password, role

### Indexes
- `idx_product_name`: Product name lookup
- `idx_product_genre`: Product genre filtering
- `idx_user_email`: User email lookup (unique)

## Adding New Changes

When you need to modify the database schema:

### 1. Create a new changeset file

```bash
touch src/main/resources/db/changelog/changes/002-your-change-description.json
```

### 2. Add your changeset

```json
{
  "databaseChangeLog": [
    {
      "changeSet": {
        "id": "002-add-product-description",
        "author": "yourname",
        "comment": "Add description field to product table",
        "changes": [
          {
            "addColumn": {
              "tableName": "product",
              "columns": [
                {
                  "column": {
                    "name": "description",
                    "type": "TEXT"
                  }
                }
              ]
            }
          }
        ],
        "rollback": [
          {
            "dropColumn": {
              "tableName": "product",
              "columnName": "description"
            }
          }
        ]
      }
    }
  ]
}
```

### 3. Include it in the master changelog

Edit `db.changelog-master.json`:
```json
{
  "databaseChangeLog": [
    {
      "include": {
        "file": "db/changelog/changes/001-initial-schema.json"
      }
    },
    {
      "include": {
        "file": "db/changelog/changes/002-your-change-description.json"
      }
    }
  ]
}
```

### 4. Test the migration

```bash
# The migration will run automatically when you start the application
./gradlew bootRun
```

## Common Operations

### Check Migration Status

Liquibase tracks applied changes in the `databasechangelog` table:

```sql
SELECT * FROM databasechangelog;
```

### Rollback Last Change

Add to `application.yml`:
```yaml
spring:
  liquibase:
    rollback-file: rollback.sql
```

### Validate Changesets

```bash
./gradlew liquibaseValidate
```

## Best Practices

1. **Never modify existing changesets** - Create new ones instead
2. **Always include rollback** - Make changes reversible
3. **Use descriptive IDs** - Format: `XXX-description-of-change`
4. **Test locally first** - Verify migrations work before committing
5. **Keep changesets small** - One logical change per changeset
6. **Add comments** - Explain why the change is needed

## Troubleshooting

### Liquibase Won't Run

Check that the database is accessible:
```bash
docker ps | grep postgres-infra
```

### Schema Out of Sync

If you've been using `ddl-auto: update` and switching to Liquibase:
1. Drop all tables
2. Restart the application (Liquibase will create everything)

```sql
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;
DROP TABLE IF EXISTS databasechangelog CASCADE;
DROP TABLE IF EXISTS databasechangeloglock CASCADE;
```

### Reset Liquibase History

⚠️ **WARNING: Only for development**
```sql
DELETE FROM databasechangelog;
```

## Configuration

See `application.yml` for Liquibase configuration:

```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.json
    enabled: true
  jpa:
    hibernate:
      ddl-auto: validate  # Changed from 'update' to 'validate'
```

## References

- [Liquibase Documentation](https://docs.liquibase.com/)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)
