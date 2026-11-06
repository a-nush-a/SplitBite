-- ===================================================================
-- SplitBite – Smart Restaurant Bill Splitting Database Schema
-- Database: MySQL 8.0+ (with GST, Extra Charges & Bill Attachment)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS splitbite_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE splitbite_db;

-- 1. Bills Table (Stores overall restaurant bill, taxes, and receipt)
CREATE TABLE IF NOT EXISTS bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_name VARCHAR(255) NOT NULL,
    food_subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_percent DECIMAL(5, 2) NOT NULL DEFAULT 5.00,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    service_charge DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    split_type VARCHAR(50) NOT NULL DEFAULT 'EQUAL', -- 'EQUAL' or 'INDIVIDUAL'
    receipt_image_name VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Participants Table (People who shared the meal)
CREATE TABLE IF NOT EXISTS participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    food_subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_share DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    service_share DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_share DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_participant_bill
        FOREIGN KEY (bill_id) REFERENCES bills(id)
        ON DELETE CASCADE
);

-- 3. Food Items Table (Dishes ordered from the restaurant)
CREATE TABLE IF NOT EXISTS food_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_food_item_bill
        FOREIGN KEY (bill_id) REFERENCES bills(id)
        ON DELETE CASCADE
);

-- 4. Food Assignments Table (Maps which participant ate which dish)
CREATE TABLE IF NOT EXISTS food_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_item_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    share_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_assignment_food_item
        FOREIGN KEY (food_item_id) REFERENCES food_items(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_assignment_participant
        FOREIGN KEY (participant_id) REFERENCES participants(id)
        ON DELETE CASCADE
);

-- Indices for high performance lookups
CREATE INDEX idx_participants_bill ON participants(bill_id);
CREATE INDEX idx_food_items_bill ON food_items(bill_id);
CREATE INDEX idx_assignments_item ON food_assignments(food_item_id);
CREATE INDEX idx_assignments_participant ON food_assignments(participant_id);
