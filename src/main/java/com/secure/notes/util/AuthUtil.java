package com.secure.notes.util;

import com.secure.notes.models.User;
import com.secure.notes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    @Autowired
    public UserRepository userRepository;

    public Long LoggedInUserId() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    public User LoggedInUser() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
