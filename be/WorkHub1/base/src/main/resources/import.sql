-- Insert roles
INSERT INTO tbl_roles (id, name, description) VALUES (1, 'ROLE_ADMIN', 'Administrator')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO tbl_roles (id, name, description) VALUES (2, 'ROLE_RECRUITER', 'Recruiter')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO tbl_roles (id, name, description) VALUES (3, 'ROLE_CANDIDATE', 'Candidate')
ON DUPLICATE KEY UPDATE name = name;
