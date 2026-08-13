# Online Reservation System

A complete, full-screen desktop application for train ticket booking, reservation management, and role-based administration built with **Java 21**, **Java Swing**, **JDBC**, and **MySQL**.

---

## 🌟 Key Features

- **Full-Screen Desktop Architecture**: Modern single-window GUI using `CardLayout` (`setExtendedState(JFrame.MAXIMIZED_BOTH)`), FlatLaf dark look-and-feel, and responsive layout.
- **Dynamic Database Integration**:
  - Trains loaded dynamically from MySQL into a `JComboBox<Train>`.
  - Auto-population of read-only train names and station routes.
  - Real-time dashboard statistics calculated via live MySQL queries (`Active Trains`, `Total Bookings`, `Your Bookings`).
- **Role-Based Access Control (RBAC)**:
  - **`USER` Role**: View and search **only** own reservations (`WHERE user_id = ?`). PNR lookup and cancellation are secured at the SQL layer to prevent unauthorized access.
  - **`ADMIN` Role**: View, search, and manage all system reservations across all users with username attribution.
- **Interactive Reservation Management**:
  - Interactive ticket booking with automatic unique 10-digit PNR generation.
  - Formatted booking confirmation modal.
  - `JTable` view with custom headers, alternating row colors, text searching/filtering, and a **Refresh** button.
  - PNR cancellation with ownership checks and confirmation dialogs.
- **Input & Date Validation**:
  - Strict journey date validation using `java.time.LocalDate` / `DateTimeFormatter` (rejects past dates).
  - Required fields, numeric checks, and source/destination station inequality checks.
  - Form resetting and clear buttons across all screens.

---

## 🏗️ Architecture & Project Structure

The project follows a clean separation of concerns (UI → Service → DAO → Config → MySQL):

```text
Java-Task1-OnlineReservationSystem/
├── pom.xml
├── database/
│   ├── schema.sql
│   └── migration.sql
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── oasis/
│   │               ├── Main.java
│   │               ├── config/
│   │               │   └── DatabaseConnection.java
│   │               ├── dao/
│   │               │   ├── UserDAO.java
│   │               │   ├── TrainDAO.java
│   │               │   └── ReservationDAO.java
│   │               ├── model/
│   │               │   ├── User.java
│   │               │   ├── Train.java
│   │               │   └── Reservation.java
│   │               ├── service/
│   │               │   ├── AuthenticationService.java
│   │               │   └── ReservationService.java
│   │               ├── ui/
│   │               │   ├── LoginFrame.java
│   │               │   ├── MainFrame.java
│   │               │   ├── DashboardPanel.java
│   │               │   ├── ReservationPanel.java
│   │               │   ├── ViewReservationsPanel.java
│   │               │   └── CancellationPanel.java
│   │               └── util/
│   │                   ├── PNRGenerator.java
│   │                   └── ValidationUtil.java
│   └── test/
│       └── java/
│           └── com/
│               └── oasis/
│                   └── ReservationSystemTest.java
└── target/
    └── online-reservation-system-1.0.0-jar-with-dependencies.jar
```

---

## 🛠️ Technology Stack

- **Language**: Java 21 (LTS)
- **GUI Framework**: Java Swing (with FlatLaf 3.4.1 Look and Feel)
- **Database**: MySQL 8.0
- **Connectivity**: JDBC (`mysql-connector-j:8.3.0`)
- **Build Tool**: Apache Maven
- **Testing**: JUnit 5

---

## 🗄️ Database Setup

1. Make sure MySQL Server is running locally on port `3306`.
2. Import the schema script into MySQL:

```cmd
mysql -u root -p < database/schema.sql
```

The database `online_reservation` will be created with tables (`users`, `trains`, `reservations`), sample trains, and pre-configured test users.

---

## 🔑 Pre-Configured Test Accounts

| Username | Password | Role | Full Name |
|---|---|---|---|
| `admin` | `admin123` | `ADMIN` | System Administrator |
| `user` | `user123` | `USER` | Passenger User |
| `tanuj` | `tanuj123` | `USER` | Tanuj Pratap Singh |

---

## 🚀 How to Build and Run

### Option 1: Run Executable JAR
```bash
mvn clean package
java -jar target/online-reservation-system-1.0.0-jar-with-dependencies.jar
```

### Option 2: Run via Maven
```bash
mvn compile exec:java
```

### Option 3: Run Test Suite
```bash
mvn test
```

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
