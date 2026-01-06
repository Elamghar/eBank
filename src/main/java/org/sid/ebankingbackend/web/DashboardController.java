package org.sid.ebankingbackend.web;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {
    @GetMapping("/admin-dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        return ResponseEntity.ok("Admin Dashboard Data");
    }

    @GetMapping("/customer-dashboard")
    public ResponseEntity<?> getCustomerDashboard() {
        return ResponseEntity.ok("Customer Dashboard Data");
    }
}
