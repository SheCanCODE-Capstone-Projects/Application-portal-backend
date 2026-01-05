-- Drop unnecessary tables for admin-only system
DROP TABLE IF EXISTS email_verification_token CASCADE;
DROP TABLE IF EXISTS password_reset_token CASCADE;
DROP TABLE IF EXISTS disability_information CASCADE;
DROP TABLE IF EXISTS documents CASCADE;
DROP TABLE IF EXISTS education_occupation CASCADE;
DROP TABLE IF EXISTS emergency_contacts CASCADE;
DROP TABLE IF EXISTS motivation_answers CASCADE;
DROP TABLE IF EXISTS vulnerability_information CASCADE;
DROP TABLE IF EXISTS personal_information CASCADE;
DROP TABLE IF EXISTS register CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Keep only admin essential tables
-- admin_users (already exists)
-- applications (simplified for admin review)
-- cohorts (for organizing applications)
-- cohort_requirements (for cohort criteria)
-- admin_activity (for audit logs)

-- Simplify applications table for admin use
ALTER TABLE applications DROP CONSTRAINT IF EXISTS FK_APPLICATIONS_ON_USER;
ALTER TABLE applications DROP COLUMN IF EXISTS user_id;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS applicant_name VARCHAR(255);
ALTER TABLE applications ADD COLUMN IF NOT EXISTS applicant_email VARCHAR(255);
ALTER TABLE applications ADD COLUMN IF NOT EXISTS application_data JSONB;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;