package com.example.PushOfLife.controller;

import com.example.PushOfLife.dto.user.UpdateRequestDTO;
import com.example.PushOfLife.dto.user.UpdateResponseDTO;
import com.example.PushOfLife.dto.user.UserJoinDTO;
import com.example.PushOfLife.dto.user.UserListDTO;
import com.example.PushOfLife.entity.UserEntity;
import com.example.PushOfLife.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/POL/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody UserJoinDTO joinDTO) {
        try{
            Boolean result = userService.checkPassword(joinDTO.getUserPassword1(), joinDTO.getUserPassword2());
            if(!result){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password incorrect");
            }
            else{
                userService.registerUser(joinDTO);
                return ResponseEntity.status(HttpStatus.OK).body("user registered successfully.");
            }
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/info")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody UpdateRequestDTO updateDTO) {
        UserEntity findUser = userService.findByRequest(request);
        if (findUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UpdateResponseDTO responseDTO = userService.updateUserInfo(findUser, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserListDTO> userList = userService.getUserList();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/info")
    public ResponseEntity<?> userInfo(HttpServletRequest request) {
        UserEntity findUser = userService.findByRequest(request);
        if (findUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UpdateResponseDTO responseDTO = userService.getUserInfo(findUser);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        UserEntity findUser = userService.findByRequest(request);
        if (findUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userService.deleteUser(findUser);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }
}
