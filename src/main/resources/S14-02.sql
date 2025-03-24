-- Create and use database
CREATE DATABASE IF NOT EXISTS computerrepairshop;
USE computerrepairshop;

-- Set character set
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Drop tables if they exist
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS devices;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS technicians;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
	customerCode INT NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    firstName VARCHAR(100) NOT NULL,
	contactNumber VARCHAR(50),
    address VARCHAR(200),
    CONSTRAINT customer_PK PRIMARY KEY (customerCode)
);

CREATE TABLE technicians (
	technicianID INT NOT NULL,
    lastName VARCHAR(100) NOT NULL,
	firstName VARCHAR(100) NOT NULL,
    contactNumber VARCHAR(50),
    address VARCHAR(200),
    availability VARCHAR(50),
    CONSTRAINT technician_PK PRIMARY KEY (technicianID)
);

CREATE TABLE inventory (
	productCode INT NOT NULL,
    productName VARCHAR(100) NOT NULL,
    quantityInStock INT NOT NULL,
    productStatus VARCHAR(100),
    CONSTRAINT product_PK PRIMARY KEY (productCode)
);

CREATE TABLE inventory_usage (
    usageId INT PRIMARY KEY AUTO_INCREMENT,
    productCode VARCHAR(20) NOT NULL,
    quantityUsed INT NOT NULL,
    usageDate DATE NOT NULL,
    FOREIGN KEY (productCode) REFERENCES inventory(productCode)
);

CREATE TABLE devices (
    deviceID INT NOT NULL,
    customerCode INT NOT NULL,
    deviceType VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    serialNumber VARCHAR(100),
    description VARCHAR(500),
    CONSTRAINT device_PK PRIMARY KEY (deviceID),
    CONSTRAINT device_FK1 FOREIGN KEY (customerCode) REFERENCES customers(customerCode)
);

CREATE TABLE appointments (
	customerCode INT NOT NULL,
    technicianID INT NOT NULL,
    serviceStatus ENUM('Pending', 'In Progress', 'For Pickup', 'Completed', 'Cancelled'),
    dateAndTime TIMESTAMP,
    invoiceNumber INT NOT NULL,
    paymentStatus ENUM('Pending', 'Paid'),
    amountPaid DECIMAL(8,2),
    deviceID INT,
    CONSTRAINT appointment_PK PRIMARY KEY (invoiceNumber),
    CONSTRAINT appointment_FK1 FOREIGN KEY (customerCode) REFERENCES customers(customerCode),
    CONSTRAINT appointment_FK2 FOREIGN KEY (technicianID) REFERENCES technicians(technicianID),
    CONSTRAINT appointment_FK3 FOREIGN KEY (deviceID) REFERENCES devices(deviceID)
);

/* 
	RECORDS
*/

-- Customers
INSERT INTO customers VALUES
(1001, 'Grino', 'Gem', '09171234567', 'Makati City'),
(1002, 'Cruz', 'Maria', '09189876543', 'Quezon City'),
(1003, 'Reyes', 'Antonio', '09209999999', 'Manila'),
(1004, 'Garcia', 'Sofia', '09161111111', 'Pasig City'),
(1005, 'Lim', 'David', '09208888888', 'Mandaluyong City'),
(1006, 'Tan', 'Angela', '09175555555', 'Taguig City'),
(1007, 'Mendoza', 'Ramon', '09189999999', 'Pasay City'),
(1008, 'Wong', 'Linda', '09207777777', 'Makati City');

-- Technicians
INSERT INTO technicians VALUES
(101, 'Grino', 'Gem', '09151234567', 'Las Piñas City', 'Available'),
(102, 'Maristela', 'Kyle', '09169876543', 'Parañaque City', 'Available'),
(103, 'Yang', 'Henry', '09203333333', 'Makati City', 'Available'),
(104, 'Jacela', 'Eugo', '09184444444', 'Quezon City', 'Available'),
(105, 'Aquino', 'Miguel', '09172222222', 'Manila', 'Available');

-- Devices
INSERT INTO devices VALUES
(1, 1001, 'Laptop', 'Dell', 'Inspiron 15', 'SN123456', 'Battery not charging'),
(2, 1002, 'Desktop', 'HP', 'Pavilion', 'SN234567', 'Slow performance'),
(3, 1003, 'Laptop', 'Lenovo', 'ThinkPad', 'SN345678', 'Screen flickering'),
(4, 1004, 'Desktop', 'Acer', 'Aspire', 'SN456789', 'No power'),
(5, 1005, 'Laptop', 'ASUS', 'ROG', 'SN567890', 'Overheating'),
(6, 1006, 'Desktop', 'Apple', 'iMac', 'SN678901', 'Software issues'),
(7, 1007, 'Laptop', 'MSI', 'GF63', 'SN789012', 'Keyboard not working'),
(8, 1001, 'Desktop', 'Custom', 'Gaming PC', 'SN890123', 'GPU problems'),
(9, 1002, 'Laptop', 'Acer', 'Swift 3', 'SN901234', 'WiFi issues'),
(10, 1003, 'Desktop', 'Lenovo', 'IdeaCentre', 'SN012345', 'HDD failure');

-- Inventory
INSERT INTO inventory (productCode, productName, quantityInStock, productStatus) VALUES
(1, 'RAM DDR4 8GB', 15, 'In Stock'),
(2, 'SSD 500GB', 20, 'In Stock'),
(3, 'Power Supply 650W', 10, 'In Stock'),
(4, 'Thermal Paste', 50, 'In Stock'),
(5, 'GPU RTX 3060', 5, 'In Stock'),
(6, 'CPU i5 12th Gen', 8, 'In Stock'),
(7, 'Laptop Battery', 12, 'In Stock'),
(8, 'Keyboard', 25, 'In Stock'),
(9, 'Mouse', 30, 'In Stock'),
(10, 'Monitor 24"', 7, 'In Stock');

-- Appointments
INSERT INTO appointments (customerCode, technicianID, serviceStatus, dateAndTime, invoiceNumber, paymentStatus, amountPaid, deviceID) VALUES
(1001, 101, 'Completed', '2023-01-15 10:00:00', 1001, 'Paid', 2500.00, 1),
(1002, 102, 'Completed', '2023-03-20 14:30:00', 1002, 'Paid', 3000.00, 2),
(1003, 103, 'Completed', '2023-05-10 11:15:00', 1003, 'Paid', 1800.00, 3),
(1004, 104, 'Completed', '2023-07-05 09:45:00', 1004, 'Paid', 4500.00, 4),
(1005, 105, 'Completed', '2023-09-12 13:20:00', 1005, 'Paid', 3500.00, 5),
(1006, 101, 'Completed', '2023-11-25 15:00:00', 1006, 'Paid', 2800.00, 6),
(1007, 102, 'Completed', '2024-01-08 10:30:00', 1007, 'Paid', 1500.00, 7),
(1001, 103, 'In Progress', '2024-02-14 14:00:00', 1008, 'Pending', 0.00, 8),
(1002, 104, 'Pending', '2024-03-01 11:30:00', 1009, 'Pending', 0.00, 9),
(1003, 105, 'For Pickup', '2024-02-28 16:15:00', 1010, 'Paid', 2200.00, 10),
(1004, 101, 'In Progress', '2024-03-05 09:00:00', 1011, 'Pending', 0.00, 4),
(1005, 102, 'Pending', '2024-03-10 13:45:00', 1012, 'Pending', 0.00, 5);
