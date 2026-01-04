package com.examCrux.webApp.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Response<T> {
    public T data;
    @Column(nullable = false)
    public int httpStatusCode;
    @Column(nullable = false)
    public String message;
}
