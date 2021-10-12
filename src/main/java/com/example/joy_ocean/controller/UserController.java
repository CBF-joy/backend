package com.example.joy_ocean.controller;

import com.example.joy_ocean.exception.ResourceNotFoundException;
import com.example.joy_ocean.model.User;
import com.example.joy_ocean.payload.UserIdentityAvailability;
import com.example.joy_ocean.payload.UserProfile;
import com.example.joy_ocean.repository.UserRepository;
import com.example.joy_ocean.security.CurrentUser;
import com.example.joy_ocean.security.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController{
    
    @Autowired
    private UserRepository userRepository;

    // 현재 로그인한 계정의 정보
    @GetMapping("/user/me")
    public UserPrincipal getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserPrincipal userPrincipal = new UserPrincipal(currentUser.getId(), currentUser.getName(),
                currentUser.getUsername(), currentUser.getAddress(), currentUser.getAuthorities());
        return userPrincipal;
    }


    // 유저이름 체크
    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    // 유저이메일 체크
    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    // 유저정보 - 마이페이지
    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(),
                user.getEmail(), user.getRoles(), user.getAddress(), user.getPublic_key());

        return userProfile;
    }
}