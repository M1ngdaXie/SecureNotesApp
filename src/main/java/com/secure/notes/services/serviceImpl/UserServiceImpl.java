package com.secure.notes.services.serviceImpl;

import com.secure.notes.dtos.UserDTO;
import com.secure.notes.models.AppRole;
import com.secure.notes.models.PasswordResetToken;
import com.secure.notes.models.Role;
import com.secure.notes.models.User;
import com.secure.notes.repositories.PasswordResetTokenRepository;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.repositories.UserRepository;
import com.secure.notes.services.TotpService;
import com.secure.notes.services.UserService;
import com.secure.notes.util.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Value("${frontend.url}")
    private String frontendUrl;
    @Autowired
    private EmailService emailService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private TotpService totpService;


    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDTO getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }
    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateAccountCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateUserPassword(Long userId, String newPassword) {
        try {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error updating password: " + e.getMessage());
        }
    }

    @Override
    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("User not found with email: " + email));
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(3600); // Token valid for 1 hour
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(passwordResetToken);
        String url = frontendUrl + "/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(user.getEmail(), url);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));
        if(passwordResetToken.isUsed()){
            throw new RuntimeException("Password reset token has already been used");
        }
        if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Password reset token has expired");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(User newUser) {
        // Handle password (existing code)
        if (newUser.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        } else {
            // Generate random secure password for OAuth2 users
            String securePassword = UUID.randomUUID().toString();
            newUser.setPassword(passwordEncoder.encode(securePassword));
        }

        // Fix for NULL account_expiry_date
        if (newUser.getAccountExpiryDate() == null) {
            newUser.setAccountExpiryDate(java.time.LocalDate.now().plusYears(1));
        }

        // Fix for NULL credentials_expiry_date
        if (newUser.getCredentialsExpiryDate() == null) {
            newUser.setCredentialsExpiryDate(java.time.LocalDate.now().plusYears(1));
        }

        // Set other account status fields if they haven't been set
        if (!newUser.isAccountNonExpired()) {
            newUser.setAccountNonExpired(true);
        }

        if (!newUser.isCredentialsNonExpired()) {
            newUser.setCredentialsNonExpired(true);
        }

        if (!newUser.isAccountNonLocked()) {
            newUser.setAccountNonLocked(true);
        }

        return userRepository.save(newUser);
    }
    @Override
    public GoogleAuthenticatorKey generateTwoFactorSecret(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        GoogleAuthenticatorKey key = totpService.generateSecretKey();
        user.setTwoFactorSecret(key.getKey());
        userRepository.save(user);
        return key;
    }
    @Override
    public boolean verifyTwoFactorCode(Long userId, int code) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        String secret = user.getTwoFactorSecret();
        if (secret == null || secret.isEmpty()) {
            throw new RuntimeException("Two-factor authentication is not set up for this user");
        }
        return totpService.verifyCode(secret, code);
    }
    @Override
    public void enableTwoFactorAuthentication(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }
    @Override
    public void disableTwoFactorAuthentication(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null); // Optionally clear the secret
        userRepository.save(user);
    }


}
