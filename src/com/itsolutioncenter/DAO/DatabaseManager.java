package com.itsolutioncenter.dao;

import com.itsolutioncenter.config.DatabaseConfig;
import com.itsolutioncenter.exception.DatabaseException;
import com.itsolutioncenter.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static Connection connection; 
    private static PreparedStatement prs;
    // Singleton instance
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
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
    /**
     * Insert data into any table
     */
//    public int insert(String table, Map<String, Object> data) {
//        if (data.isEmpty()) return 0;
//       
//        StringBuilder columns = new StringBuilder();
//        StringBuilder placeholders = new StringBuilder();
//        List<Object> values = new ArrayList<>();
//        for (Map.Entry<String, Object> entry : data.entrySet()) {
//            columns.append(entry.getKey()).append(",");
//            placeholders.append("?,");
//            values.add(entry.getValue());
//        }
//        // Remove last comma
//        columns.setLength(columns.length() - 1);
//        placeholders.setLength(placeholders.length() - 1);
//        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
//                table, columns, placeholders);
//        return executeUpdate(sql, values);
//        
//    }
     // ================ INSERT WITH AUTO-INCREMENT ================
   
    /**
     * Insert data and return auto-generated ID
     */
    public int insert(String table, Map<String, Object> data) throws SQLException {
        if (data.isEmpty()) return 0;
       
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();
       
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                columns.append(entry.getKey()).append(",");
                placeholders.append("?,");
                values.add(entry.getValue());
            }
        }
        // Remove last comma
        columns.setLength(columns.length() - 1);
        placeholders.setLength(placeholders.length() - 1);
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",table, columns, placeholders);
        try (PreparedStatement pstmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            } 
            // Execute insert
            int rowsAffected = pstmt.executeUpdate();
          
            if (rowsAffected > 0) {
                // Get generated auto-increment key
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        }
    }
    /**
     * Update data in any table
     * @param table
     * @param data
     * @param where
     * @param params
     * @return 
     */
    public int update(String table, Map<String, Object> data,String where, Object... params) {
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
        return executeUpdate(sql, values);
    }
     /**
     * Delete from table
     * @param table
     * @param where
     * @param params
     * @return 
     */
    public int delete(String table, String where, Object... params) {
        try
        {
            String sql = String.format("DELETE FROM %s WHERE %s", table, where);
        return executeUpdate(sql, params);
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return -1;
        }
    }
    /**
     * Select all records
     * @return 
     */
    public List<Map<String, Object>> selectAll(String table) throws SQLException {
        String sql = String.format("SELECT * FROM %s", table);
        return query(sql);
    }
   
    /**
     * Select with WHERE clause
     */
    public List<Map<String, Object>> select(String table, String where,
                                           Object... params) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s", table, where);
        return query(sql, params);
    }
   public List<Map<String, Object>> select(String query) throws SQLException {
       // String sql = String.format("SELECT * FROM %s WHERE %s", table, where);
        return query(query);
    }
    /**
     * Select single record
     */
    public Map<String, Object> selectOne(String table, String where,Object... params) {
        try
        {
            List<Map<String, Object>> results = select(table, where, params);
        return results.isEmpty() ? null : results.get(0);
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }
    }   
    /**
     * Execute custom query
     */
    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
           
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }
    /**
     * Execute custom update
     */
    public int execute(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, params);
    }
   
    // ================ HELPER METHODS ================
   
    public int executeUpdate(String sql, Object... params) throws SQLException{
        return executeUpdate(sql, Arrays.asList(params));
    }
   public static int executeUpdate(String query,String msg)
   {
       try
       {
           prs=connection.prepareStatement(query);
           JOptionPane.showMessageDialog(null, msg);
           return prs.executeUpdate();
       }
       catch(SQLException e)
       {
           JOptionPane.showMessageDialog(null, e.getMessage());
           return -1;
       }
   }
    private int executeUpdate(String sql, List<Object> params) {
        try
        {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }}
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
                return -1;
            }
    }
   
    private void setParameters(PreparedStatement pstmt, Object... params)
            throws SQLException {
        setParameters(pstmt, Arrays.asList(params));
    }
   
    private void setParameters(PreparedStatement pstmt, List<Object> params)
            throws SQLException {
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
        try
        {
            return count(table, where, params) > 0;
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
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
       String query="SELECT courses.course_id,courses.course_code,courses.course_name," +
       "courses.description,courses.duration_hours,courses.fee, " +
       "courses.category, users.full_name, courses.start_date,courses.end_date," +
       "courses.`schedule`,courses.`status` FROM users INNER JOIN courses ON " +
       "courses.instructor_id = users.user_id where status IN('ongoing','upcoming') order by course_id";
       return select(query);
        //return select("courses", "status IN ('ongoing', 'upcoming') ORDER BY course_id");
    }
   
   // Project methods
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
   
       // ================ AUTHENTICATION ================
   
      public User login(String username, String password) {
            Map<String,Object> user = selectOne("users", "username = ?", username);
            if (user == null) return null;
        String storedHash = (String)user.get("password_hash");
          try
          {
                if(BCrypt.checkpw(password,storedHash))
            {
                 return new User((int)(user.get("user_id")),
                        (String)user.get("username"),
                        (String)user.get("full_name"),
                 (String)user.get("role"),(boolean)user.get("is_active"));
            }
          }  catch(IllegalArgumentException e)
                        {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                        }
        return null;
    }
//    public Map<String, Object> authenticateUser(String username, String password) throws SQLException {
//        Map<String, Object> user = selectOne("users", "username = ?", username);
//        if (user == null) return null;
//        String storedHash = (String)user.get("password_hash");
//                if(BCrypt.checkpw(password,storedHash))
//            return user;
//           else
//        return null;
//   }
    public boolean addUser(User user) throws DatabaseException {
        String sql = """
            INSERT INTO users (username, password_hash, email, full_name, role,
                             phone, address, hire_date, salary, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
       
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setUserParameters(pstmt, user);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
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
        try
        {
              try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? extractUserFromResultSet(rs) : null;
            }
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }
    }
    public List<User> getAllUsers() {
        return getUsers("SELECT * FROM users ORDER BY full_name");
    }
        ///////////temporary for user rights form
   public List<Map<String, Object>> getAllUsers1()  {
       try
       {
           return select("select * from users ");
       }catch(SQLException e)
       {
           return null;
       }
        
    }
    public List<User> getActiveUser() {
        return getUsers("SELECT * FROM users WHERE is_active = true ORDER BY full_name");
    }
    private List<User> getUsers(String sql)  {
        try
        {
            List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }
    }
    public boolean updateUser(User user) {
        try
        {
            String sql = "UPDATE users SET username = ?, email = ?, full_name = ?, role = ?, phone = ?, address = ?,"
                + " hire_date = ?, salary = ?, is_active = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setUserParameters(pstmt, user);
            pstmt.setInt(10, user.getUserId());
            return pstmt.executeUpdate() > 0;
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try {
            return executeUpdate(sql, newPasswordHash, userId) > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return false;
        }
    }
    public boolean activateUser(int userId)  {
        String sql = "UPDATE users SET is_active = true WHERE user_id = ?";
        try {
            return executeUpdate(sql, userId) > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return false;
        }
    }
    public boolean deactivateUser(int userId)  {
        String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
        try {
            return executeUpdate(sql, userId) > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return false;
        }
    }
    public boolean usernameExists(String username) {
        return exists("users", "username = ?", username);
    }
    public boolean verifyPassword(int userID,String password)
    {
         Map<String,Object> user = selectOne("users", "user_id = ?", userID);
            if (user == null) return false;
        String storedHash = (String)user.get("password_hash");
            if(BCrypt.checkpw(password,storedHash)){ return true;}
            else 
            {  //JOptionPane.showMessageDialog(null, "Current Password= "+password+" and hash password= "+storedHash);
                return false;}
    }
    public boolean emailExists(String email) {
        return exists("users", "email = ?", email);
    }
    public List<User> searchUsers(String keyword) throws SQLException {////////باید بازنگری گردد و یکی بماند
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
    // ================ TABLE UTILITIES ================
   
     /**
     * Execute SELECT query and return ResultSet
     * @param sqlQuery
     * @return 
     * @throws java.sql.SQLException
     */
    public static ResultSet executeSimpleQuery(String sqlQuery) {
        try
        {
        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sqlQuery);
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }
    }
    /**
     * Get data for JTable
     */
    public DefaultTableModel getTableModel(String sqlQuery, JTable userTable/*, Object... params*/) {
       //DefaultTableModel model = new DefaultTableModel();
       DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        try (ResultSet rs = executeSimpleQuery(sqlQuery)) {
        // Clear existing data
        model.setRowCount(0);  
        // Add new data (columns will keep their design-time names)
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
    public void fillTable(JTable table, String sql, Object... params) throws SQLException {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
       
        List<Map<String, Object>> data = query(sql, params);
        for (Map<String, Object> row : data) {
            Object[] rowData = new Object[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                String columnName = model.getColumnName(i);
                rowData[i] = row.get(columnName);
            }
            model.addRow(rowData);
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
        if (e.getErrorCode() == 1062) { // Duplicate entry
            throw DatabaseException.duplicateKeyError("users", "username", e);
        } else if (e.getErrorCode() == 1048) { // Null constraint
            throw DatabaseException.nullConstraintError("users", "email", e);
        } else {
            throw new DatabaseException("INSERT", "users", e);
        }
    }
        /**
     * Show error message
     */
    private static void showError(String title, SQLException e) {
        String message = "SQL Error: " + e.getMessage() +
                        "\nSQL State: " + e.getSQLState() +
                        "\nError Code: " + e.getErrorCode();
        JOptionPane.showMessageDialog(null,
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
        System.err.println("❌ " + title + ": " + message);
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
       /**
     * Get database information
     */
    public static Map<String, String> getDatabaseInfo() {
        Map<String, String> info = new HashMap<>();  
        try {
            Connection conn = DatabaseConfig.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            info.put("\nDatabase Product", meta.getDatabaseProductName());
            info.put("\nDatabase Version", meta.getDatabaseProductVersion());
            info.put("\nDriver Name", meta.getDriverName());
            info.put("\nDriver Version", meta.getDriverVersion());
            info.put("\nURL", meta.getURL());
            info.put("\nUser", meta.getUserName());
        } catch (SQLException e) {
            showError("Database Info Error", e);
        }
        return info;
    }
}