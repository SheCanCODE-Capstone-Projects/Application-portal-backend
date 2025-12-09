-- Initial database setup
-- This runs automatically when PostgreSQL container starts for the first time

-- Create database if not exists (handled by POSTGRES_DB env var)
-- Additional initialization can go here

-- Example: Create extensions if needed
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

SELECT 'Database initialized successfully' AS status;
