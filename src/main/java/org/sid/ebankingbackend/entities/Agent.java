package org.sid.ebankingbackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("AGENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Agent extends User {
    private String agentCode; // Optional: Unique code for agents
}
