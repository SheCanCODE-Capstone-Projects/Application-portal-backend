-- Rename the column from provider_id to provider
ALTER TABLE register
    RENAME COLUMN provider_id TO provider;