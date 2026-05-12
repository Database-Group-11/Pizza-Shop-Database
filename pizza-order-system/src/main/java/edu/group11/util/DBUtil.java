package main.java.edu.group11.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class DBUtil {
    // 数据库配置参数
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/pizza_order_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8";
    // TODO: 修改为你的MySQL用户名
    private static final String USERNAME = "root";
    // TODO: 修改为你的MySQL密码
    private static final String PASSWORD = "T4c3b95J!3b9";

    // ThreadLocal 用于事务管理（每个线程有自己的连接）
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /**
     * 静态代码块：加载驱动（只执行一次）
     */
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("数据库驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("数据库驱动加载失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * @return Connection 连接对象
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            threadLocal.set(conn);
        }
        return conn;
    }

    /**
     * 关闭连接（并移除ThreadLocal）
     */
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

    /**
     * 关闭所有资源（ResultSet, Statement, Connection）
     * @param rs ResultSet
     * @param stmt Statement
     * @param conn Connection
     */
    public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    /**
     * 关闭 ResultSet
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭 Statement
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭 Connection
     */
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

    /**
     * 开启事务
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.commit();
        conn.setAutoCommit(true);
    }

    /**
     * 回滚事务
     */
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

    /**
     * 执行 SQL 文件（用于初始化数据库）
     * @param sqlFilePath SQL文件路径
     * @throws Exception
     */
    public static void executeSqlFile(String sqlFilePath) throws Exception {
        String sqlContent = new String(Files.readAllBytes(Paths.get(sqlFilePath)), "UTF-8");

        // 按分号分割SQL语句（简单处理，生产环境建议使用更完善的解析器）
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
            System.out.println("成功执行SQL文件：" + sqlFilePath);
        } catch (SQLException e) {
            System.err.println("执行SQL文件失败：" + e.getMessage());
            throw e;
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * 批量执行多个SQL文件（按顺序）
     * @param sqlFilePaths SQL文件路径数组
     */
    public static void executeSqlFiles(String... sqlFilePaths) {
        for (String filePath : sqlFilePaths) {
            try {
                executeSqlFile(filePath);
            } catch (Exception e) {
                System.err.println("执行失败：" + filePath);
                e.printStackTrace();
                // 失败时停止执行
                break;
            }
        }
    }

    /**
     * 初始化整个数据库（按正确顺序执行所有SQL文件）
     * @param sqlDirPath SQL文件夹路径
     */
    public static void initDatabase(String sqlDirPath) {
        try {
            // 按顺序执行SQL文件
            executeSqlFile(sqlDirPath + "/schema.sql");      // 1. 建表
            executeSqlFile(sqlDirPath + "/views.sql");       // 2. 建视图
            executeSqlFile(sqlDirPath + "/indexes.sql");     // 3. 建索引
            executeSqlFile(sqlDirPath + "/test_data.sql");   // 4. 插入测试数据
            System.out.println("数据库初始化成功！");
        } catch (Exception e) {
            System.err.println("数据库初始化失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试数据库连接
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前数据库时间（用于测试）
     * @return 数据库当前时间
     */
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

    /**
     * 执行更新操作（INSERT, UPDATE, DELETE）
     * @param sql SQL语句
     * @param params 参数（可变参数）
     * @return 受影响的行数
     */
    public static int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);

            // 设置参数
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

    /**
     * 执行查询操作（SELECT）
     * @param sql SQL语句
     * @param params 参数（可变参数）
     * @return ResultSet（需要手动关闭）
     */
    public static ResultSet executeQuery(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        // 注意：这里不能关闭连接，因为ResultSet需要保持连接
    }

    /**
     * 查询单个对象（String/Integer等）
     * @param sql SQL语句
     * @param params 参数
     * @return 查询结果的第一行第一列
     */
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
