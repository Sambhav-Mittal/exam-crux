package com.login.Login.entities;

import lombok.Getter;

@Getter
public enum ResourceType {
    SYLLABUS("syllabus"),
    PYQ("pyq"),
    ASSIGNMENT("assignment"),
    NOTE("note"),
    BOOK("book"),
    VIDEO("video");

    private final String type;

    ResourceType(String type) {
        this.type = type;
    }

}
