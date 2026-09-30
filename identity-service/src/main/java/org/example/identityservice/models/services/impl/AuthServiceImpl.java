package org.example.identityservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.exceptions.BadRequestException;
import org.example.identityservice.exceptions.NotFoundException;
import org.example.identityservice.models.constants.RoleName;
import org.example.identityservice.models.dto.req.LoginReq;
import org.example.identityservice.models.dto.req.RegisterReq;
import org.example.identityservice.models.dto.res.JwtRes;
import org.example.identityservice.models.entities.RefreshToken;
import org.example.identityservice.models.entities.Role;
import org.example.identityservice.models.entities.User;
import org.example.identityservice.models.repositories.RefreshTokenRepository;
import org.example.identityservice.models.repositories.RoleRepository;
import org.example.identityservice.models.repositories.UserRepository;
import org.example.identityservice.models.services.AuthService;
import org.example.identityservice.security.jwt.JwtUtils;
import org.example.identityservice.security.principal.MyUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager manager;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void register(RegisterReq req) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new NotFoundException("Role not found")));

        User user = User.builder()
                .fullName(req.fullName())
                .username(req.username())
                .password(encoder.encode(req.password()))
                .roles(roles)
                .build();

        userRepository.save(user);
    }

    @Override
    public JwtRes login(LoginReq req) {
        Authentication authentication;

        try {
            authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(req.username(),req.password()));
        } catch (AuthenticationException e) {
            throw new BadRequestException("Username or password is incorrect");
        }

//        SecurityContextHolder.getContext().setAuthentication(authentication);
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(" Username not found"));

        // Refresh Token là 1 chỗi UUID
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken token = new RefreshToken(
                null,
                refreshToken,
                Instant.now().plus(7, ChronoUnit.DAYS),
                false,
                user
        );
        // lưu laại token vào db
        refreshTokenRepository.save(token);

        return new JwtRes(
                jwtUtils.generateToken(userDetails.getUser()),
                refreshToken,
                "Bearer",
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );
    }
}
