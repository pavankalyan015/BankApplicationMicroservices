package com.bankapplicationmicroservices.api_gateway.service;

import com.bankapplicationmicroservices.api_gateway.dto.AuthResponse;
import com.bankapplicationmicroservices.api_gateway.dto.RegisterRequest;
import com.bankapplicationmicroservices.api_gateway.entity.Role;
import com.bankapplicationmicroservices.api_gateway.entity.User;
import com.bankapplicationmicroservices.api_gateway.repository.RoleRepository;
import com.bankapplicationmicroservices.api_gateway.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public UserService(UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.roles = roles; this.encoder = encoder; this.jwt = jwt;
    }

    public void register(RegisterRequest req) {
        if (users.existsByUsername(req.getUsername())) {
            throw new RuntimeException("username already exists");
        }
        Set<Role> roleEntities = (req.getRoles() == null ? Set.<String>of() : req.getRoles())
                .stream()
                .map(r -> roles.findByName(r).orElseGet(() -> roles.save(new Role(null, r))))
                .collect(Collectors.toSet());
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setEnabled(true);
        u.setRoles(roleEntities);
        users.save(u);
    }

/*    public AuthResponse login(String username, String rawPassword) {
        User user = users.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("bad credentials"));
        if (!user.isEnabled() || !encoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("bad credentials");
        }
        var roles = user.getRoles().stream().map(Role::getName).toList();
        String token = jwt.generateToken(user.getId(), user.getUsername(), roles); // <-- include userId
        return new AuthResponse(token, jwt.getExpiresIn(), user.getId());
    }*/

    public AuthResponse login(String username, String rawPassword) {
        User user = users.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("bad credentials"));

        if (!user.isEnabled() || !encoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("bad credentials");
        }

        var roles = user.getRoles().stream().map(Role::getName).toList();
        String token = jwt.generateToken(user.getId(), user.getUsername(), roles);

        return new AuthResponse(token, jwt.getExpiresIn(), user.getId());
    }

}
