package com.examCrux.webApp.entities;

import lombok.Getter;

@Getter
public enum RoleType {
    ADMIN("admin"),
    USER("user");

    private final String type;

    RoleType(String type) {
        this.type = type;
    }

}
