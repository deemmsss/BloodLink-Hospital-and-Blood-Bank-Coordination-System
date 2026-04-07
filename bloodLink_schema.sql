-- ══════════════════════════════════════
-- BloodLink Database Schema
-- ══════════════════════════════════════

DROP DATABASE IF EXISTS bloodlink;
CREATE DATABASE bloodlink;
USE bloodlink;

-- ──────────────────────────────────────
-- USERS TABLE
-- Stores all 4 roles in a single table.
-- The 'role' column determines which
-- dashboard and permissions apply.
-- ──────────────────────────────────────
CREATE TABLE users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    role            ENUM('HOSPITAL_NURSE', 'HOSPITAL_ADMIN', 'BLOOD_BANK_TECHNICIAN', 'BLOOD_BANK_ADMIN') NOT NULL,
    department      VARCHAR(100) DEFAULT NULL,       -- only used by nurses
    lab_certification VARCHAR(100) DEFAULT NULL,     -- only used by technicians
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ──────────────────────────────────────
-- PATIENTS TABLE
-- Managed by Hospital Nurses.
-- ──────────────────────────────────────
CREATE TABLE patients (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE NOT NULL,
    blood_type      ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    ward            VARCHAR(50)  NOT NULL,
    medical_notes   TEXT,
    nurse_id        INT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nurse_id) REFERENCES users(id)
);

-- ──────────────────────────────────────
-- DONORS TABLE
-- Managed by Blood Bank Admins.
-- ──────────────────────────────────────
CREATE TABLE donors (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(100) NOT NULL,
    date_of_birth       DATE NOT NULL,
    blood_type          ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    phone               VARCHAR(20)  NOT NULL,
    last_donation_date  DATE DEFAULT NULL,
    eligible_to_donate  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ──────────────────────────────────────
-- BLOOD UNITS TABLE
-- Each row is one unit of blood in the
-- Blood Bank's inventory, linked to its donor.
-- ──────────────────────────────────────
CREATE TABLE blood_units (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    blood_type      ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    collection_date DATE NOT NULL,
    expiry_date     DATE NOT NULL,
    donor_id        INT NOT NULL,
    available       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(id)
);

-- ──────────────────────────────────────
-- BLOOD REQUESTS TABLE
-- Created by Nurses, processed by the Blood Bank.
-- ──────────────────────────────────────
CREATE TABLE blood_requests (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT NOT NULL,
    nurse_id        INT NOT NULL,
    blood_type      ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    units_requested INT NOT NULL DEFAULT 1,
    status          ENUM('PENDING','APPROVED','REJECTED','FULFILLED','SHIPPED','RECEIVED') NOT NULL DEFAULT 'PENDING',
    priority        ENUM('ROUTINE','URGENT','EMERGENCY') NOT NULL DEFAULT 'ROUTINE',
    notes           TEXT,
    request_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (nurse_id)   REFERENCES users(id)
);

-- ──────────────────────────────────────
-- SHIPMENTS TABLE
-- Created by Technicians when fulfilling
-- a blood request.
-- ──────────────────────────────────────
CREATE TABLE shipments (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    request_id      INT NOT NULL UNIQUE,
    technician_id   INT NOT NULL,
    ship_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    received_date   TIMESTAMP NULL DEFAULT NULL,
    confirmed       BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (request_id)    REFERENCES blood_requests(id),
    FOREIGN KEY (technician_id) REFERENCES users(id)
);

-- ──────────────────────────────────────
-- SHIPMENT_ITEMS TABLE
-- Junction table: which blood units are
-- included in a shipment.
-- ──────────────────────────────────────
CREATE TABLE shipment_items (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    shipment_id     INT NOT NULL,
    blood_unit_id   INT NOT NULL,
    FOREIGN KEY (shipment_id)  REFERENCES shipments(id),
    FOREIGN KEY (blood_unit_id) REFERENCES blood_units(id)
);

-- ──────────────────────────────────────
-- INVENTORY THRESHOLDS TABLE
-- Configured by Blood Bank Admin.
-- One row per blood type defining the
-- minimum acceptable stock level.
-- ──────────────────────────────────────
CREATE TABLE inventory_thresholds (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    blood_type      ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL UNIQUE,
    minimum_units   INT NOT NULL DEFAULT 5
);

-- ══════════════════════════════════════
-- SEED DATA
-- ══════════════════════════════════════

-- Hospital Nurse
INSERT INTO users (username, password, full_name, email, role, department)
VALUES ('nurse1', 'password', 'Sarah Johnson', 'sarah@hospital.com', 'HOSPITAL_NURSE', 'Emergency');

-- Hospital Admin
INSERT INTO users (username, password, full_name, email, role)
VALUES ('hadmin1', 'password', 'Dr. Michael Chen', 'mchen@hospital.com', 'HOSPITAL_ADMIN');

-- Blood Bank Technician
INSERT INTO users (username, password, full_name, email, role, lab_certification)
VALUES ('tech1', 'password', 'Emily Davis', 'emily@bloodbank.com', 'BLOOD_BANK_TECHNICIAN', 'ASCP-BB');

-- Blood Bank Admin
INSERT INTO users (username, password, full_name, email, role)
VALUES ('bbadmin1', 'password', 'James Wilson', 'james@bloodbank.com', 'BLOOD_BANK_ADMIN');

-- Default inventory thresholds (one per blood type)
INSERT INTO inventory_thresholds (blood_type, minimum_units) VALUES
('A_POSITIVE', 10),
('A_NEGATIVE', 5),
('B_POSITIVE', 8),
('B_NEGATIVE', 5),
('AB_POSITIVE', 5),
('AB_NEGATIVE', 3),
('O_POSITIVE', 15),
('O_NEGATIVE', 10);