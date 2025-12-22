-- Remove User entity references and update admin service to work with applications directly
-- This migration ensures the admin system works without User table

-- Update applications table to be self-contained
ALTER TABLE applications ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'PENDING';
ALTER TABLE applications ADD COLUMN IF NOT EXISTS applicant_name VARCHAR(255);
ALTER TABLE applications ADD COLUMN IF NOT EXISTS applicant_email VARCHAR(255);