-- Add missing columns to users table for entity relationships
ALTER TABLE users ADD COLUMN register_id BIGINT;
ALTER TABLE users ADD COLUMN cohort_id BIGINT;

-- Add foreign key constraints
ALTER TABLE users ADD CONSTRAINT fk_users_register FOREIGN KEY (register_id) REFERENCES register(id);
ALTER TABLE users ADD CONSTRAINT fk_users_cohort FOREIGN KEY (cohort_id) REFERENCES cohorts(id);

-- Remove duplicate columns that are now in register table
ALTER TABLE users DROP COLUMN IF EXISTS email;
ALTER TABLE users DROP COLUMN IF EXISTS username;
ALTER TABLE users DROP COLUMN IF EXISTS password;
ALTER TABLE users DROP COLUMN IF EXISTS role;
ALTER TABLE users DROP COLUMN IF EXISTS is_verified;