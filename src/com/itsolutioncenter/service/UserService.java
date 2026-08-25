package com.itsolutioncenter.service;
/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.exception.DatabaseException;
import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Permission;
import com.itsolutioncenter.model.User;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
   
   private static DatabaseManager dbManager = DatabaseManager.getInstance();
   private Map<String, List<Permission>> rolePermissions = new HashMap<>();
   private static ResultSet rs;
   private static String query;

      public boolean changePassword(int userId, String currentPassword, String newPassword){
            String newHashPassword=BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return dbManager.updatePassword(userId, newHashPassword);
    }
    
    public List<Map<String, Object>> searchUsers(String keyword) throws DatabaseException, SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return dbManager.selectAll("users");
        }
        String sql = "SELECT * FROM users WHERE " +
                     "username LIKE ? OR " +
                     "full_name LIKE ? OR " +
                     "email LIKE ? OR " +
                     "phone LIKE ? " +
                     "ORDER BY full_name";  
        String searchTerm = "%" + keyword + "%";
        return dbManager.select(sql, searchTerm, searchTerm, searchTerm, searchTerm);
    }
    public Map<String, Object> getUserStatistics() throws DatabaseException, SQLException {
        String sql = "SELECT " +
                     "  COUNT(*) as total_users, " +
                     "  SUM(CASE WHEN is_active = true THEN 1 ELSE 0 END) as active_users, " +
                     "  SUM(CASE WHEN role = 'admin' THEN 1 ELSE 0 END) as admin_count, " +
                     "  SUM(CASE WHEN role = 'manager' THEN 1 ELSE 0 END) as manager_count, " +
                     "  SUM(CASE WHEN role = 'employee' THEN 1 ELSE 0 END) as employee_count, " +
                     "  SUM(CASE WHEN role = 'intern' THEN 1 ELSE 0 END) as intern_count " +
                     "FROM users";
        List<Map<String, Object>> results = dbManager.select(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }

// Register new user
    public int registerUser(String username, String hashPassword,String email,
                                String fullName, String role, String phone,String address,Date hireDate,double salary){
       
    try
    {
        Map<String, Object> data = new HashMap<>();
      data.put("username", username);
      data.put("password_hash", hashPassword);
      data.put("email", email);
      data.put("full_Name", fullName);
      data.put("role",role);
      data.put("phone",phone);
      data.put("address",address);
      data.put("hire_date", hireDate);
      data.put("salary",salary);
      data.put("is_active",1);
      
       int row=dbManager.insert("users", data);  
       return row;
    }catch(SQLException e)
    {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return -1;
    }
    }
public int updateUser(int userID,String username,String email,String fullName,
                        String role,String phone,String address,Date hireDate,double salary)
{
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("user_id", userID);
        updateData.put("username", username);
        updateData.put("email", email);
        updateData.put("full_name",fullName);
        updateData.put("role",role);
        updateData.put("phone", phone);
        updateData.put("address", address);
        updateData.put("hire_date",hireDate);
        updateData.put("salary", salary);
        Map<String, Object> existingUser = dbManager.selectOne("users","email = ? AND user_id != ?", 
                updateData.get("email"), userID);
            if (existingUser != null) {
               JOptionPane.showMessageDialog(null, "Email already registered to another user");
               return -1;
            }
       int rows=dbManager.update("users", updateData, "user_id = ?", userID);

       return rows;
}   
    public int changeUserType(String userType,String username)
    {
        try
        {
            int userID = 0;
             List<Map<String,Object>> users=dbManager.query("select user_id from users where username=?", username);
          for(Map<String,Object> s: users){
          userID=Integer.parseInt(s.get("user_id").toString());}
            query="update users set role=? where user_id=?";
            return dbManager.executeUpdate(query, userType,userID);
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return -1;
        }
    }
    // Get all users
    public List<Map<String,Object>> getAllUsers() {
        return dbManager.getAllUsers1();
    }
    // Get active users
    public List<User> getActiveUser() {
        return dbManager.getActiveUser();
    }
    // Get user by ID
    public User getUserById(int userId) {
        return dbManager.getUserById(userId);
    }
    public boolean verifyPassword(int userID,String password)
    {
        return dbManager.verifyPassword(userID, password);   
    }
    // Activate user
    public boolean activateUser(String username) {
        int userID = 0;
             try
             {
                 List<Map<String,Object>> users=dbManager.query("select user_id from users where username=?", username);
                 for(Map<String,Object> s: users){
                 userID=Integer.parseInt(s.get("user_id").toString());}
             }catch(SQLException e)
             {
                 JOptionPane.showMessageDialog(null, e.getMessage());
             }
        return dbManager.activateUser(userID);
    } 
    // Deactivate user
    public boolean deactivateUser(String username) {
        int userID = 0;
             try
             {
                 List<Map<String,Object>> users=dbManager.query("select user_id from users where username=?", username);
                 for(Map<String,Object> s: users){
                 userID=Integer.parseInt(s.get("user_id").toString());}
             }catch(SQLException e)
             {
                 JOptionPane.showMessageDialog(null, e.getMessage());
             }
        return dbManager.deactivateUser(userID);
    } 
     public  List<Map<String, Object>> getAll(String tableName) throws DatabaseException, SQLException {
        return dbManager.selectAll(tableName);
    }
    public Map<String, Object> getById(String tableName, int id) throws DatabaseException {
        return dbManager.selectOne(tableName, tableName + "_id = ?", id);
    }
    public boolean deleteById(String tableName, int id) throws DatabaseException {
        int rows = dbManager.delete(tableName, tableName + "_id = ?", id);
        return rows > 0;
    }
    // Get permissions for user type
    public static Permission getPermissions(String userType) {
        try {
           String sql = "SELECT * FROM permissions WHERE user_type = '"+userType+"'";
            rs=DatabaseManager.executeSimpleQuery(sql);
            if (rs.next()) {
                return new Permission(
                    rs.getBoolean("can_view"),
                    rs.getBoolean("can_add"),
                    rs.getBoolean("can_edit"),
                    rs.getBoolean("can_delete"),
                    rs.getBoolean("can_report"),
                    rs.getBoolean("can_export"),
                    rs.getBoolean("can_manage_users")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return null;
    }
    // Check specific permission
    public static boolean hasPermission(String userType, String permission) {
        Permission perm = getPermissions(userType);
        if (perm == null) return false;
       
        switch (permission.toLowerCase()) {
            case "view": return perm.canView();
            case "add": return perm.canAdd();
            case "edit": return perm.canEdit();
            case "delete": return perm.canDelete();
            case "report": return perm.canReport();
            case "export": return perm.canExport();
            case "manage_users": return perm.canManageUsers();
            default: return false;
        }
    }
}