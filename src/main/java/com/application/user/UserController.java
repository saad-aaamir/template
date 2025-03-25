package com.application.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Tag(name = "Protected User", description = "Apis for the authorized Users.")
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/ping")
    public String hello() {
        return "Hi user, protected controller!";
    }

}
