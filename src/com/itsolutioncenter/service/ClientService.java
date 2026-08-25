package com.itsolutioncenter.service;

import com.itsolutioncenter.dao.DatabaseManager;
import java.sql.SQLException;
import java.util.*;

public class ClientService {
    private DatabaseManager db = DatabaseManager.getInstance();
   
    /**
     * Add new client
     */
    public int addClient(String name, String contactPerson, String email,
                        String phone, String address, String clientType,String note) throws SQLException {
       
        //Validator.validateRequired(name, "Client Name");
       
        Map<String, Object> client = new HashMap<>();
        client.put("client_name", name);
        client.put("contact_person", contactPerson);
        client.put("email", email);
        client.put("phone", phone);
        client.put("address", address);
        client.put("client_type", clientType);
        client.put("status", "active");
        client.put("note", note);
       
        return db.insert("clients", client);
    }
   
    /**
     * Get all clients
     */
    public List<Map<String, Object>> getAllClients() throws SQLException {
        return db.select("clients", "1 ORDER BY client_name");
    }
   
    /**
     * Get active clients
     */
    public List<Map<String, Object>> getActiveClients() throws SQLException {
        return db.getActiveClients();
    }
   
    /**
     * Get client by ID
     */
    public Map<String, Object> getClientById(int clientId) throws SQLException {
        return db.selectOne("clients", "client_id = ?", clientId);
    }
   
    /**
     * Update client information
     */
    public boolean updateClient(int clientId, String name, String contactPerson,
                               String email, String phone, String address,
                               String clientType, String status) throws SQLException {
       
        Map<String, Object> update = new HashMap<>();
        if (name != null) update.put("client_name", name);
        if (contactPerson != null) update.put("contact_person", contactPerson);
        if (email != null) update.put("email", email);
        if (phone != null) update.put("phone", phone);
        if (address != null) update.put("address", address);
        if (clientType != null) update.put("client_type", clientType);
        if (status != null) update.put("status", status);
       
        if (update.isEmpty()) return false;
       
        int rows = db.update("clients", update, "client_id = ?", clientId);
        return rows > 0;
    }
   
    /**
     * Delete client (soft delete - set status to inactive)
     */
    public boolean deleteClient(int clientId) throws SQLException {
        Map<String, Object> update = Collections.singletonMap("status", "inactive");
        int rows = db.update("clients", update, "client_id = ?", clientId);
        return rows > 0;
    }
   
    /**
     * Search clients by name or email
     */
    public List<Map<String, Object>> searchClients(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllClients();
        }
       
        String sql = "SELECT * FROM clients WHERE " +
                     "client_name LIKE ? OR " +
                     "contact_person LIKE ? OR " +
                     "email LIKE ? OR " +
                     "phone LIKE ? " +
                     "ORDER BY client_name";
       
        String searchTerm = "%" + keyword + "%";
        return db.query(sql, searchTerm, searchTerm, searchTerm, searchTerm);
    }
   
    /**
     * Get clients by type
     */
    public List<Map<String, Object>> getClientsByType(String type) throws SQLException {
        return db.select("clients", "client_type = ? AND status = 'active' ORDER BY client_name", type);
    }
   
    /**
     * Get client statistics
     */
    public Map<String, Object> getClientStatistics() throws SQLException {
        String sql = "SELECT " +
                     "  COUNT(*) as total_clients, " +
                     "  SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) as active_clients, " +
                     "  SUM(CASE WHEN client_type = 'individual' THEN 1 ELSE 0 END) as individual_clients, " +
                     "  SUM(CASE WHEN client_type = 'business' THEN 1 ELSE 0 END) as business_clients, " +
                     "  SUM(CASE WHEN client_type = 'government' THEN 1 ELSE 0 END) as government_clients " +
                     "FROM clients";
       
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
   
    /**
     * Get client projects
     */
    public List<Map<String, Object>> getClientProjects(int clientId) throws SQLException {
        String sql = "SELECT p.*, u.full_name as manager_name " +
                     "FROM development_projects p " +
                     "LEFT JOIN users u ON p.manager_id = u.user_id " +
                     "WHERE p.client_id = ? " +
                     "ORDER BY p.start_date DESC";
        return db.query(sql, clientId);
    }
   
    /**
     * Get client support tickets
     */
    public List<Map<String, Object>> getClientTickets(int clientId) throws SQLException {
        String sql = "SELECT t.*, u.full_name as assigned_to_name " +
                     "FROM support_tickets t " +
                     "LEFT JOIN users u ON t.assigned_to = u.user_id " +
                     "WHERE t.client_id = ? " +
                     "ORDER BY t.reported_date DESC";
        return db.query(sql, clientId);
    }
   
    /**
     * Get client financial summary
     */
    public Map<String, Object> getClientFinancialSummary(int clientId) throws SQLException {
        String sql = "SELECT " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE source_type = 'development_project' AND " +
                     "   source_id IN (SELECT project_id FROM development_projects WHERE client_id = ?)) as project_income, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE source_type = 'support_service' AND " +
                     "   source_id IN (SELECT ticket_id FROM support_tickets WHERE client_id = ?)) as support_income";
       
        List<Map<String, Object>> results = db.query(sql, clientId, clientId);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
}