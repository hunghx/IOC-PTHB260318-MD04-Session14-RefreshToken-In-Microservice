package org.example.identityservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.models.dto.req.LoginReq;
import org.example.identityservice.models.dto.req.RefreshReq;
import org.example.identityservice.models.dto.req.RegisterReq;
import org.example.identityservice.models.entities.RefreshToken;
import org.example.identityservice.models.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("Register Successfully");
    }
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test(){
        return "Bạn đang truy cập với quyền admin";
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshReq refreshReq){
        // xử lý
        // B1 : kiểm tra refreshToken có ồn tại trong db hay ko
        // B2 kiểm tra hạn sử dụng và trạng thái revoked
        // B3: thu hồi token cũ và cấp phát mới
        // B4: trả về
        return ResponseEntity.ok(authService.refresh(refreshReq));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        return ResponseEntity.ok(authService.login(req));
    }

}
