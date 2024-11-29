package com.example.PushOfLife.service;

import com.example.PushOfLife.dto.user.UpdateRequestDTO;
import com.example.PushOfLife.dto.user.UpdateResponseDTO;
import com.example.PushOfLife.dto.user.UserJoinDTO;
import com.example.PushOfLife.dto.user.UserListDTO;
import com.example.PushOfLife.entity.UserEntity;
import com.example.PushOfLife.jwt.JwtUtil;
import com.example.PushOfLife.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;


    public void registerUser(UserJoinDTO joinDTO){
        UserEntity user = UserEntity.builder()
                .userName(joinDTO.getUserName())
                .password(bCryptPasswordEncoder.encode(joinDTO.getUserPassword1()))
                .userBirthday(joinDTO.getUserBirthday())
                .userGender(joinDTO.getUserGender())
                .userDisease(joinDTO.getUserDisease())
                .userPhone(joinDTO.getUserPhone())
                .userProtector(joinDTO.getUserProtector())
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    public boolean checkPassword(String password1, String password2){
        if (password1 == null && password2 == null){
            return true;
        }
        return password1.equals(password2);
    }

    public UserEntity findByRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        UserEntity user = userRepository.findByUserPhone(jwtUtil.getPhone(token));
        return user;
    }

    public UpdateResponseDTO updateUserInfo(UserEntity user, UpdateRequestDTO updateDTO){
        // 우선 비밀번호가 null인지 확인합니다.
        if (updateDTO.getUserPassword1() == null && updateDTO.getUserPassword2() == null) {
            user.updateInfo(updateDTO, null); // 비밀번호 변경 없이 다른 정보만 업데이트
            userRepository.save(user);
        } else {
            // 비밀번호가 null이 아닌 경우에만 일치하는지 확인
            Boolean result = checkPassword(updateDTO.getUserPassword1(), updateDTO.getUserPassword2());
            if (result) {
                String password = bCryptPasswordEncoder.encode(updateDTO.getUserPassword1());
                user.updateInfo(updateDTO, password); // 인코딩된 비밀번호로 업데이트
                userRepository.save(user);
            } else {
                throw new RuntimeException("password incorrect");
            }
        }

        UpdateResponseDTO updateResponseDTO = new UpdateResponseDTO();
        updateResponseDTO.fromEntity(user);
        return updateResponseDTO;
    }

    public List<UserListDTO> getUserList(){
        List<UserEntity> findUserList = userRepository.findAll();
        List<UserListDTO> allUsers = new ArrayList<>();
        for (UserEntity user : findUserList) {
            UserListDTO userInfo = new UserListDTO();
            userInfo.fromEntity(user);
            allUsers.add(userInfo);
        }
        return allUsers;
    }

    public UpdateResponseDTO getUserInfo(UserEntity user){
        UserEntity findUser = userRepository.findByUserPhone(user.getUserPhone());
        if (findUser == null){
            throw new RuntimeException("User not found");
        }
        else{
            UpdateResponseDTO responseDTO = new UpdateResponseDTO();
            responseDTO.fromEntity(findUser);
            return responseDTO;
        }
    }

    public void deleteUser(UserEntity user){
        userRepository.delete(user);
    }
}
