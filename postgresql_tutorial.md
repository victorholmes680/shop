# PostgreSQL Tutorial

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Database Operations](#database-operations)
5. [Table Operations](#table-operations)
6. [Data Manipulation](#data-manipulation)
7. [Queries](#queries)
8. [Advanced Features](#advanced-features)
9. [Best Practices](#best-practices)

---

## Introduction

**PostgreSQL** is a powerful, open-source object-relational database system with over 35 years of active development. It's known for its reliability, feature robustness, and performance.

### Key Features
- ACID compliance
- Support for both SQL and JSON queries
- Advanced indexing and performance optimization
- Extensive data types including arrays, hstore, and user-defined types
- Full text search capabilities
- Replication and high availability
- Extensible architecture

---

## Installation

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

### macOS (using Homebrew)
```bash
brew install postgresql
brew services start postgresql
```

### Windows
Download the installer from the official website: https://www.postgresql.org/download/windows/

---

## Getting Started

### Start the PostgreSQL Service
```bash
# Linux
sudo service postgresql start

# macOS
brew services start postgresql

# Windows
The service starts automatically after installation
```

### Access PostgreSQL Command Line
```bash
# Switch to postgres user and access psql
sudo -u postgres psql

# Or connect directly
psql -U postgres
```

### Basic psql Commands
```sql
-- List all databases
\l

-- Connect to a database
\c database_name

-- List all tables
\dt

-- Describe a table
\d table_name

-- Show all users
\du

-- Execute SQL from file
\i filename.sql

-- Clear screen
\! clear

-- Quit psql
\q
```

---

## Database Operations

### Create Database
```sql
-- Create a new database
CREATE DATABASE mydb;

-- Create database with specific encoding
CREATE DATABASE mydb WITH ENCODING 'UTF8';

-- Create database with owner
CREATE DATABASE mydb OWNER username;

-- Create database with specific locale
CREATE DATABASE mydb LOCALE 'en_US.UTF-8';
```

### Connect to Database
```sql
\c mydb
```

### Drop Database
```sql
-- Drop database (must disconnect first)
DROP DATABASE mydb;

-- Drop database if exists
DROP DATABASE IF EXISTS mydb;
```

### Rename Database
```sql
ALTER DATABASE old_name RENAME TO new_name;
```

---

## Table Operations

### Create Table
```sql
-- Basic table creation
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table with foreign key
CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table with constraints
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock INTEGER DEFAULT 0 CHECK (stock >= 0),
    category VARCHAR(50),
    is_active BOOLEAN DEFAULT true
);
```

### Data Types

| Category | Types | Description |
|----------|-------|-------------|
| **Numeric** | INTEGER, BIGINT, SMALLINT, DECIMAL, NUMERIC, REAL, DOUBLE PRECISION | Various number types |
| **Character** | VARCHAR(n), CHAR(n), TEXT | String types |
| **Boolean** | BOOLEAN | True/False values |
| **Date/Time** | DATE, TIME, TIMESTAMP, TIMESTAMPTZ, INTERVAL | Date and time types |
| **Binary** | BYTEA | Binary data |
| **Array** | INTEGER[], TEXT[], etc. | Arrays of any type |
| **JSON** | JSON, JSONB | JSON data |
| **Special** | UUID, MACADDR, INET | Specialized types |

### Modify Table
```sql
-- Add column
ALTER TABLE users ADD COLUMN age INTEGER;

-- Add column with default
ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'active';

-- Drop column
ALTER TABLE users DROP COLUMN age;

-- Rename column
ALTER TABLE users RENAME COLUMN username TO user_name;

-- Change column type
ALTER TABLE users ALTER COLUMN age TYPE INTEGER USING age::INTEGER;

-- Add constraint
ALTER TABLE users ADD CONSTRAINT email_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Drop constraint
ALTER TABLE users DROP CONSTRAINT email_check;
```

### Drop Table
```sql
-- Drop table
DROP TABLE users;

-- Drop table if exists
DROP TABLE IF EXISTS users;

-- Drop table with cascade (drops dependent objects)
DROP TABLE users CASCADE;
```

---

## Data Manipulation

### Insert Data
```sql
-- Insert single row
INSERT INTO users (username, email) VALUES ('john_doe', 'john@example.com');

-- Insert multiple rows
INSERT INTO users (username, email) VALUES
    ('alice', 'alice@example.com'),
    ('bob', 'bob@example.com'),
    ('charlie', 'charlie@example.com');

-- Insert with returning
INSERT INTO users (username, email)
VALUES ('david', 'david@example.com')
RETURNING id, username;

-- Insert from select
INSERT INTO archived_users (username, email)
SELECT username, email FROM users WHERE created_at < '2020-01-01';
```

### Update Data
```sql
-- Update single column
UPDATE users SET email = 'newemail@example.com' WHERE id = 1;

-- Update multiple columns
UPDATE users
SET email = 'updated@example.com', status = 'verified'
WHERE username = 'john_doe';

-- Update with returning
UPDATE products SET price = price * 1.1
RETURNING id, name, price;

-- Update from another table
UPDATE orders o
SET status = 'shipped'
FROM shipments s
WHERE o.id = s.order_id;
```

### Delete Data
```sql
-- Delete specific rows
DELETE FROM users WHERE id = 1;

-- Delete with condition
DELETE FROM users WHERE created_at < '2020-01-01';

-- Delete all rows (truncate is faster)
DELETE FROM users;
-- or
TRUNCATE TABLE users;

-- Delete with returning
DELETE FROM users WHERE status = 'inactive' RETURNING *;
```

---

## Queries

### Basic Queries
```sql
-- Select all columns
SELECT * FROM users;

-- Select specific columns
SELECT username, email FROM users;

-- Select distinct values
SELECT DISTINCT status FROM users;

-- Limit results
SELECT * FROM users LIMIT 10;

-- Offset and limit (pagination)
SELECT * FROM users LIMIT 10 OFFSET 20;

-- Order by
SELECT * FROM users ORDER BY created_at DESC;
SELECT * FROM users ORDER BY username ASC, created_at DESC;
```

### Filtering
```sql
-- Where clause
SELECT * FROM users WHERE age > 18;

-- Multiple conditions
SELECT * FROM users WHERE age >= 18 AND status = 'active';

-- OR conditions
SELECT * FROM users WHERE status = 'active' OR status = 'pending';

-- IN clause
SELECT * FROM users WHERE id IN (1, 2, 3, 4, 5);

-- NOT IN
SELECT * FROM users WHERE id NOT IN (6, 7, 8);

-- BETWEEN
SELECT * FROM users WHERE created_at BETWEEN '2024-01-01' AND '2024-12-31';

-- Pattern matching
SELECT * FROM users WHERE username LIKE 'john%';  -- Starts with john
SELECT * FROM users WHERE email LIKE '%@gmail.com';  -- Ends with gmail.com
SELECT * FROM users WHERE username LIKE '%_doe';  -- Contains _doe

-- NULL handling
SELECT * FROM users WHERE email IS NULL;
SELECT * FROM users WHERE email IS NOT NULL;
```

### Aggregation
```sql
-- Count
SELECT COUNT(*) FROM users;
SELECT COUNT(email) FROM users;
SELECT COUNT(DISTINCT status) FROM users;

-- Sum
SELECT SUM(price) FROM products;

-- Average
SELECT AVG(price) FROM products;

-- Min/Max
SELECT MIN(created_at), MAX(created_at) FROM users;
SELECT MIN(price), MAX(price) FROM products;

-- Group by
SELECT status, COUNT(*) FROM users GROUP BY status;
SELECT category, AVG(price) FROM products GROUP BY category;

-- Having (filter groups)
SELECT status, COUNT(*) as count
FROM users
GROUP BY status
HAVING COUNT(*) > 5;

-- Multiple aggregations
SELECT
    category,
    COUNT(*) as product_count,
    AVG(price) as avg_price,
    MIN(price) as min_price,
    MAX(price) as max_price
FROM products
GROUP BY category;
```

### Joins
```sql
-- Inner Join
SELECT u.username, p.title
FROM users u
INNER JOIN posts p ON u.id = p.user_id;

-- Left Join
SELECT u.username, p.title
FROM users u
LEFT JOIN posts p ON u.id = p.user_id;

-- Right Join
SELECT u.username, p.title
FROM users u
RIGHT JOIN posts p ON u.id = p.user_id;

-- Full Outer Join
SELECT u.username, p.title
FROM users u
FULL OUTER JOIN posts p ON u.id = p.user_id;

-- Multiple Joins
SELECT u.username, p.title, c.comment_text
FROM users u
JOIN posts p ON u.id = p.user_id
JOIN comments c ON p.id = c.post_id;

-- Self Join
SELECT e1.name as employee, e2.name as manager
FROM employees e1
LEFT JOIN employees e2 ON e1.manager_id = e2.id;

-- Cross Join (Cartesian product)
SELECT * FROM users CROSS JOIN departments;
```

### Union
```sql
-- Union (removes duplicates)
SELECT username FROM admins
UNION
SELECT username FROM users;

-- Union All (keeps duplicates)
SELECT username FROM admins
UNION ALL
SELECT username FROM users;

-- Intersect
SELECT username FROM admins
INTERSECT
SELECT username FROM users;

-- Except
SELECT username FROM users
EXCEPT
SELECT username FROM banned_users;
```

---

## Advanced Features

### Views
```sql
-- Create view
CREATE VIEW user_post_counts AS
SELECT u.id, u.username, COUNT(p.id) as post_count
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
GROUP BY u.id, u.username;

-- Use view
SELECT * FROM user_post_counts WHERE post_count > 10;

-- Drop view
DROP VIEW user_post_counts;
```

### Functions
```sql
-- Create function
CREATE OR REPLACE FUNCTION get_user_posts(user_id INTEGER)
RETURNS TABLE (
    post_title VARCHAR(200),
    post_content TEXT,
    created_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.title, p.content, p.created_at
    FROM posts p
    WHERE p.user_id = user_id
    ORDER BY p.created_at DESC;
END;
$$ LANGUAGE plpgsql;

-- Use function
SELECT * FROM get_user_posts(1);
```

### Triggers
```sql
-- Create trigger function
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
CREATE TRIGGER update_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_updated_at();
```

### Indexes
```sql
-- Create index
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);

-- Create composite index
CREATE INDEX idx_posts_user_created ON posts(user_id, created_at DESC);

-- Create unique index
CREATE UNIQUE INDEX idx_users_username ON users(username);

-- Create partial index
CREATE INDEX idx_active_users ON users(email) WHERE is_active = true;

-- Drop index
DROP INDEX idx_users_email;
```

### Transactions
```sql
-- Start transaction
BEGIN;

-- Multiple operations
INSERT INTO accounts (user_id, balance) VALUES (1, 1000);
UPDATE accounts SET balance = balance - 100 WHERE user_id = 1;
UPDATE accounts SET balance = balance + 100 WHERE user_id = 2;

-- Commit transaction
COMMIT;

-- Or rollback
ROLLBACK;
```

### JSON Support
```sql
-- Create table with JSONB column
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    event_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert JSON data
INSERT INTO events (event_data) VALUES
('{"name": "click", "page": "/home", "user_id": 123}');

-- Query JSON data
SELECT event_data->>'name' as event_name
FROM events
WHERE event_data->>'page' = '/home';

-- Update JSON data
UPDATE events
SET event_data = event_data || '{"timestamp": "2024-03-10T10:00:00"}'
WHERE id = 1;

-- JSON operations
SELECT * FROM events WHERE event_data @> '{"user_id": 123}';
SELECT event_data->'metadata'->>'source' FROM events;
```

### Window Functions
```sql
-- Row number
SELECT
    username,
    created_at,
    ROW_NUMBER() OVER (ORDER BY created_at DESC) as row_num
FROM users;

-- Rank with partition
SELECT
    category,
    name,
    price,
    RANK() OVER (PARTITION BY category ORDER BY price DESC) as price_rank
FROM products;

-- Running total
SELECT
    order_date,
    amount,
    SUM(amount) OVER (ORDER BY order_date) as running_total
FROM orders;

-- Moving average
SELECT
    date,
    value,
    AVG(value) OVER (ORDER BY date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) as moving_avg
FROM metrics;
```

---

## Best Practices

### 1. Naming Conventions
- Use lowercase names (PostgreSQL is case-sensitive)
- Use snake_case for table and column names
- Use singular or plural consistently (prefer singular)
- Use descriptive names (e.g., `created_at` instead of `date`)

### 2. Primary Keys
```sql
-- Always define primary keys
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    -- or
    -- id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50)
);
```

### 3. Indexes Wisely
- Index columns used in WHERE, JOIN, and ORDER BY clauses
- Don't over-index (each index slows down writes)
- Use EXPLAIN ANALYZE to verify index usage

### 4. Use Constraints
```sql
-- Use NOT NULL for required fields
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Use CHECK for validation
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)
);
```

### 5. Use Transactions
Always wrap related operations in transactions for data consistency.

### 6. Use Prepared Statements
Prevent SQL injection and improve performance with prepared statements.

### 7. Backup Regularly
```bash
# Backup database
pg_dump mydb > backup.sql

# Backup with custom format
pg_dump -Fc mydb > backup.dump

# Restore
psql mydb < backup.sql
pg_restore -d mydb backup.dump
```

### 8. Monitor Performance
```sql
-- View slow queries
SELECT query, mean_exec_time
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;

-- View table sizes
SELECT
    tablename,
    pg_size_pretty(pg_total_relation_size(tablename::regclass)) as size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(tablename::regclass) DESC;
```

---

## Useful Resources

- **Official Documentation**: https://www.postgresql.org/docs/
- **SQL Fiddle**: Practice SQL online
- **pgAdmin**: Graphical management tool
- **psql**: Command-line interface

---

## Quick Reference

### Common psql Commands
| Command | Description |
|---------|-------------|
| `\l` | List databases |
| `\c dbname` | Connect to database |
| `\dt` | List tables |
| `\d tablename` | Describe table |
| `\du` | List users |
| `\q` | Quit |

### Common SQL Commands
| Command | Description |
|---------|-------------|
| `SELECT` | Query data |
| `INSERT` | Add data |
| `UPDATE` | Modify data |
| `DELETE` | Remove data |
| `CREATE TABLE` | Create table |
| `DROP TABLE` | Delete table |
| `ALTER TABLE` | Modify table |
| `CREATE INDEX` | Create index |
| `BEGIN/COMMIT` | Transaction control |

---

**Happy Querying! 🐘**
