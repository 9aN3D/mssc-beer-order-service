DROP DATABASE IF EXISTS beerorderservice;
DROP USER IF EXISTS `beer_order_service`@`%`;
CREATE DATABASE IF NOT EXISTS beerorderservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `beer_order_service`@`%` IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON `beerorderservice`.* TO `beer_order_service`@`%`;
FLUSH PRIVILEGES;
