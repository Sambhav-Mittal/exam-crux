package com.examCrux.webApp.dto;

import com.examCrux.webApp.entities.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {

    private User user;
    private String token;

}
