package com.sparta.hanghaeboard.service;

import com.sparta.hanghaeboard.dto.LoginRequestDto;
import com.sparta.hanghaeboard.dto.SignupRequestDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.entity.User;
import com.sparta.hanghaeboard.jwt.JwtUtil;
import com.sparta.hanghaeboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private  final JwtUtil jwtUtil;

    @Transactional
    public StatusResponseDto<String> signup(SignupRequestDto signupRequestDto) {
        Optional<User> found = userRepository.findByUsername(signupRequestDto.getUsername());
        //회원 중복 확인
        if(found.isPresent()){
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        //사용자 등록
        User user = User.builder()
                .requestDto(signupRequestDto)
                .build();
        userRepository.save(user);

        return  StatusResponseDto.success("success SignUp!") ;
    }

    @Transactional(readOnly = true)
    public StatusResponseDto<String> login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        //사용자 확인
       User user = userRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));
       if (!user.getPassword().equals(password)){
           throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
       }
        return StatusResponseDto.success(jwtUtil.createToken(user.getUsername(), user.getRole()));
    }


}
