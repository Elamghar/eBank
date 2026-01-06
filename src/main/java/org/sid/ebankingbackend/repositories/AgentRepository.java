package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entities.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
}