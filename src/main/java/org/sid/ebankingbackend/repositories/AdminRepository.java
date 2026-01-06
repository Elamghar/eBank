package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
