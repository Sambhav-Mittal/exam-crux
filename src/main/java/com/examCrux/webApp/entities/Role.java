package com.examCrux.webApp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String roleName;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}

