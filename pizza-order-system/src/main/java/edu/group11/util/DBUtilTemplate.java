package main.java.edu.group11.util;

import java.sql.*;
import java.nio.file.*;

public class DBUtilTemplate {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/pizza_order_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8";
    // TODO: 修改为你的MySQL用户名
    private static final String USERNAME = "root";
    // TODO: 修改为你的MySQL密码
    private static final String PASSWORD = "123456";

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    static {
        try {
            Class.forName(DRIVER);
            System.out.println("Successfully loaded database driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to loaded database driver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            threadLocal.set(conn);
        }
        return conn;
    }

    public static void closeConnection() {
        Connection conn = threadLocal.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            threadLocal.remove();
        }
    }

    public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
    }

    public static void commitTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.commit();
        conn.setAutoCommit(true);
    }

    public static void rollbackTransaction() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeSqlFile(String sqlFilePath) throws Exception {
        String sqlContent = new String(Files.readAllBytes(Paths.get(sqlFilePath)), "UTF-8");

        String[] sqlStatements = sqlContent.split(";");

        Connection conn = getConnection();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            for (String sql : sqlStatements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    // 跳过注释行
                    if (!trimmedSql.startsWith("--") && !trimmedSql.startsWith("/*")) {
                        stmt.execute(trimmedSql);
                    }
                }
            }
            System.out.println("Successfully executed SQL files: " + sqlFilePath);
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL files: " + e.getMessage());
            throw e;
        } finally {
            closeStatement(stmt);
        }
    }

    public static void executeSqlFiles(String... sqlFilePaths) {
        for (String filePath : sqlFilePaths) {
            try {
                executeSqlFile(filePath);
            } catch (Exception e) {
                System.err.println("Failed to execute: " + filePath);
                e.printStackTrace();
                break;
            }
        }
    }

    public static void initDatabase(String sqlDirPath) {
        try {
            executeSqlFile(sqlDirPath + "/schema.sql");      // 1. 建表
            executeSqlFile(sqlDirPath + "/views.sql");       // 2. 建视图
            executeSqlFile(sqlDirPath + "/indexes.sql");     // 3. 建索引
            executeSqlFile(sqlDirPath + "/test_data.sql");   // 4. 插入测试数据
            System.out.println("Successfully initialized the database");
        } catch (Exception e) {
            System.err.println("Failed to initialize the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Failed to test database connection: " + e.getMessage());
            return false;
        }
    }

    public static Timestamp getCurrentDatabaseTime() {
        String sql = "SELECT NOW()";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            closeStatement(ps);
        }
    }

    public static ResultSet executeQuery(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object querySingleValue(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
