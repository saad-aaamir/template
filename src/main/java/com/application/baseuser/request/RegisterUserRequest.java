package com.application.baseuser.request;

import com.application.common.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private RoleType role;

}
