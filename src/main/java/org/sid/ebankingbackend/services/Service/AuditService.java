package org.sid.ebankingbackend.services.Service;

import org.sid.ebankingbackend.entities.AuditLog;

import java.util.List;

public interface AuditService {
    public void log(String action, String username, String details);
    public List<AuditLog> getLogsByUsername(String username);
    public List<AuditLog> getAllLogs();
    String getCurrentUsername();
}
