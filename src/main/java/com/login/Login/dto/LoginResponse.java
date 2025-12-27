package com.login.Login.dto;

import com.login.Login.entities.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {

    private User user;
    private String token;

}
