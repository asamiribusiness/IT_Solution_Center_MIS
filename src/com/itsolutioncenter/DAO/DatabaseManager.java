package com.itsolutioncenter.dao;

import com.itsolutioncenter.config.DatabaseConfig;
import com.itsolutioncenter.exception.DatabaseException;
import com.itsolutioncenter.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Generic table access (insert/update/delete/select/query) plus a small
 * set of project-specific and User-specific queries used across the app.
 *
 * Design notes for whoever reads this next:
 *  - The connection is held for the lifetime of the app (singleton). It is
 *    NOT thread-safe to share across multiple threads doing concurrent
 *    writes; if you add multithreading later, switch to a connection pool
 *    (HikariCP) instead of a single shared Connection.
 *  - Methods that return a "safe" default (false / null / -1) on error also
 *    show a JOptionPane, so this class is coupled to the UI. That was true
 *    in the original code too, so it's kept for now, but it means this
 *    class isn't unit-testable without a display. If you ever split the UI
 *    from the DAO, replace showError()/the inline catches with logging and
 *    let the caller decide how to inform the user.
 */
public class DatabaseManager {

    private static volatile DatabaseManager instance;
    private final Connection connection;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private DatabaseManager() {
        try {
            connection = DatabaseConfig.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    // ================ CORE CRUD OPERATIONS ================

    /** Insert a row and return the generated auto-increment id (-1 if none). */
    public int insert(String table, Map<String, Object> data) throws SQLException {
        if (data.isEmpty()) return 0;

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() == null) continue;
            columns.append(entry.getKey()).append(",");
            placeholders.append("?,");
            values.add(entry.getValue());
        }
        columns.setLength(columns.length() - 1);
        placeholders.setLength(placeholders.length() - 1);

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, placeholders);
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(pstmt, values);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    /** Update rows matching {@code where}. Swallows SQLException (shows a dialog) to match prior behavior. */
    public int update(String table, Map<String, Object> data, String where, Object... params) {
        if (data.isEmpty()) return 0;

        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            setClause.append(entry.getKey()).append("=?,");
            values.add(entry.getValue());
        }
        setClause.setLength(setClause.length() - 1);
        values.addAll(Arrays.asList(params));

        String sql = String.format("UPDATE %s SET %s WHERE %s", table, setClause, where);
        return executeUpdateSafely(sql, values);
    }

    /** Delete rows matching {@code where}. */
    public int delete(String table, String where, Object... params) {
        String sql = String.format("DELETE FROM %s WHERE %s", table, where);
        return executeUpdateSafely(sql, Arrays.asList(params));
    }

    public List<Map<String, Object>> selectAll(String table) throws SQLException {
        return query(String.format("SELECT * FROM %s", table));
    }

    public List<Map<String, Object>> select(String table, String where, Object... params) throws SQLException {
        return query(String.format("SELECT * FROM %s WHERE %s", table, where), params);
    }

    /** Run a fully custom SELECT (no table/where wrapping). */
    public List<Map<String, Object>> select(String rawQuery) throws SQLException {
        return query(rawQuery);
    }

    public Map<String, Object> selectOne(String table, String where, Object... params) {
        try {
            List<Map<String, Object>> results = select(table, where, params);
            return results.isEmpty() ? null : results.get(0);
        } catch (SQLException e) {
            showError("Select Error", e);
            return null;
        }
    }

    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    public int execute(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, Arrays.asList(params));
    }

    /**
     * Public passthrough for running an arbitrary INSERT/UPDATE/DELETE with
     * positional parameters, propagating SQLException to the caller (unlike
     * update()/delete() which swallow it). Equivalent to execute(sql, params) -
     * kept as a separate name since existing forms call it directly.
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, Arrays.asList(params));
    }

    // ================ HELPER METHODS ================

    private int executeUpdate(String sql, List<Object> params) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    /** Same as executeUpdate but catches SQLException and shows a dialog, returning -1 on failure. */
    private int executeUpdateSafely(String sql, List<Object> params) {
        try {
            return executeUpdate(sql, params);
        } catch (SQLException e) {
            showError("Update Error", e);
            return -1;
        }
    }

    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        setParameters(pstmt, Arrays.asList(params));
    }

    private void setParameters(PreparedStatement pstmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(meta.getColumnName(i), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }

    // ================ UTILITY METHODS ================

    public int count(String table, String where, Object... params) throws SQLException {
        String sql = String.format("SELECT COUNT(*) as count FROM %s WHERE %s", table, where);
        List<Map<String, Object>> results = query(sql, params);
        return results.isEmpty() ? 0 : ((Number) results.get(0).get("count")).intValue();
    }

    public boolean exists(String table, String where, Object... params) {
        try {
            return count(table, where, params) > 0;
        } catch (SQLException e) {
            showError("Exists Check Error", e);
            return false;
        }
    }

    // ================ PROJECT-SPECIFIC QUERIES ================

    public List<Map<String, Object>> getActiveUsers() throws SQLException {
        return select("users", "is_active = true ORDER BY full_name");
    }

    public List<Map<String, Object>> getActiveClients() throws SQLException {
        return select("clients", "status = 'active' ORDER BY client_name");
    }

    public List<Map<String, Object>> getActiveCourses() throws SQLException {
        String sql = "SELECT courses.course_id, courses.course_code, courses.course_name, "
                + "courses.description, courses.duration_hours, courses.fee, "
                + "courses.category, users.full_name, courses.start_date, courses.end_date, "
                + "courses.`schedule`, courses.`status` "
                + "FROM courses INNER JOIN users ON courses.instructor_id = users.user_id "
                + "WHERE courses.status IN ('ongoing','upcoming') "
                + "ORDER BY courses.course_id";
        return select(sql);
    }

    public List<Map<String, Object>> getActiveProjects() throws SQLException {
        return select("development_projects",
                "status IN ('planned', 'in_progress', 'testing') ORDER BY deadline");
    }

    public Map<String, Object> getMonthlySummary(int year, int month) throws SQLException {
        String sql = """
            SELECT
                COALESCE(SUM(CASE WHEN source_type = 'course_fee' THEN amount END), 0) as course_income,
                COALESCE(SUM(CASE WHEN source_type = 'support_service' THEN amount END), 0) as support_income,
                COALESCE(SUM(CASE WHEN source_type = 'development_project' THEN amount END), 0) as project_income,
                COALESCE(SUM(amount), 0) as total_income
            FROM income_transactions
            WHERE YEAR(transaction_date) = ?
                AND MONTH(transaction_date) = ?
                AND status = 'received'
            """;
        List<Map<String, Object>> results = query(sql, year, month);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }

    // ================ USER DAO METHODS ================

    public User login(String username, String password) {
        Map<String, Object> user = selectOne("users", "username = ?", username);
        if (user == null) return null;

        String storedHash = (String) user.get("password_hash");
        try {
            if (BCrypt.checkpw(password, storedHash)) {
                return new User((int) user.get("user_id"),
                        (String) user.get("username"),
                        (String) user.get("full_name"),
                        (String) user.get("role"),
                        (boolean) user.get("is_active"));
            }
        } catch (IllegalArgumentException e) {
            showError("Login Error", null, e.getMessage());
        }
        return null;
    }

    public boolean addUser(User user) throws DatabaseException {
        String sql = """
            INSERT INTO users (username, password_hash, email, full_name, role,
                             phone, address, hire_date, salary, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setUserParameters(pstmt, user);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleUserInsertException(e);
            return false;
        }
    }

    public User getUserById(int userId) {
        return getUser("SELECT * FROM users WHERE user_id = ?", userId);
    }

    public User getUserByUsername(String username) {
        return getUser("SELECT * FROM users WHERE username = ?", username);
    }

    private User getUser(String sql, Object param) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? extractUserFromResultSet(rs) : null;
            }
        } catch (SQLException e) {
            showError("User Lookup Error", e);
            return null;
        }
    }

    public List<User> getAllUsers() {
        return getUsers("SELECT * FROM users ORDER BY full_name");
    }

    /**
     * Returns raw rows (not User objects) for forms that need arbitrary
     * columns - e.g. the user-rights form. Replaces the old getAllUsers1(),
     * which silently swallowed its SQLException and returned null with no
     * error shown; this now shows the same error dialog as everything else.
     */
    public List<Map<String, Object>> getAllUsersRaw() {
        try {
            return select("SELECT * FROM users");
        } catch (SQLException e) {
            showError("User List Error", e);
            return null;
        }
    }

    public List<User> getActiveUser() {
        return getUsers("SELECT * FROM users WHERE is_active = true ORDER BY full_name");
    }

    private List<User> getUsers(String sql) {
        List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            return users;
        } catch (SQLException e) {
            showError("User List Error", e);
            return null;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, full_name = ?, role = ?, phone = ?, address = ?, "
                + "hire_date = ?, salary = ?, is_active = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setUserParameters(pstmt, user);
            pstmt.setInt(10, user.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showError("Update User Error", e);
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        return runUpdate("UPDATE users SET password_hash = ? WHERE user_id = ?", newPasswordHash, userId);
    }

    public boolean activateUser(int userId) {
        return runUpdate("UPDATE users SET is_active = true WHERE user_id = ?", userId);
    }

    public boolean deactivateUser(int userId) {
        return runUpdate("UPDATE users SET is_active = false WHERE user_id = ?", userId);
    }

    private boolean runUpdate(String sql, Object... params) {
        try {
            return execute(sql, params) > 0;
        } catch (SQLException e) {
            showError("Update Error", e);
            return false;
        }
    }

    public boolean usernameExists(String username) {
        return exists("users", "username = ?", username);
    }

    public boolean emailExists(String email) {
        return exists("users", "email = ?", email);
    }

    public boolean verifyPassword(int userId, String password) {
        Map<String, Object> user = selectOne("users", "user_id = ?", userId);
        if (user == null) return false;
        String storedHash = (String) user.get("password_hash");
        return BCrypt.checkpw(password, storedHash);
    }

    public List<User> searchUsers(String keyword) throws SQLException {
        String sql = "SELECT * FROM users WHERE full_name LIKE ? OR username LIKE ? OR email LIKE ? ORDER BY full_name";
        String pattern = "%" + keyword + "%";
        List<User> users = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        }
        return users;
    }

    // ================ TABLE / SWING UTILITIES ================

    /**
     * Runs a raw SELECT and returns the live ResultSet, for forms that iterate
     * over it directly (e.g. custom table-loading code).
     *
     * IMPORTANT: unlike the old version, this reuses the shared connection
     * instead of opening a new physical connection per call - the old one
     * leaked a Connection every single call. The caller still owns the
     * returned ResultSet/Statement and MUST close it when done, e.g.:
     *
     *   try (ResultSet rs = dbManager.executeSimpleQuery(sql)) {
     *       while (rs.next()) { ... }
     *   }
     *
     * If a form doesn't close it, statements will leak (though at least not
     * whole connections anymore). Prefer fillTable(...) or query(...) for
     * new code - both close everything internally.
     */
    public ResultSet executeSimpleQuery(String sqlQuery) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sqlQuery);
        } catch (SQLException e) {
            showError("Query Error", e);
            return null;
        }
    }

    /**
     * Runs a query and loads results into the JTable's model positionally -
     * i.e. result column 1 goes into model column 0, column 2 into model
     * column 1, and so on, regardless of column names. Use this when your
     * SELECT's column order already matches the table's column order.
     *
     * Fixed vs. the original: this now reuses the shared connection and
     * closes its own Statement/ResultSet internally, instead of going
     * through the old executeSimpleQuery (which leaked a connection).
     */
    public DefaultTableModel getTableModel(String sqlQuery, JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {
            while (rs.next()) {
                Object[] row = new Object[model.getColumnCount()];
                for (int i = 0; i < model.getColumnCount(); i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            showError("Table Data Error", e);
        }
        return model;
    }

    /**
     * Loads the given query's results into the JTable's existing DefaultTableModel,
     * matching columns by name (column order/names in {@code table} must exist in
     * the result set). Replaces the old executeSimpleQuery-based version, which
     * opened a brand-new Connection per call and never closed it.
     */
    public void fillTable(JTable table, String sql, Object... params) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Map<String, Object>> data = query(sql, params);
            for (Map<String, Object> row : data) {
                Object[] rowData = new Object[model.getColumnCount()];
                for (int i = 0; i < model.getColumnCount(); i++) {
                    rowData[i] = row.get(model.getColumnName(i));
                }
                model.addRow(rowData);
            }
        } catch (SQLException e) {
            showError("Table Data Error", e);
        }
    }

    // ================ PRIVATE HELPER METHODS ================

    private void setUserParameters(PreparedStatement pstmt, User user) throws SQLException {
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPasswordHash());
        pstmt.setString(3, user.getEmail());
        pstmt.setString(4, user.getFullName());
        pstmt.setString(5, user.getRole());
        pstmt.setString(6, user.getPhone());
        pstmt.setString(7, user.getAddress());

        if (user.getHireDate() != null) {
            pstmt.setDate(8, new java.sql.Date(user.getHireDate().getTime()));
        } else {
            pstmt.setDate(8, null);
        }

        pstmt.setDouble(9, user.getSalary());
        pstmt.setBoolean(10, user.isActive());
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));

        java.sql.Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            user.setHireDate(new java.util.Date(hireDate.getTime()));
        }

        user.setSalary(rs.getDouble("salary"));
        user.setActive(rs.getBoolean("is_active"));

        java.sql.Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            user.setCreatedAt(new java.util.Date(createdAt.getTime()));
        }
        return user;
    }

    private void handleUserInsertException(SQLException e) throws DatabaseException {
        if (e.getErrorCode() == 1062) {
            throw DatabaseException.duplicateKeyError("users", "username", e);
        } else if (e.getErrorCode() == 1048) {
            throw DatabaseException.nullConstraintError("users", "email", e);
        } else {
            throw new DatabaseException("INSERT", "users", e);
        }
    }

    private static void showError(String title, SQLException e) {
        String message = "SQL Error: " + e.getMessage()
                + "\nSQL State: " + e.getSQLState()
                + "\nError Code: " + e.getErrorCode();
        showError(title, e, message);
    }

    private static void showError(String title, Exception e, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        System.err.println("Error - " + title + ": " + message);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static Map<String, String> getDatabaseInfo() {
        Map<String, String> info = new HashMap<>();
        try {
            Connection conn = DatabaseConfig.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            info.put("Database Product", meta.getDatabaseProductName());
            info.put("Database Version", meta.getDatabaseProductVersion());
            info.put("Driver Name", meta.getDriverName());
            info.put("Driver Version", meta.getDriverVersion());
            info.put("URL", meta.getURL());
            info.put("User", meta.getUserName());
        } catch (SQLException e) {
            showError("Database Info Error", e);
        }
        return info;
    }
}