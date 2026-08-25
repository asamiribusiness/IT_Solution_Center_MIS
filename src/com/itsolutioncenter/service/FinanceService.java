package com.itsolutioncenter.service;

import com.itsolutioncenter.dao.DatabaseManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinanceService {
    private DatabaseManager db = DatabaseManager.getInstance();
   
    public int addIncome(Date date,String reference,String source, String payer,double amount,
                         String method,String description, int receivedBy,String status) throws SQLException {
       
        Map<String, Object> income = new HashMap<>();
        income.put("transaction_date",date);
        income.put("reference_number", date);
        income.put("source_type", source);
        income.put("payer_name", payer);
        income.put("amount", amount);
        income.put("payment_method", method);
        income.put("description", description);
        income.put("received_by", receivedBy);
        income.put("status", status);
       
        return db.insert("income_transactions", income);
    }
   
    public int addExpense(Date date,String reference,String category, String payee,double amount,String method,String description, 
                         int approved, String status) throws SQLException {
       
        Map<String, Object> expense = new HashMap<>();
        expense.put("transaction_date",date);
        expense.put("reference_number",reference);
        expense.put("category", category);
        expense.put("payee_name", payee);
        expense.put("amount", amount);
        expense.put("payment_method", method);
        expense.put("description", description);
        expense.put("approved_by",approved);
        expense.put("status", status);
       
        return db.insert("expense_transactions", expense);
    }
   
    public double getBalance() throws SQLException {
        String incomeSql = "SELECT COALESCE(SUM(amount), 0) as total FROM income_transactions WHERE status = 'received'";
        String expenseSql = "SELECT COALESCE(SUM(amount), 0) as total FROM expense_transactions WHERE status = 'paid'";
       
        List<Map<String, Object>> incomeResult = db.query(incomeSql);
        List<Map<String, Object>> expenseResult = db.query(expenseSql);
       
        double totalIncome = incomeResult.isEmpty() ? 0 : ((Number) incomeResult.get(0).get("total")).doubleValue();
        double totalExpense = expenseResult.isEmpty() ? 0 : ((Number) expenseResult.get(0).get("total")).doubleValue();
       
        return totalIncome - totalExpense;
    }
}