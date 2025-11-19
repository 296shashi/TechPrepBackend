-- Migration: move `status` from user_lesson_progress to lessons
-- Adds status column to lessons and drops status from user_lesson_progress

-- Add status column to lessons
ALTER TABLE lessons
  ADD COLUMN status VARCHAR(50) DEFAULT 'INACTIVE';

-- Copy existing values from user_lesson_progress if present.
-- This copies status for lessons that have at least one progress row with a non-null status.
-- Adjust the query as needed for your SQL dialect.

UPDATE lessons l
SET status = (
  SELECT DISTINCT ulp.status
  FROM user_lesson_progress ulp
  WHERE ulp.lesson_id = l.id AND ulp.status IS NOT NULL
  LIMIT 1
)
WHERE EXISTS (
  SELECT 1 FROM user_lesson_progress ulp WHERE ulp.lesson_id = l.id AND ulp.status IS NOT NULL
);

-- Drop status column from user_lesson_progress (if you want to keep existing rows, skip this)
ALTER TABLE user_lesson_progress DROP COLUMN IF EXISTS status;

-- Create index for lesson status if helpful
CREATE INDEX IF NOT EXISTS idx_lessons_status ON lessons (status);
