# 📚 Library Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17%2B-blue?style=for-the-badge&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-orange?style=for-the-badge&logo=mysql)
![Maven](https://img.shields.io/badge/Build-Maven-yellow?style=for-the-badge&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-Passing-brightgreen?style=for-the-badge&logo=junit5)
![Coverage](https://img.shields.io/badge/Coverage-85%25-blue?style=for-the-badge)

**A production-ready Library Management System** with book inventory, user management, and loan tracking — built on a clean layered architecture with both Console and GUI interfaces.

[Features](#-features) · [Quick Start](#-quick-start) · [Architecture](#-architecture) · [Database](#-database-setup) · [API Docs](#-api-documentation) · [Contributing](#-contributing)

</div>

---

## ✨ Features

### Core Modules

| Module | Description | Status |
|--------|-------------|--------|
| 📖 **Book Management** | Full CRUD with real-time availability tracking | ✅ Stable |
| 👥 **User Management** | 4 user types with role-based privileges | ✅ Stable |
| 🔁 **Loan System** | Automatic due dates, fine calculation, and overdue detection | ✅ Stable |
| 📊 **Reports** | Popular books, overdue loans, and user activity analytics | ✅ Stable |
| 🔔 **Notifications** | Email alerts for due dates and overdue loans | 🚧 In Progress |
| 📦 **Reservations** | Waitlist queue for unavailable books | 🚧 In Progress |

### Technical Highlights

- ✅ Clean layered architecture: DAO → Service → UI
- ✅ Both Console and GUI interfaces
- ✅ Input validation and global exception handling
- ✅ Prepared statements throughout (SQL injection safe)
- ✅ HikariCP connection pooling
- ✅ JUnit 5 unit + integration test suite
- ✅ MySQL 8 relational backend with optimised indexes

---

## 🚀 Quick Start

### Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17 or higher |
| MySQL | 8.0 or higher |
| Maven | 3.8+ |

### 1. Clone the repository

```bash
git clone https://github.com/your-username/library-management-system.git
cd library-management-system
```

### 2. Configure the database

Copy the example config and fill in your credentials:

```bash
cp src/main/resources/config.example.properties src/main/resources/config.properties
```

```properties
# config.properties
db.url=jdbc:mysql://localhost:3306/library_db
db.username=your_username
db.password=your_password
db.pool.size=10
```

### 3. Set up the database

```bash
mysql -u your_username -p < sql/schema.sql
mysql -u your_username -p library_db < sql/seed.sql
```

### 4. Build and run

```bash
# Build
mvn clean package

# Run Console UI
java -jar target/library-system.jar --console

# Run GUI
java -jar target/library-system.jar --gui
```

---

## 🏗 Architecture

The system follows a strict three-layer architecture, ensuring separation of concerns and testability.

```
┌─────────────────────────────────────────┐
│              UI Layer                   │
│   ┌───────────────┐  ┌───────────────┐  │
│   │  Console UI   │  │  Swing GUI    │  │
│   └───────┬───────┘  └───────┬───────┘  │
└───────────┼──────────────────┼──────────┘
            │                  │
            └────────┬─────────┘
                     ▼
┌─────────────────────────────────────────┐
│           Service Layer                 │
│  BookService  UserService  LoanService  │
│         FineCalculationService          │
│         NotificationService             │
└────────────────────┬────────────────────┘
                     ▼
┌─────────────────────────────────────────┐
│             DAO Layer                   │
│   BookDAO   UserDAO   LoanDAO           │
│          GenreDAO  AuthorDAO            │
└────────────────────┬────────────────────┘
                     ▼
┌─────────────────────────────────────────┐
│          MySQL Database                 │
│     (HikariCP connection pool)          │
└─────────────────────────────────────────┘
```

### Package Structure

```
src/
├── main/java/com/library/
│   ├── dao/              # Data Access Objects (one per entity)
│   ├── model/            # Domain models / entities
│   ├── service/          # Business logic
│   ├── ui/
│   │   ├── console/      # Console menus and handlers
│   │   └── gui/          # Swing panels and frames
│   ├── util/             # Helpers: validators, formatters, constants
│   └── exception/        # Custom exception hierarchy
├── main/resources/
│   └── config.properties
└── test/java/com/library/
    ├── service/          # Unit tests (Mockito mocked DAOs)
    └── integration/      # Integration tests (H2 in-memory DB)
```

---

## 👥 User Roles & Privileges

| Role | Max Books | Loan Period | Fine / Day | Notes |
|------|-----------|-------------|-----------|-------|
| **Student** | 5 | 14 days | $0.25 | Default for new registrations |
| **Faculty** | 10 | 30 days | $0.10 | Reduced fine rate |
| **Staff** | 7 | 21 days | $0.15 | Internal employees |
| **Visitor** | 2 | 7 days | $0.50 | Guest access only |

---

## 🗄 Database Setup

### Entity Relationship Overview

```
genres ──< books >── book_authors >── authors
                        │
                     loans
                      / \
                  users   (fine tracking per loan)
```

### Schema

```sql
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE genres (
    genre_id    VARCHAR(20)  PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE authors (
    author_id     VARCHAR(20)  PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    nationality   VARCHAR(100),
    biography     TEXT
);

-- Junction table for many-to-many book ↔ author relationship
CREATE TABLE book_authors (
    isbn      VARCHAR(20) NOT NULL,
    author_id VARCHAR(20) NOT NULL,
    PRIMARY KEY (isbn, author_id)
);

CREATE TABLE books (
    isbn               VARCHAR(20)  PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    publication_date   DATE,
    quantity           INT          DEFAULT 1,
    available_quantity INT          DEFAULT 1,
    publisher          VARCHAR(100),
    edition            INT,
    description        TEXT,
    language           VARCHAR(50)  DEFAULT 'English',
    page_count         INT,
    is_reference_only  BOOLEAN      DEFAULT FALSE,
    genre_id           VARCHAR(20),
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_books_genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE users (
    user_id           VARCHAR(20)                              PRIMARY KEY,
    first_name        VARCHAR(100)                             NOT NULL,
    last_name         VARCHAR(100)                             NOT NULL,
    email             VARCHAR(255)                             UNIQUE,
    phone_number      VARCHAR(20),
    registration_date DATE                                     NOT NULL,
    date_of_birth     DATE,
    address           TEXT,
    user_type         ENUM('STUDENT','FACULTY','STAFF','VISITOR') NOT NULL,
    max_books_allowed INT                                      NOT NULL,
    is_active         BOOLEAN                                  DEFAULT TRUE,
    password_hash     VARCHAR(255)                             NOT NULL,  -- BCrypt
    created_at        TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loans (
    loan_id     VARCHAR(20)                               PRIMARY KEY,
    book_isbn   VARCHAR(20)                               NOT NULL,
    user_id     VARCHAR(20)                               NOT NULL,
    loan_date   DATE                                      NOT NULL,
    due_date    DATE                                      NOT NULL,
    return_date DATE,
    status      ENUM('ACTIVE','RETURNED','OVERDUE','LOST') NOT NULL,
    fine_amount DECIMAL(10,2)                             DEFAULT 0.00,
    notes       TEXT,
    CONSTRAINT fk_loans_book FOREIGN KEY (book_isbn) REFERENCES books(isbn),
    CONSTRAINT fk_loans_user FOREIGN KEY (user_id)   REFERENCES users(user_id)
);

-- Audit log for tracking all data changes
CREATE TABLE audit_log (
    log_id      BIGINT       PRIMARY KEY AUTO_INCREMENT,
    table_name  VARCHAR(50)  NOT NULL,
    record_id   VARCHAR(50)  NOT NULL,
    action      ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    changed_by  VARCHAR(20),
    changed_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    old_value   JSON,
    new_value   JSON
);

-- Performance indexes
CREATE INDEX idx_loans_user_status  ON loans(user_id, status);
CREATE INDEX idx_loans_due_date     ON loans(due_date);
CREATE INDEX idx_loans_status       ON loans(status);
CREATE INDEX idx_books_title        ON books(title);
CREATE INDEX idx_books_genre        ON books(genre_id);
CREATE INDEX idx_users_email        ON users(email);
```

---

## 🌱 Sample Data

```sql
-- Genres
INSERT INTO genres VALUES
    ('GEN001', 'Fiction',         'Imaginative narratives and stories'),
    ('GEN002', 'Science Fiction', 'Futuristic and speculative stories'),
    ('GEN003', 'Non-Fiction',     'Factual accounts and essays');

-- Authors
INSERT INTO authors (author_id, first_name, last_name, date_of_birth, nationality) VALUES
    ('AUTH001', 'George', 'Orwell',  '1903-06-25', 'British'),
    ('AUTH002', 'J.K.',   'Rowling', '1965-07-31', 'British'),
    ('AUTH003', 'Frank',  'Herbert', '1920-10-08', 'American');

-- Books
INSERT INTO books (isbn, title, publication_date, quantity, available_quantity, publisher, edition, language, page_count, genre_id) VALUES
    ('9780451524935', '1984',                  '1950-07-01', 5, 5, 'Signet Classic',   1, 'English', 328, 'GEN001'),
    ('9780747532699', 'Harry Potter and the Philosopher''s Stone', '1997-06-26', 3, 3, 'Bloomsbury', 1, 'English', 223, 'GEN001'),
    ('9780441013593', 'Dune',                  '1965-08-01', 4, 4, 'Ace Books',        1, 'English', 688, 'GEN002');

-- Book ↔ Author links
INSERT INTO book_authors VALUES
    ('9780451524935', 'AUTH001'),
    ('9780747532699', 'AUTH002'),
    ('9780441013593', 'AUTH003');

-- Users (passwords are BCrypt hashes of 'password123')
INSERT INTO users (user_id, first_name, last_name, email, phone_number, registration_date, date_of_birth, address, user_type, max_books_allowed, password_hash) VALUES
    ('USER001', 'John', 'Doe',   'john.doe@example.com',   '555-0101', CURDATE(), '1990-05-10', '123 Main St',  'STUDENT', 5,  '$2a$12$examplehash1'),
    ('USER002', 'Jane', 'Smith', 'jane.smith@example.com', '555-0102', CURDATE(), '1975-11-22', '456 Oak Ave',  'FACULTY', 10, '$2a$12$examplehash2'),
    ('USER003', 'Bob',  'Jones', 'bob.jones@example.com',  '555-0103', CURDATE(), '1988-03-14', '789 Pine Rd',  'STAFF',   7,  '$2a$12$examplehash3');

-- Active loans
INSERT INTO loans (loan_id, book_isbn, user_id, loan_date, due_date, return_date, status, fine_amount) VALUES
    ('LN001', '9780451524935', 'USER001', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL, 'ACTIVE',   0.00),
    ('LN002', '9780747532699', 'USER002', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), NULL, 'ACTIVE',   0.00),
    ('LN003', '9780441013593', 'USER001', DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, 'OVERDUE', 1.50);
```

---

## 📘 API Documentation

### BookService

```java
// Retrieve a book by ISBN — returns empty Optional if not found
Optional<Book> findByIsbn(String isbn);

// Search by title fragment, genre, or author (paginated)
List<Book> search(String query, int page, int pageSize);

// Add a new book — throws DuplicateIsbnException if ISBN exists
Book addBook(BookDTO bookDTO);

// Update book details — throws BookNotFoundException if missing
Book updateBook(String isbn, BookDTO bookDTO);

// Permanently remove a book — throws LoanConflictException if active loans exist
void deleteBook(String isbn);

// Check real-time availability
boolean isAvailable(String isbn);
```

### LoanService

```java
// Borrow a book — validates availability and user loan limits
Loan borrowBook(String userId, String isbn);

// Return a book and calculate any outstanding fine
Loan returnBook(String loanId);

// Retrieve all active loans for a user
List<Loan> getActiveLoans(String userId);

// Retrieve all overdue loans system-wide (for staff/admin)
List<Loan> getAllOverdueLoans();

// Calculate current outstanding fine for a loan
BigDecimal calculateFine(String loanId);
```

### UserService

```java
// Register a new user — hashes password with BCrypt automatically
User registerUser(UserDTO userDTO);

// Authenticate — returns JWT token on success
String authenticate(String email, String password);

// Retrieve user profile
Optional<User> findById(String userId);

// Update profile fields
User updateUser(String userId, UserDTO userDTO);

// Deactivate account (soft delete)
void deactivateUser(String userId);
```

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dgroups="unit"

# Run only integration tests
mvn test -Dgroups="integration"

# Generate coverage report (opens at target/site/jacoco/index.html)
mvn jacoco:report
```

### Test Strategy

| Layer | Tool | Scope |
|-------|------|-------|
| Service unit tests | JUnit 5 + Mockito | All business logic paths |
| DAO integration tests | JUnit 5 + H2 in-memory DB | All queries and transactions |
| End-to-end smoke tests | JUnit 5 | Happy-path loan lifecycle |

---

## 🔒 Security Notes

- Passwords are hashed with **BCrypt** (cost factor 12) — never stored in plain text
- All database queries use **prepared statements** to prevent SQL injection
- Role-based access control (RBAC) enforced at the service layer
- Sensitive fields (email, phone) should be encrypted at rest in production deployments
- Sessions are managed via signed JWT tokens with configurable expiry

---

## 🚢 Deployment

### Running with Docker (recommended)

```bash
# Build the image
docker build -t library-system .

# Start the app and MySQL together
docker compose up -d
```

### Manual production setup

```bash
mvn clean package -DskipTests
java -Xmx512m -jar target/library-system.jar --config /etc/library/config.properties
```

---

## 🛠 Troubleshooting

| Problem | Likely Cause | Fix |
|---------|-------------|-----|
| `Connection refused` on startup | MySQL not running or wrong credentials | Check `config.properties` and verify MySQL service is active |
| `Too many connections` error | Pool size too small under load | Increase `db.pool.size` in config |
| GUI doesn't open | Missing display / headless server | Use `--console` flag instead |
| `DuplicateIsbnException` on import | ISBN already in the database | Check existing records or use the update endpoint |
| Tests fail with `Table not found` | H2 schema not applied | Ensure `schema-h2.sql` is on the test classpath |

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository and create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. **Write tests** for any new business logic
3. **Run the full test suite** before pushing
   ```bash
   mvn clean verify
   ```
4. **Open a Pull Request** with a clear description of the change

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for code style guidelines and the branching strategy.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ | Library Management System

[Report a Bug](https://github.com/your-username/library-management-system/issues) · [Request a Feature](https://github.com/your-username/library-management-system/issues)

</div>
