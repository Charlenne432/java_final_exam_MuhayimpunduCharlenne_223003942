-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 08:41 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `transport_analytic_system_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `drivers`
--

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE IF NOT EXISTS `drivers` (
  `driver_id` int NOT NULL AUTO_INCREMENT,
  `names` varchar(255) DEFAULT NULL,
  `driving_licence` varchar(255) DEFAULT NULL,
  `experience` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `contact` int DEFAULT NULL,
  PRIMARY KEY (`driver_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `drivers`
--

INSERT INTO `drivers` (`driver_id`, `names`, `driving_licence`, `experience`, `created_at`, `contact`) VALUES
(1, 'John', 'A1234567', '2 years experience', '2025-10-23 11:43:16', 788654562),
(2, 'Kabera', 'B9876543', '10 years experience', '2025-10-23 11:43:16', 789865434),
(3, 'Michael', 'C5551111', '5 years experience', '2025-10-23 11:43:16', 799987645),
(4, 'James', 'D6523456', '1 year experience', '2025-11-06 15:34:58', 788889860);

-- --------------------------------------------------------

--
-- Table structure for table `maintenance`
--

DROP TABLE IF EXISTS `maintenance`;
CREATE TABLE IF NOT EXISTS `maintenance` (
  `maintenance_id` int NOT NULL AUTO_INCREMENT,
  `reference_id` varchar(255) DEFAULT NULL,
  `description` text,
  `date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `cost` text,
  `driver_id` int DEFAULT NULL,
  `vehicle_id` int DEFAULT NULL,
  `ticket_id` int DEFAULT NULL,
  PRIMARY KEY (`maintenance_id`),
  KEY `driver_id` (`driver_id`),
  KEY `vehicle_id` (`vehicle_id`),
  KEY `ticket_id` (`ticket_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `maintenance`
--

INSERT INTO `maintenance` (`maintenance_id`, `reference_id`, `description`, `date`, `status`, `cost`, `driver_id`, `vehicle_id`, `ticket_id`) VALUES
(1, 'MNT-9001', 'Oil filter and tire change', '2025-12-25', 'Completed', 'RWF 10000', 1, 1, 1),
(2, 'MNT-9002', 'Engine tune-up', '2025-10-27', 'In Progress', 'RWF 10000', 2, 3, 2),
(3, 'MNT-9003', 'Brake inspection', '2025-11-03', 'Completed', 'FRW 15000', 3, 2, 3);

-- --------------------------------------------------------

--
-- Table structure for table `route`
--

DROP TABLE IF EXISTS `route`;
CREATE TABLE IF NOT EXISTS `route` (
  `route_id` int NOT NULL AUTO_INCREMENT,
  `location` varchar(255) DEFAULT NULL,
  `distance` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `route`
--

INSERT INTO `route` (`route_id`, `location`, `distance`, `time`, `created_at`) VALUES
(1, 'Kigali - Musanze', '120km', '2 hours', '2025-10-23 11:47:51'),
(2, 'Huye - Rubavu', '160km', '3.5 hours', '2025-10-23 11:47:51'),
(3, 'Kigali - Butare', '135km', '2.5 hours', '2025-10-23 11:47:51');

-- --------------------------------------------------------

--
-- Table structure for table `route_ticket`
--

DROP TABLE IF EXISTS `route_ticket`;
CREATE TABLE IF NOT EXISTS `route_ticket` (
  `route_id` int NOT NULL,
  `ticket_id` int NOT NULL,
  PRIMARY KEY (`route_id`,`ticket_id`),
  KEY `ticket_id` (`ticket_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `route_ticket`
--

INSERT INTO `route_ticket` (`route_id`, `ticket_id`) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 3),
(3, 2),
(3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
CREATE TABLE IF NOT EXISTS `ticket` (
  `ticket_id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(255) DEFAULT NULL,
  `francs` varchar(255) DEFAULT NULL,
  `PW` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ticket_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `ticket`
--

INSERT INTO `ticket` (`ticket_id`, `status`, `francs`, `PW`, `created_at`) VALUES
(1, 'Regular', 'RWF 5000', 'One way', '2025-10-23 11:48:31'),
(2, 'VIP', 'RWF 7000', 'One way', '2025-10-23 11:48:31'),
(3, 'Student', 'RWF 3000', 'Discounted', '2025-10-23 11:48:31'),
(4, 'Regular', 'RWF 4500', 'One way', '2025-11-06 17:08:09');

-- --------------------------------------------------------

--
-- Table structure for table `trip`
--

DROP TABLE IF EXISTS `trip`;
CREATE TABLE IF NOT EXISTS `trip` (
  `trip_id` int NOT NULL AUTO_INCREMENT,
  `order_number` varchar(100) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `total_amount` decimal(12,2) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `route_id` int DEFAULT NULL,
  `driver_id` int DEFAULT NULL,
  `vehicle_id` int DEFAULT NULL,
  PRIMARY KEY (`trip_id`),
  KEY `driver_id` (`driver_id`),
  KEY `vehicle_id` (`vehicle_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `trip`
--

INSERT INTO `trip` (`trip_id`, `order_number`, `date`, `status`, `total_amount`, `payment_method`, `route_id`, `driver_id`, `vehicle_id`) VALUES
(1, 'ORD-0011', '2025-10-27', 'Completed', 15000.00, 'Cash', 1, 1, 1),
(2, 'ORD-0032', '2025-11-03', 'Ongoing', 20000.00, 'Mobile Money', 2, 2, 2),
(3, 'ORD-0103', '2025-12-24', 'Pending', 18000.00, 'Credit', 3, 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`, `created_at`) VALUES
(1, 'Charlenne', 'admin14', 'ADMIN', '2025-10-23 11:30:52'),
(2, 'CRT', 'manager123', 'MANAGER', '2025-10-23 11:30:52'),
(3, 'STAFF', 'staff123', 'STAFF', '2025-10-23 11:30:52');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE IF NOT EXISTS `vehicles` (
  `vehicle_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `assigned_since` date DEFAULT NULL,
  PRIMARY KEY (`vehicle_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`vehicle_id`, `name`, `identifier`, `status`, `location`, `assigned_since`) VALUES
(1, 'Toyota Hiace', 'VH-001', 'Active', 'Kigali Depot', '2025-01-15'),
(2, 'Nissan Caravan', 'VH-002', 'INACTIVE', 'Gisenyi Depot', '2023-03-10'),
(3, 'Isuzu Bus', 'VH-003', 'Under Maintenance', 'Butare Depot', '2021-11-20'),
(4, 'BMW', 'VH-004', 'ACTIVE', 'Kigali Depot', '2025-11-06');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
