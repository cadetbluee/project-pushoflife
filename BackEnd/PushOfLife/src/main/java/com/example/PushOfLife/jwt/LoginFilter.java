package com.example.PushOfLife.jwt;

import com.example.PushOfLife.dto.user.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);
            String phone = credentials.get("user_phone");
            String password = credentials.get("user_password");

            //검증을 위한 토큰 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phone, password, null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            System.out.println("error!!!!!!!");
            throw new RuntimeException(e);
        }

    }

    // 로그인 성공
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){
        System.out.println("successfulAuthentication");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String phone = customUserDetails.getUserPhone();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String accessToken = jwtUtil.createAccessToken(phone, role);

        response.setHeader("Authorization", "Bearer " + accessToken);

        System.out.println(accessToken);
        System.out.println("successful authentication");
    }

    // 로그인 실패
    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        System.out.println("unsuccessfulAuthentication");
        response.setStatus(401);

    }
}
