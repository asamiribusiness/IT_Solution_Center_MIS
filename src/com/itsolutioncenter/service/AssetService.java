
package com.itsolutioncenter.service;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.Validator;
import java.sql.SQLException;
import java.util.*;

public class AssetService {
    private DatabaseManager db = DatabaseManager.getInstance();
   
    /**
     * Add new asset
     */
    public int addAsset(String tag, String name, String category, String serialNo,
                       Date purchaseDate, double cost, String location) throws SQLException {
       
        Validator.validateRequired(tag, "Asset Tag");
        Validator.validateRequired(name, "Asset Name");
       
        Map<String, Object> asset = new HashMap<>();
        asset.put("asset_tag", tag);
        asset.put("asset_name", name);
        asset.put("category", category);
        asset.put("serial_number", serialNo);
        asset.put("purchase_date", purchaseDate);
        asset.put("purchase_cost", cost);
        asset.put("current_value", cost); // Initially same as purchase cost
        asset.put("status", "available");
        asset.put("location", location);
       
        return db.insert("assets", asset);
    }
   
    /**
     * Get all assets
     */
    public List<Map<String, Object>> getAllAssets() throws SQLException {
//        String sql = "SELECT a.*, u.full_name as assigned_to_name " +
//                     "FROM assets a " +
//                     "LEFT JOIN users u ON a.assigned_to = u.user_id " +
//                     "ORDER BY a.asset_name";
 //       return db.query(sql);
 String query="SELECT assets.asset_id,assets.asset_tag,assets.asset_name,assets.category," +
    "assets.serial_number,assets.purchase_date,assets.purchase_cost,assets.current_value," +
    "assets.`status`,users.full_name,assets.location FROM assets " +
    "INNER JOIN users ON users.user_id = assets.assigned_to";
 return db.query(query);
    }
   
    /**
     * Get asset by ID
     */
    public Map<String, Object> getAssetById(int assetId) throws SQLException {
        String sql = "SELECT a.*, u.full_name as assigned_to_name, u.username " +
                     "FROM assets a " +
                     "LEFT JOIN users u ON a.assigned_to = u.user_id " +
                     "WHERE a.asset_id = ?";
       
        List<Map<String, Object>> results = db.query(sql, assetId);
        return results.isEmpty() ? null : results.get(0);
    }
   
    /**
     * Update asset information
     */
    public boolean updateAsset(int assetId, Map<String, Object> updateData) throws SQLException {
        if (updateData.isEmpty()) return false;
       
        int rows = db.update("assets", updateData, "asset_id = ?", assetId);
        return rows > 0;
    }
   
    /**
     * Assign asset to user
     */
    public boolean assignAsset(int assetId, int userId, String location) throws SQLException {
        Map<String, Object> update = new HashMap<>();
        update.put("assigned_to", userId);
        update.put("status", "in_use");
        update.put("location", location);
       
        int rows = db.update("assets", update, "asset_id = ?", assetId);
        return rows > 0;
    }
   
    /**
     * Unassign asset (make available)
     */
    public boolean unassignAsset(int assetId, String location) throws SQLException {
        Map<String, Object> update = new HashMap<>();
        update.put("assigned_to", null);
        update.put("status", "available");
        update.put("location", location);
       
        int rows = db.update("assets", update, "asset_id = ?", assetId);
        return rows > 0;
    }
   
    /**
     * Mark asset for maintenance
     */
    public boolean markForMaintenance(int assetId, String notes) throws SQLException {
        Map<String, Object> update = new HashMap<>();
        update.put("status", "maintenance");
       
        if (notes != null && !notes.trim().isEmpty()) {
            update.put("notes", notes);
        }
       
        int rows = db.update("assets", update, "asset_id = ?", assetId);
        return rows > 0;
    }
   
    /**
     * Retire asset
     */
    public boolean retireAsset(int assetId, String notes) throws SQLException {
        Map<String, Object> update = new HashMap<>();
        update.put("status", "retired");
        update.put("current_value", 0);
       
        if (notes != null && !notes.trim().isEmpty()) {
            update.put("notes", notes);
        }
       
        int rows = db.update("assets", update, "asset_id = ?", assetId);
        return rows > 0;
    }
   
    /**
     * Search assets
     */
    public List<Map<String, Object>> searchAssets(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAssets();
        }
       
        String sql = "SELECT a.*, u.full_name as assigned_to_name " +
                     "FROM assets a " +
                     "LEFT JOIN users u ON a.assigned_to = u.user_id " +
                     "WHERE a.asset_tag LIKE ? OR " +
                     "      a.asset_name LIKE ? OR " +
                     "      a.serial_number LIKE ? OR " +
                     "      a.category LIKE ? " +
                     "ORDER BY a.asset_name";
       
        String searchTerm = "%" + keyword + "%";
        return db.query(sql, searchTerm, searchTerm, searchTerm, searchTerm);
    }
   
    /**
     * Get assets by category
     */
    public List<Map<String, Object>> getAssetsByCategory(String category) throws SQLException {
        String sql = "SELECT a.*, u.full_name as assigned_to_name " +
                     "FROM assets a " +
                     "LEFT JOIN users u ON a.assigned_to = u.user_id " +
                     "WHERE a.category = ? " +
                     "ORDER BY a.asset_name";
        return db.query(sql, category);
    }
   
    /**
     * Get assets by status
     */
    public List<Map<String, Object>> getAssetsByStatus(String status) throws SQLException {
        String sql = "SELECT a.*, u.full_name as assigned_to_name " +
                     "FROM assets a " +
                     "LEFT JOIN users u ON a.assigned_to = u.user_id " +
                     "WHERE a.status = ? " +
                     "ORDER BY a.asset_name";
        return db.query(sql, status);
    }
   
    /**
     * Get user's assigned assets
     */
    public List<Map<String, Object>> getUserAssets(int userId) throws SQLException {
        return db.select("assets", "assigned_to = ? ORDER BY asset_name", userId);
    }
   
    /**
     * Get available assets
     */
    public List<Map<String, Object>> getAvailableAssets() throws SQLException {
        return getAssetsByStatus("available");
    }
   
    /**
     * Get asset statistics
     */
    public Map<String, Object> getAssetStatistics() throws SQLException {
        String sql = "SELECT " +
                     "  COUNT(*) as total_assets, " +
                     "  SUM(purchase_cost) as total_cost, " +
                     "  SUM(current_value) as total_value, " +
                     "  SUM(CASE WHEN status = 'available' THEN 1 ELSE 0 END) as available_assets, " +
                     "  SUM(CASE WHEN status = 'in_use' THEN 1 ELSE 0 END) as in_use_assets, " +
                     "  SUM(CASE WHEN status = 'maintenance' THEN 1 ELSE 0 END) as maintenance_assets, " +
                     "  SUM(CASE WHEN status = 'retired' THEN 1 ELSE 0 END) as retired_assets, " +
                     "  COUNT(DISTINCT category) as categories_count " +
                     "FROM assets";
       
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
   
    /**
     * Get category-wise summary
     */
    public List<Map<String, Object>> getCategorySummary() throws SQLException {
        String sql = "SELECT " +
                     "  category, " +
                     "  COUNT(*) as count, " +
                     "  SUM(purchase_cost) as total_cost, " +
                     "  AVG(purchase_cost) as avg_cost, " +
                     "  SUM(CASE WHEN status = 'available' THEN 1 ELSE 0 END) as available, " +
                     "  SUM(CASE WHEN status = 'in_use' THEN 1 ELSE 0 END) as in_use " +
                     "FROM assets " +
                     "GROUP BY category " +
                     "ORDER BY count DESC";
       
        return db.query(sql);
    }
   
    /**
     * Update asset depreciation
     */
    public boolean updateDepreciation(int assetId, double newValue) throws SQLException {
        Map<String, Object> update = Collections.singletonMap("current_value", newValue);
        int rows = db.update("assets", update, "asset_id = ?", assetId);
        return rows > 0;
    }
}
