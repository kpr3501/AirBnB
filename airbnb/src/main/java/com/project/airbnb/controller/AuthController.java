package com.project.airbnb.controller;


import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.LoginDto;
import com.project.airbnb.dto.LoginResponseDto;
import com.project.airbnb.dto.SignUpRequestDto;
import com.project.airbnb.dto.UserDto;
import com.project.airbnb.security.AuthService;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<UserDto> Signup(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.signUp(signUpRequestDto),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> Login(@RequestBody LoginDto loginDto, HttpServletRequest httpsServletRequest, HttpServletResponse httpServletResponse){
        String[] token = authService.login(loginDto);
        Cookie cookie = new Cookie("refreshedToken",token[1]);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(token[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> RefreshToken(HttpServletRequest httpsServletRequest){
        String refreshToken = Arrays.stream(httpsServletRequest.getCookies()).
                              filter(cookies -> "refreshedToken".equals(cookies.getName()))
                              .findFirst()
                              .map(Cookie::getValue)
                              .orElseThrow(()-> new AuthenticationServiceException("Refresh token not found inside the cookies"));
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));


    }


}
