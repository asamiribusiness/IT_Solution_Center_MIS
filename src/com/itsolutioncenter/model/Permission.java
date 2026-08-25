package com.itsolutioncenter.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class Permission {
      private boolean canView;
    private boolean canAdd;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canReport;
    private boolean canExport;
    private boolean canManageUsers;
   private Map<String, List<Permission>> rolePermissions = new HashMap<>();
    public Permission(boolean view, boolean add, boolean edit, boolean delete, boolean canReport, boolean export, boolean manageUsers) {
        this.canView = view;
        this.canAdd = add;
        this.canEdit = edit;
        this.canDelete = delete;
        this.canReport=canReport;
        this.canExport = export;
        this.canManageUsers = manageUsers;
    }
   
    // Getters
    public boolean canView() { return canView; }
    public boolean canAdd() { return canAdd; }
    public boolean canEdit() { return canEdit; }
    public boolean canDelete() { return canDelete; }
    public boolean canReport() { return canReport;}
    public boolean canExport() { return canExport; }
    public boolean canManageUsers() { return canManageUsers; }
       /**
     * Get user's permissions as a map for quick lookup
     */
    public Map<String, Boolean> getUserPermissionsMap(String role) {
        Map<String, Boolean> permMap = new HashMap<>();
        List<Permission> permissions = rolePermissions.get(role);
       
        if (permissions != null) {
            for (Permission perm : permissions) {
                String key = String.format("%s.%s.%s",
                        perm.canAdd(), perm.canDelete(), perm.canEdit());
                permMap.put(key, perm.canManageUsers());
            }
        }
       
        return permMap;
    }
}
