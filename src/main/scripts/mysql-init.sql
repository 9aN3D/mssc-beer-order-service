DROP DATABASE IF EXISTS beerorderservice;
DROP USER IF EXISTS `beerorderservice`@`%`;
CREATE DATABASE IF NOT EXISTS beerorderservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `beerorderservice`@`%` IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON `beerorderservice`.* TO `beerorderservice`@`%`;
FLUSH PRIVILEGES;
