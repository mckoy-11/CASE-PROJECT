-- wcms_schema.sql
-- Normalized WCMS database schema in Third Normal Form (3NF)
-- Includes lookup/reference tables, foreign keys, and sample parent table data.

DROP DATABASE IF EXISTS wcms;
CREATE DATABASE wcms;
USE wcms;

CREATE TABLE role_lookup (
    role_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_key VARCHAR(50) NOT NULL UNIQUE,
    role_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE gender_lookup (
    gender_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    gender_key VARCHAR(50) NOT NULL UNIQUE,
    gender_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE status_domain (
    status_domain_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    domain_key VARCHAR(50) NOT NULL UNIQUE,
    domain_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE status_lookup (
    status_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    status_domain_id INT NOT NULL,
    status_key VARCHAR(50) NOT NULL,
    status_label VARCHAR(100) NOT NULL,
    UNIQUE KEY ux_status_domain_key (status_domain_id, status_key),
    FOREIGN KEY (status_domain_id) REFERENCES status_domain(status_domain_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE complaint_type (
    complaint_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type_key VARCHAR(50) NOT NULL UNIQUE,
    type_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE report_type (
    report_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type_key VARCHAR(50) NOT NULL UNIQUE,
    type_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE request_type (
    request_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type_key VARCHAR(50) NOT NULL UNIQUE,
    type_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE truck_type_lookup (
    truck_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    truck_type_key VARCHAR(50) NOT NULL UNIQUE,
    truck_type_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE entry_type_lookup (
    entry_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entry_type_key VARCHAR(50) NOT NULL UNIQUE,
    entry_type_label VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- Core tables
CREATE TABLE account (
    account_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status_id INT NOT NULL,
    role_id INT NOT NULL,
    is_barangay_setup_complete TINYINT(1) NOT NULL DEFAULT 0,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role_lookup(role_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE barangay (
    barangay_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_name VARCHAR(255) NOT NULL,
    barangay_household INT NOT NULL DEFAULT 0,
    purok_count INT NOT NULL DEFAULT 0,
    population INT NOT NULL DEFAULT 0,
    contact VARCHAR(50) NULL,
    collection_day VARCHAR(50) NULL,
    status_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE barangay_admin (
    barangay_admin_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_id INT NULL,
    account_id INT NOT NULL,
    admin_name VARCHAR(255) NULL,
    age INT NULL,
    gender_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY ux_barangay_admin_account (account_id),
    FOREIGN KEY (barangay_id) REFERENCES barangay(barangay_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (account_id) REFERENCES account(account_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES gender_lookup(gender_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE announcement (
    announcement_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NULL,
    message TEXT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    is_archived TINYINT(1) NOT NULL DEFAULT 0,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    archived_at TIMESTAMP NULL
) ENGINE=InnoDB;

CREATE TABLE complaint (
    complaint_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_id INT NULL,
    complaint_type_id INT NULL,
    message TEXT NOT NULL,
    proof LONGBLOB NULL,
    status_id INT NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    is_archived TINYINT(1) NOT NULL DEFAULT 0,
    archived_at TIMESTAMP NULL,
    location VARCHAR(255) NULL,
    response_message TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (barangay_id) REFERENCES barangay(barangay_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (complaint_type_id) REFERENCES complaint_type(complaint_type_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE report (
    report_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_id INT NULL,
    report_type_id INT NULL,
    message TEXT NOT NULL,
    proof LONGBLOB NULL,
    status_id INT NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    is_archived TINYINT(1) NOT NULL DEFAULT 0,
    archived_at TIMESTAMP NULL,
    response_message TEXT NULL,
    purok_analytics TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (barangay_id) REFERENCES barangay(barangay_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (report_type_id) REFERENCES report_type(report_type_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE request (
    request_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_id INT NULL,
    request_type_id INT NULL,
    message TEXT NOT NULL,
    proof LONGBLOB NULL,
    status_id INT NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    is_archived TINYINT(1) NOT NULL DEFAULT 0,
    archived_at TIMESTAMP NULL,
    location VARCHAR(255) NULL,
    response_message TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (barangay_id) REFERENCES barangay(barangay_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (request_type_id) REFERENCES request_type(request_type_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE personnel (
    personnel_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    personnel_name VARCHAR(255) NOT NULL,
    age INT NULL,
    gender_id INT NULL,
    address VARCHAR(255) NULL,
    contact_number VARCHAR(50) NULL,
    team_id INT NULL,
    role_id INT NOT NULL,
    status_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role_lookup(role_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES gender_lookup(gender_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE truck (
    truck_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(50) NOT NULL UNIQUE,
    truck_type_id INT NOT NULL,
    capacity VARCHAR(100) NULL,
    status_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (truck_type_id) REFERENCES truck_type_lookup(truck_type_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE team (
    team_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(255) NOT NULL,
    leader_id INT NULL,
    driver_id INT NULL,
    truck_id INT NULL,
    status_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (truck_id) REFERENCES truck(truck_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE team_collectors (
    team_id INT NOT NULL,
    personnel_id INT NOT NULL,
    PRIMARY KEY (team_id, personnel_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (personnel_id) REFERENCES personnel(personnel_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE schedule (
    schedule_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_id INT NOT NULL,
    team_id INT NOT NULL,
    schedule_date DATE NOT NULL,
    schedule_time TIME NOT NULL,
    status_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (barangay_id) REFERENCES barangay(barangay_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (team_id) REFERENCES team(team_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (status_id) REFERENCES status_lookup(status_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE purok_checklist (
    checklist_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    barangay_id INT NOT NULL,
    purok_name VARCHAR(100) NOT NULL,
    is_collected TINYINT(1) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (barangay_id) REFERENCES barangay(barangay_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE entry_attachment (
    attachment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entry_type_id INT NOT NULL,
    entry_id INT NOT NULL,
    image_blob LONGBLOB NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (entry_type_id) REFERENCES entry_type_lookup(entry_type_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Indexes
CREATE INDEX idx_account_status ON account(status_id);
CREATE INDEX idx_account_role ON account(role_id);
CREATE INDEX idx_barangay_status ON barangay(status_id);
CREATE INDEX idx_complaint_barangay ON complaint(barangay_id);
CREATE INDEX idx_report_barangay ON report(barangay_id);
CREATE INDEX idx_request_barangay ON request(barangay_id);
CREATE INDEX idx_personnel_status ON personnel(status_id);
CREATE INDEX idx_personnel_role ON personnel(role_id);
CREATE INDEX idx_team_status ON team(status_id);
CREATE INDEX idx_schedule_date ON schedule(schedule_date);
CREATE INDEX idx_purok_barangay ON purok_checklist(barangay_id);

-- Add circular foreign key constraints
ALTER TABLE personnel ADD CONSTRAINT fk_personnel_team 
    FOREIGN KEY (team_id) REFERENCES team(team_id) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE team ADD CONSTRAINT fk_team_leader 
    FOREIGN KEY (leader_id) REFERENCES personnel(personnel_id) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE team ADD CONSTRAINT fk_team_driver 
    FOREIGN KEY (driver_id) REFERENCES personnel(personnel_id) ON DELETE SET NULL ON UPDATE CASCADE;


-- Roles
INSERT INTO role_lookup (role_id, role_key, role_label) VALUES
(1, 'MENRO', 'MENRO'),
(2, 'BARANGAY', 'Barangay Admin'),
(3, 'PERSONNEL', 'Personnel'),
(4, 'COLLECTOR', 'Collector'),
(5, 'DRIVER', 'Driver');

-- Genders
INSERT INTO gender_lookup (gender_id, gender_key, gender_label) VALUES
(1, 'MALE', 'Male'),
(2, 'FEMALE', 'Female'),
(3, 'OTHER', 'Other');

-- Status domains
INSERT INTO status_domain (status_domain_id, domain_key, domain_label) VALUES
(1, 'ACCOUNT', 'Account Status'),
(2, 'BARANGAY', 'Barangay Status'),
(3, 'COMPLAINT', 'Complaint Status'),
(4, 'REPORT', 'Report Status'),
(5, 'REQUEST', 'Request Status'),
(6, 'TRUCK', 'Truck Status'),
(7, 'TEAM', 'Team Status'),
(8, 'SCHEDULE', 'Schedule Status'),
(9, 'ANNOUNCEMENT', 'Announcement Status'),
(10, 'PERSONNEL', 'Personnel Status');

-- Status values
INSERT INTO status_lookup (status_id, status_domain_id, status_key, status_label) VALUES
(1, 1, 'ACTIVE', 'Active'),
(2, 1, 'INACTIVE', 'Inactive'),
(3, 1, 'SUSPENDED', 'Suspended'),
(4, 2, 'ACTIVE', 'Active'),
(5, 2, 'SCHEDULED', 'Scheduled'),
(6, 2, 'INACTIVE', 'Inactive'),
(7, 3, 'PENDING', 'Pending'),
(8, 3, 'UNDER_REVIEW', 'Under Review'),
(9, 3, 'RESOLVED', 'Resolved'),
(10, 3, 'CLOSED', 'Closed'),
(11, 4, 'PENDING', 'Pending'),
(12, 4, 'COMPLETED', 'Completed'),
(13, 4, 'REJECTED', 'Rejected'),
(14, 5, 'PENDING', 'Pending'),
(15, 5, 'APPROVED', 'Approved'),
(16, 5, 'DENIED', 'Denied'),
(17, 5, 'COMPLETED', 'Completed'),
(18, 6, 'ACTIVE', 'Active'),
(19, 6, 'IN_MAINTENANCE', 'In Maintenance'),
(20, 6, 'DECOMMISSIONED', 'Decommissioned'),
(21, 7, 'ACTIVE', 'Active'),
(22, 7, 'INACTIVE', 'Inactive'),
(23, 7, 'SCHEDULED', 'Scheduled'),
(24, 8, 'SCHEDULED', 'Scheduled'),
(25, 8, 'COMPLETED', 'Completed'),
(26, 8, 'CANCELLED', 'Cancelled'),
(27, 8, 'MISSED', 'Missed'),
(28, 9, 'ACTIVE', 'Active'),
(29, 9, 'ARCHIVED', 'Archived'),
(30, 9, 'EXPIRED', 'Expired'),
(31, 10, 'ACTIVE', 'Active'),
(32, 10, 'UNASSIGNED', 'Unassigned'),
(33, 10, 'ON_DUTY', 'On Duty'),
(34, 10, 'OFF_DUTY', 'Off Duty');

-- Built-in admin account
INSERT INTO account (account_id, name, email_address, password, status_id, role_id, is_barangay_setup_complete)
VALUES (1, 'Menro Admin', 'admin@municipal.gov', 'admin123', 1, 1, 1);

-- Complaint types
INSERT INTO complaint_type (complaint_type_id, type_key, type_label) VALUES
(1, 'WASTE_COLLECTION', 'Waste Collection'),
(2, 'DUMPING', 'Illegal Dumping'),
(3, 'RECYCLING', 'Recycling'),
(4, 'ENVIRONMENTAL', 'Environmental Issue'),
(5, 'OTHER', 'Other');

-- Report types
INSERT INTO report_type (report_type_id, type_key, type_label) VALUES
(1, 'WASTE_REPORT', 'Waste Report'),
(2, 'CLEANUP_REPORT', 'Cleanup Report'),
(3, 'AUDIT_REPORT', 'Audit Report'),
(4, 'MAINTENANCE_REPORT', 'Maintenance Report'),
(5, 'OTHER', 'Other');

-- Request types
INSERT INTO request_type (request_type_id, type_key, type_label) VALUES
(1, 'PICKUP_REQUEST', 'Pickup Request'),
(2, 'DISPOSAL_REQUEST', 'Disposal Request'),
(3, 'CLEANUP_REQUEST', 'Cleanup Request'),
(4, 'REPAIR_REQUEST', 'Repair Request'),
(5, 'OTHER', 'Other');

-- Truck types
INSERT INTO truck_type_lookup (truck_type_id, truck_type_key, truck_type_label) VALUES
(1, 'DUMP_TRUCK', 'Dump Truck'),
(2, 'FLATBED', 'Flatbed Truck'),
(3, 'CRANE', 'Crane Truck'),
(4, 'COMPACTOR', 'Compactor Truck'),
(5, 'UTILITY', 'Utility Truck');

-- Entry types
INSERT INTO entry_type_lookup (entry_type_id, entry_type_key, entry_type_label) VALUES
(1, 'COMPLAINT', 'Complaint'),
(2, 'REPORT', 'Report'),
(3, 'REQUEST', 'Request');