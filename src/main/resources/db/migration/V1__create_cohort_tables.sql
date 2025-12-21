-- Ensure cohort and cohort_requirement tables exist with expected columns

-- Cohorts table
CREATE TABLE IF NOT EXISTS cohorts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    year INTEGER,
    is_open BOOLEAN DEFAULT TRUE,
    application_limit INTEGER,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

ALTER TABLE cohorts
    ADD COLUMN IF NOT EXISTS name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS domain VARCHAR(255),
    ADD COLUMN IF NOT EXISTS year INTEGER,
    ADD COLUMN IF NOT EXISTS is_open BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS application_limit INTEGER,
    ADD COLUMN IF NOT EXISTS start_date DATE,
    ADD COLUMN IF NOT EXISTS end_date DATE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- Cohort requirements table
CREATE TABLE IF NOT EXISTS cohort_requirements (
    id BIGSERIAL PRIMARY KEY,
    cohort_id BIGINT REFERENCES cohorts (id) ON DELETE CASCADE,
    requirement_type VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

ALTER TABLE cohort_requirements
    ADD COLUMN IF NOT EXISTS cohort_id BIGINT,
    ADD COLUMN IF NOT EXISTS requirement_type VARCHAR(255),
    ADD COLUMN IF NOT EXISTS description VARCHAR(255),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- Add foreign key constraint (skip if exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_cohort_requirements_cohort'
    ) THEN
        ALTER TABLE cohort_requirements
            ADD CONSTRAINT fk_cohort_requirements_cohort
                FOREIGN KEY (cohort_id) REFERENCES cohorts (id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_cohort_requirements_cohort_id
    ON cohort_requirements (cohort_id);

