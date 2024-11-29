package com.example.PushOfLife.service;

import com.example.PushOfLife.dto.user.CustomUserDetails;
import com.example.PushOfLife.entity.UserEntity;
import com.example.PushOfLife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userPhone) throws UsernameNotFoundException {

        //DB에서 조회
        UserEntity userData = userRepository.findByUserPhone(userPhone);
        System.out.println(userData.getUserPhone());

        if (userData != null) {
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(userData);
        }
        return null;
    }
}
