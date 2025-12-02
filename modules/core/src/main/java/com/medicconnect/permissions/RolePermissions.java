package com.medicconnect.permissions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RolePermissions {

    private static final Map<String, List<Permission>> rolePermissionMap = new HashMap<>();

    static {
        List<Permission> allPermissions = Arrays.asList(Permission.values());
        rolePermissionMap.put("ADMIN", allPermissions);
        rolePermissionMap.put("DOCTOR", allPermissions);
        rolePermissionMap.put("PHARMACIST", allPermissions);
        rolePermissionMap.put("BILLING", allPermissions);
    }

    public static List<Permission> getPermissions(String role) {
        return rolePermissionMap.getOrDefault(role.toUpperCase(), Arrays.asList());
    }

    public static void setPermissions(String role, List<Permission> permissions) {
        rolePermissionMap.put(role.toUpperCase(), permissions);
    }
}
