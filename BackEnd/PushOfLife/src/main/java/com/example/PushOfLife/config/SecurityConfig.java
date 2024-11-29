package com.example.PushOfLife.config;

import com.example.PushOfLife.jwt.JwtFilter;
import com.example.PushOfLife.jwt.JwtUtil;
import com.example.PushOfLife.jwt.LoginFilter;
import com.example.PushOfLife.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.formLogin(formLogin -> formLogin.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());

        http.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Collections.singletonList("https://k11b303.p.ssafy.io"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);
                configuration.setExposedHeaders(Arrays.asList("Authorization"));


                return configuration;
            }
        }));

//        // 허용 URL 및 인증 설정
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/POL/users/login", "/POL/users/join", "POL/test").permitAll()
                .requestMatchers( "/login/**",
                        "/h2-console/**",
                        "/swagger-ui/**", "swagger-resources/**", "/faviocn.ico","/error","/v3/api-docs/**" ).permitAll()
//                .anyRequest().authenticated() // 그 외 요청은 인증 필요
                .anyRequest().permitAll() // 개발용(전체 요청 허용)
        );

        http
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        // 폼 로그인을 위한 LoginFilter 추가
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);


        // 세션 사용 안 함 (JWT 사용 시 필요)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
