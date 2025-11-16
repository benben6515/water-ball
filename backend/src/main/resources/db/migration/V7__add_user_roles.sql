-- Add user roles for RBAC (Role-Based Access Control)
-- Roles: GUEST, STUDENT, TEACHER, ADMIN
-- Default role: STUDENT

-- Add role column to users table
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'STUDENT';

-- Update existing users to have STUDENT role (if any exist)
UPDATE users
SET role = 'STUDENT'
WHERE role IS NULL;

-- Create index on role column for efficient role-based queries
CREATE INDEX idx_users_role ON users(role);

-- Add comment to document the column
COMMENT ON COLUMN users.role IS 'User role for RBAC: GUEST, STUDENT, TEACHER, ADMIN';
