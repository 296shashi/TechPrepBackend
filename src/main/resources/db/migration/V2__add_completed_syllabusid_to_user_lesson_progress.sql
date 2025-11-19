-- Migration: add `syllabus_id` and `completed` columns to user_lesson_progress
-- Adjust this migration for your SQL dialect if needed (MySQL / MariaDB syntax shown)

ALTER TABLE user_lesson_progress
  ADD COLUMN syllabus_id BIGINT NULL,
  ADD COLUMN completed BOOLEAN DEFAULT FALSE;

-- Optionally add an index on syllabus_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_ulp_syllabus ON user_lesson_progress (syllabus_id);
