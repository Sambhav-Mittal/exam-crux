package com.login.Login.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.login.Login.serializer.UserSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "app_user")
@Builder
@AllArgsConstructor
@JsonSerialize(using = UserSerializer.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String profileImage;

    @Column(nullable = false)
    private Boolean active = true;
    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, columnDefinition = "timestamp(6) without time zone")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "timestamp(6) without time zone")
    private LocalDateTime updatedAt;

    public User() {
        // This constructor is required by JPA for entity instantiation
    }


}
