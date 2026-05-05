# 📚 Library Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17%2B-blue?style=for-the-badge&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-orange?style=for-the-badge&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Maven%2FGradle-yellow?style=for-the-badge&logo=apache-maven)

A complete Library Management System with book inventory, user management, and loan tracking — built on a clean layered architecture with both console and GUI interfaces.

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Database Setup](#-database-setup)
- [Sample Data](#-sample-data)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### Core Modules

| Module | Description |
|--------|-------------|
| 📖 **Book Management** | Full CRUD operations with real-time availability tracking |
| 👥 **User Management** | 4 user types (Student, Faculty, Staff, Visitor) with role-based privileges |
| 🔁 **Loan System** | Automatic due date calculation and fine management |
| 📊 **Reporting** | Popular books, overdue loans, and user activity reports |

### Technical Highlights

- ✅ Layered architecture (DAO → Service → UI)
- ✅ Input validation and comprehensive error handling
- ✅ Both Console and GUI interfaces
- ✅ Comprehensive unit testing suite
- ✅ MySQL relational database backend

---

## 🏗 Architecture

### Component Diagram

```
┌──────────────┐    ┌──────────────┐
│  Console UI  │    │     GUI      │
└──────┬───────┘    └──────┬───────┘
       │                   │
       └────────┬──────────┘
                ▼
       ┌─────────────────┐
       │  Business Logic │
       └────────┬────────┘
                ▼
       ┌─────────────────┐
       │   Data Access   │
       └────────┬────────┘
                ▼
       ┌─────────────────┐
       │  MySQL Database │
       └─────────────────┘
```

---

## 🗄 Database Setup

### Main Tables

```sql
CREATE TABLE genres (
    genre_id    VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE authors (
    author_id   VARCHAR(20) PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    nationality VARCHAR(100),
    biography   TEXT
);

CREATE TABLE books (
    isbn               VARCHAR(20) PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    publication_date   DATE,
    quantity           INT DEFAULT 1,
    available_quantity INT DEFAULT 1,
    publisher          VARCHAR(100),
    edition            INT,
    description        TEXT,
    language           VARCHAR(50),
    page_count         INT,
    is_reference_only  BOOLEAN DEFAULT FALSE,
    genre_id           VARCHAR(20),
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE users (
    user_id           VARCHAR(20) PRIMARY KEY,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    email             VARCHAR(255),
    phone_number      VARCHAR(20),
    registration_date DATE NOT NULL,
    date_of_birth     DATE,
    address           TEXT,
    user_type         ENUM('STUDENT','FACULTY','STAFF','VISITOR') NOT NULL,
    max_books_allowed INT NOT NULL,
    fines             DECIMAL(10,2) DEFAULT 0.00,
    is_active         BOOLEAN DEFAULT TRUE,
    password_hash     VARCHAR(255) NOT NULL
);

CREATE TABLE loans (
    loan_id     VARCHAR(20) PRIMARY KEY,
    book_isbn   VARCHAR(20) NOT NULL,
    user_id     VARCHAR(20) NOT NULL,
    loan_date   DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE,
    status      ENUM('ACTIVE','RETURNED','OVERDUE','LOST') NOT NULL,
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (book_isbn) REFERENCES books(isbn),
    FOREIGN KEY (user_id)   REFERENCES users(user_id)
);

-- Performance indexes
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_books_title  ON books(title);
```

---

## 🌱 Sample Data

```sql
-- Genres
INSERT INTO genres VALUES
    ('GEN001', 'Fiction', 'Imaginative narratives'),
    ('GEN002', 'Science Fiction', 'Futuristic stories');

-- Authors
INSERT INTO authors VALUES
    ('AUTH001', 'George', 'Orwell',  '1903-06-25', 'British', NULL),
    ('AUTH002', 'J.K.',   'Rowling', '1965-07-31', 'British', NULL);

-- Books
INSERT INTO books VALUES
    ('9780451524935', '1984',         '1950-07-01', 5, 5, 'Signet',     1, 'Dystopian classic', 'English', 328, FALSE, 'GEN001'),
    ('9780743477106', 'Harry Potter', '1997-06-26', 3, 3, 'Bloomsbury', 1, 'Wizard school',     'English', 223, FALSE, 'GEN001');

-- Users
INSERT INTO users VALUES
    ('USER001', 'John', 'Doe',   'john@example.com', '555-0101', CURDATE(), '1990-05-10', '123 Main St', 'STUDENT', 5,  0.00, TRUE, 'hashed123'),
    ('USER002', 'Jane', 'Smith', 'jane@example.com', '555-0102', CURDATE(), '1985-11-22', '456 Oak Ave', 'FACULTY', 10, 0.00, TRUE, 'hashed456');

-- Loans
INSERT INTO loans VALUES
    ('LN001', '9780451524935', 'USER001', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL, 'ACTIVE', 0.00),
    ('LN002', '9780743477106', 'USER002', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 21 DAY), NULL, 'ACTIVE', 0.00);
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ | Library Management System

</div>
