# Pizza Shop Online Ordering System

A full-stack web application for pizza takeout ordering, built with Jakarta EE (Servlet) + MySQL + vanilla HTML/CSS/JavaScript. This project was developed as a course assignment for the Database Systems course.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [System Architecture](#3-system-architecture)
4. [Database Design](#4-database-design)
5. [Features](#5-features)
6. [Project Structure](#6-project-structure)
7. [Setup & Run](#7-setup--run)
8. [API Reference](#8-api-reference)
9. [Screenshots](#9-screenshots)

---

## 1. Project Overview

The Pizza Shop Online Ordering System is a complete takeout platform with two subsystems:

- **Customer-facing site** — Browse menu, customize pizzas with toppings, manage cart, place orders, track order status
- **Admin panel** — Manage orders, update order status, assign delivery riders, monitor topping inventory, view sales reports

The system demonstrates a classic three-tier architecture: **frontend (HTML/CSS/JS)** → **servlet layer (Java)** → **database (MySQL)**, with proper separation of concerns across DAO, DTO, Model, and Servlet layers.

---

## 2. Technology Stack

| Layer | Technology |
|-------|------------|
| Frontend | HTML5, CSS3, Vanilla JavaScript (ES6+) |
| Backend | Jakarta EE 10 (Servlet 6.0) |
| Database | MySQL 8.0 |
| Server | Apache Tomcat 10.1 |
| Build Tool | Maven (pom.xml) |
| Libraries | Gson 2.14, Org JSON, MySQL Connector/J 8.0 |
| Version Control | Git |

---

## 3. System Architecture

```
Browser (HTML/CSS/JS)
    │
    ▼
Tomcat 10.1 ───────────────────────────────────┐
    │                                            │
    ▼                                            │
Jakarta Servlet Layer                           │
├── CorsFilter         (global CORS)            │
├── CustomerServlet    (/api/customer/*)        │
├── PizzaServlet       (/api/pizzas/*)          │
├── ToppingServlet     (/api/toppings/*)        │
├── OrderServlet       (/api/orders/*)          │
├── PaymentServlet     (/api/payments)           │
├── DeliveryServlet    (/api/deliveries/*)       │
├── ReportServlet      (/api/reports/*)          │
├── AdminServlet       (/api/admin/*)            │
└── CartServlet        (/api/cart/*)             │
    │                                            │
    ▼                                            │
Data Access Layer (DAO)                         │
├── CustomerDAO, PizzaDAO, ToppingDAO           │
├── OrderDAO, OrderItemDAO                      │
├── PaymentDAO, DeliveryDAO, ReportDAO          │
    │                                            │
    ▼                                            │
MySQL 8.0 (pizza_shop database)                 │
├── 8 tables, 4 views, indexes                  │
└── Test data: 3 customers, 7 pizzas, 7 toppings│
```

### Design Patterns

- **MVC pattern**: Servlet (Controller) → DAO (Model) → JSP/HTML (View)
- **DTO pattern**: Request/Response objects decouple API contracts from entities
- **Singleton via WebServlet**: Each servlet is a single-instance controller
- **Front Controller**: Path-based routing within each servlet (`/api/orders/*`)

---

## 4. Database Design

### ER Diagram

The database `pizza_shop` contains 8 core tables:

```
customers ──1:N── orders ──1:N── order_items ──N:1── pizzas
                         │                         │
                        1:1                       N:M (via order_toppings)
                         │                         │
                      payments                 toppings
                         │
                        1:1
                         │
                      deliveries
```

### Table Summary

| Table | Description | Key Columns |
|-------|-------------|-------------|
| `customers` | Registered users | customer_id, name, phone, password, address |
| `pizzas` | Pizza menu | pizza_id, name, base_price, category, stock_quantity |
| `toppings` | Available toppings | topping_id, name, price, stock_quantity |
| `orders` | Customer orders | order_id, order_no, total_amount, status, customer_id |
| `order_items` | Items within an order | item_id, order_id, pizza_id, quantity, unit_price |
| `order_toppings` | Toppings on each item | item_id, topping_id, quantity |
| `payments` | Payment records | payment_id, order_id, amount, status, transaction_id |
| `deliveries` | Delivery tracking | delivery_id, order_id, rider_name, status |

### Constraints & Integrity

- Foreign keys with `ON DELETE CASCADE` on order-related tables
- `UNIQUE` constraints on `customers.phone`, `orders.order_no`, `payments.order_id`, `deliveries.order_id`
- `DECIMAL(10,2)` for all monetary values to avoid floating-point precision issues
- Default status values: orders → `pending`, deliveries → `preparing`

---

## 5. Features

### Customer System

| Feature | Description |
|---------|-------------|
| User Registration | Phone + password registration with client-side validation |
| User Login | Session-based authentication (HttpSession) |
| Browse Menu | Category filtering (Classic, Specialty, Meat, Seafood, Vegetarian) |
| Customize Pizza | Add/remove toppings with real-time price calculation |
| Shopping Cart | Add, update quantity, remove items (localStorage) |
| Checkout | Delivery address, payment method selection |
| Place Order | Creates order + order items in a DB transaction |
| Order Tracking | Real-time status with progress bar (5s polling) |
| Pay Order | Payment processing via Credit Card/Alipay/WeChat |
| Order History | Filter by status (All/Pending/Paid/Completed/Cancelled) |
| Re-order | Clone past order items into cart |

### Admin System

| Feature | Description |
|---------|-------------|
| Admin Login | Username/password authentication |
| Order Management | View all orders with customer names, filter stats |
| Status Update | Change order status (Pending → Paid → Preparing → Delivering → Completed → Cancelled) |
| Rider Assignment | Select from 5 predefined riders, auto- create delivery record |
| Topping Inventory | CRUD operations, stock update, low-stock alerts |
| Sales Reports | Total revenue, order count, average order value |
| Top Pizzas | Ranked list of best-selling pizzas |
| Sales Trend | Bar chart of last 7 days order volume |

---

## 6. Project Structure

```
Pizza-Shop-Database/
├── pom.xml                        # Maven build configuration
├── README.md
├── doc/
│   └── er_diagram.drawio.html     # ER diagram
└── pizza-order-system/
    ├── src/main/java/edu/group11/
    │   ├── dao/                   # Data access objects (8 files)
    │   │   ├── CustomerDAO.java
    │   │   ├── PizzaDAO.java
    │   │   ├── ToppingDAO.java
    │   │   ├── OrderDAO.java
    │   │   ├── OrderItemDAO.java
    │   │   ├── PaymentDAO.java
    │   │   ├── DeliveryDAO.java
    │   │   └── ReportDAO.java
    │   ├── dto/request/           # Request DTOs (6 files)
    │   │   ├── CustomerLoginRequest.java
    │   │   ├── CustomerRegisterRequest.java
    │   │   ├── AdminLoginRequest.java
    │   │   ├── OrderCreateRequest.java
    │   │   ├── OrderItemRequest.java
    │   │   ├── PaymentRequest.java
    │   │   ├── ToppingCreateRequest.java
    │   │   └── ToppingUpdateRequest.java
    │   ├── dto/response/          # Response DTOs (11 files)
    │   ├── model/                 # Entity classes (6 files)
    │   │   ├── Customer.java
    │   │   ├── Pizza.java
    │   │   ├── Topping.java
    │   │   ├── Order.java
    │   │   ├── OrderItem.java
    │   │   ├── Payment.java
    │   │   └── Delivery.java
    │   ├── servlet/               # Servlet controllers (9 files)
    │   │   ├── CorsFilter.java    # Global CORS filter
    │   │   ├── CustomerServlet.java
    │   │   ├── PizzaServlet.java
    │   │   ├── ToppingServlet.java
    │   │   ├── OrderServlet.java
    │   │   ├── PaymentServlet.java
    │   │   ├── DeliveryServlet.java
    │   │   ├── ReportServlet.java
    │   │   ├── AdminServlet.java
    │   │   └── CartServlet.java
    │   └── util/
    │       ├── DBUtil.java        # Database connection (git-ignored)
    │       └── DBUtilTemplate.java # Connection template
    ├── sql/                       # Database files
    │   ├── schema.sql             # Table definitions
    │   ├── test_data.sql          # Sample data
    │   ├── views.sql              # Database views
    │   └── indexes.sql            # Performance indexes
    └── web/                       # Frontend files
        ├── css/style.css
        ├── js/main.js             # Shared JS (API, auth, cart, utils)
        ├── login.html             # Customer login
        ├── register.html          # Customer registration
        ├── menu.html              # Pizza menu browser
        ├── pizza_detail.html      # Pizza customization
        ├── cart.html              # Shopping cart
        ├── checkout.html          # Order checkout
        ├── order_status.html      # Order tracking
        ├── order_history.html     # Order history
        ├── admin_login.html       # Admin login
        ├── admin_orders.html      # Admin order management
        ├── admin_inventory.html   # Admin topping inventory
        └── admin_reports.html     # Admin sales reports
```

---

## 7. Setup & Run

### Prerequisites

- JDK 21, MySQL 8.0+, Apache Tomcat 10.1, IntelliJ IDEA 2023+

### Step 1 — Database

```sql
CREATE DATABASE pizza_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pizza_shop;

-- Replace <project-path> with your actual project location, e.g. D:/study-resources/Pizza-Shop-Database
SOURCE <project-path>/pizza-order-system/sql/schema.sql;
SOURCE <project-path>/pizza-order-system/sql/test_data.sql;
SOURCE <project-path>/pizza-order-system/sql/views.sql;
SOURCE <project-path>/pizza-order-system/sql/indexes.sql;
```

### Step 2 — Configure DB Connection

Copy `pizza-order-system/src/main/java/edu/group11/util/DBUtilTemplate.java` to `DBUtil.java` and set your MySQL password:

```java
private static final String PASSWORD = "your_mysql_password";
```

### Step 3 — IntelliJ Configuration

1. Open the project root `Pizza-Shop-Database/` in IntelliJ
2. `Run` → `Edit Configurations` → `+` → `Tomcat Server` → `Local`
3. Set Tomcat home directory, HTTP port `8080`
4. `Deployment` → `+` → `Artifact` → `myproject:war exploded`
5. Application context: `/`

### Step 4 — Run

Click the green ▶ button. The application will be available at `http://localhost:8080/`.

### Test Accounts

| Role | Username/Phone | Password |
|------|---------------|----------|
| Admin | `admin` | `admin123` |
| Customer | `13800138000` | `123456` |

---

## 8. API Reference

### Customer Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/customer/register` | Register new user |
| POST | `/api/customer/login` | Login with phone + password |

### Menu Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/pizzas` | List all pizzas |
| GET | `/api/pizzas/available` | List available pizzas |
| GET | `/api/pizzas/category/{name}` | Filter by category |
| GET | `/api/toppings` | List all toppings |
| GET | `/api/toppings/available` | List available toppings |

### Order Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/orders` | Create new order |
| GET | `/api/orders/customer?customer_id={id}` | Get user's orders |
| GET | `/api/orders/{id}` | Get order detail |
| POST | `/api/payments` | Process payment |
| GET | `/api/deliveries/order?orderId={id}` | Get delivery info |

### Admin Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/admin/login` | Admin login |
| GET | `/api/admin/orders` | List all orders |
| PUT | `/api/admin/orders/{id}/status` | Update order status |
| GET | `/api/toppings` | List toppings (CRUD via GET/POST/PUT/DELETE) |

### Report Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/reports/sales/today` | Today's sales stats |
| GET | `/api/reports/top-pizzas?limit=10` | Top-selling pizzas |
| GET | `/api/reports/daily-trend` | Last 7 days order trend |

---

## 9. Team

This project was developed as a course assignment for the Database Systems course.

| Name         | Student ID | Contributions     |
|--------------|------------|-------------------|
| Dong Qiutong | 24107752   | Database, backend |
| Zhu Jingyi   | 24107747   | Frontend          |
| Qiu Siqi     | 27107750   | Backend           |
| Liu Yiming   | 24107755   | Backend           |
| Gao Debo     | 24107756   | Testing           |

---

**Course:** Databases and Info Sys  
**Course Code:** COMP2013J  
**Semester:** Spring 2026  
**Lecturer:** Dong Ruihai
