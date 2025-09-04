DROP DATABASE IF EXISTS hyo_banking_db;
CREATE DATABASE hyo_banking_db;
USE hyo_banking_db;

DROP USER IF EXISTS 'hyo'@'localhost';
CREATE USER 'hyo'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON hyo_banking_db.* TO 'hyo'@'localhost';
FLUSH PRIVILEGES;