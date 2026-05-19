# Pizza Ordering System - Setup Guide

## 1. Requirements

| Software | Version | Notes |
|----------|---------|-------|
| JDK | 21 | Required |
| MySQL | 8.0+ | Database |
| Tomcat | 10.1.x | Servlet container |
| IntelliJ IDEA | 2023+ | IDE |
| Git | Any | Version control |

## 2. Clone the Project

```bash
git clone <your-repo-url>
cd Pizza-Shop-Database
```

## 3. Database Setup

### 3.1 Start MySQL

```bash
# Windows
net start MySQL

# Mac/Linux
mysql.server start
```

### 3.2 Create Database and Import Data

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE pizza_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Use database
USE pizza_shop;

# Execute SQL files (update path to your project)
SOURCE D:/your-project-path/pizza-order-system/sql/schema.sql;
SOURCE D:/your-project-path/pizza-order-system/sql/test_data.sql;
SOURCE D:/your-project-path/pizza-order-system/sql/views.sql;
SOURCE D:/your-project-path/pizza-order-system/sql/indexes.sql;
```

### 3.3 Verify Database

```sql
USE pizza_shop;
SHOW TABLES;                    -- Should show 8 tables
SELECT COUNT(*) FROM customers; -- 3
SELECT COUNT(*) FROM pizzas;     -- 5
SELECT COUNT(*) FROM toppings;   -- 7
```

## 4. Project Configuration

### 4.1 Configure Database Connection

Copy the template file:

```bash
cp src/util/DBUtilTemplate.java src/util/DBUtil.java
```

Edit `DBUtil.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/pizza_shop?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_mysql_password";  // ← Change this
```

### 4.2 Configure Tomcat

1. **Download Tomcat 10.1.x**: https://tomcat.apache.org/download-10.cgi

2. **Configure in IDEA**:
    - `Run` → `Edit Configurations` → `+` → `Tomcat Server` → `Local`
    - `Configure...` → Select Tomcat installation directory
    - `Deployment` tab → `+` → `Artifact` → Select `myproject:war exploded`
    - Set `Application Context` to `/`
    - Click `OK`

### 4.3 Configure Artifact Output Layout

If API returns empty data, check Artifact configuration:

1. `File` → `Project Structure` → `Artifacts`
2. Select `myproject:war exploded`
3. In `Output Layout`:
    - Remove `'myproject'模块:'Web'facet resources` (if exists)
    - Click `+` → `Directory Content` → Select `pizza-order-system/web` folder
4. Click `Apply` → `OK`

## 5. Run the Project

### 5.1 Start Tomcat

Click the green triangle ▶ in IDEA

### 5.2 Test APIs

| API | URL | Expected Result |
|-----|-----|-----------------|
| Pizzas | `http://localhost:8080/api/pizzas` | Returns 5 pizzas |
| Toppings | `http://localhost:8080/api/toppings` | Returns 7 toppings |

## 6. Troubleshooting

### 6.1 Port Already in Use

Change Tomcat port: `Run` → `Edit Configurations` → Tomcat → `HTTP port` to `8081`

### 6.2 Database Connection Failed

- Check if MySQL is running
- Verify password in `DBUtil.java`
- Check port number in URL (3306 or 3307)

### 6.3 API Returns Empty Data

- Check `available` column in `pizzas` table is `1`
- Verify Artifact output layout includes `web` folder
- Rebuild: `Build` → `Build Artifacts` → `Rebuild`

### 6.4 Console Shows Chinese Garbled Text

Edit `conf/logging.properties`:
```
java.util.logging.ConsoleHandler.encoding = GBK
```

## 7. Project Structure

```
Pizza-Shop-Database/
├── pizza-order-system/
│   ├── src/                    # Java source code
│   │   ├── dao/               # Data access layer
│   │   ├── dto/               # Data transfer objects
│   │   ├── model/             # Entity classes
│   │   ├── servlet/           # Controllers
│   │   └── util/              # Utilities
│   ├── sql/                   # SQL files
│   │   ├── schema.sql         # Table structure
│   │   ├── test_data.sql      # Test data
│   │   ├── views.sql          # Views
│   │   └── indexes.sql        # Indexes
│   ├── web/                   # Frontend files
│   └── lib/                   # JAR dependencies
├── .gitignore
└── README.md
```

## 8. API Endpoints

| Module | Method | Path | Description |
|--------|--------|------|-------------|
| User | POST | `/api/customer/register` | Register |
| User | POST | `/api/customer/login` | Login |
| Pizza | GET | `/api/pizzas` | Get pizza list |
| Topping | GET | `/api/toppings` | Get topping list |
| Order | POST | `/api/orders` | Create order |
| Order | GET | `/api/orders/customer` | Get user orders |
| Order | GET | `/api/orders/{id}` | Get order details |
| Payment | POST | `/api/payments` | Process payment |
| Delivery | GET | `/api/deliveries/order/{id}` | Get delivery info |
| Admin | POST | `/api/admin/login` | Admin login |
| Admin | GET | `/api/admin/orders` | Get all orders |
| Admin | PUT | `/api/admin/orders/{id}/status` | Update order status |
| Admin | GET | `/api/admin/reports` | Get statistics |

## 9. Team Member Checklist

Each team member must complete:

- [ ] Install JDK 21, MySQL, Tomcat 10.1
- [ ] Execute SQL files to initialize database
- [ ] Copy `DBUtilTemplate.java` to `DBUtil.java` and set password
- [ ] Configure Tomcat in IDEA
- [ ] Run project and test `/api/pizzas` endpoint

**After setup, visit:** `http://localhost:8080/api/pizzas`