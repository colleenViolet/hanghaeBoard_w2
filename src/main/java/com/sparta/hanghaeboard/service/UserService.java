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
    public ResponseEntity<StatusResponseDto> signup(SignupRequestDto signupRequestDto) {
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

        return ResponseEntity.ok(new StatusResponseDto(HttpStatus.OK.value(), "success SignUp!"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<StatusResponseDto>login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        //사용자 확인
        Optional<User> check = userRepository.findByUsername(username);
        if (check.isEmpty()){
            return exceptionResponse("유효하지 않은 사용자명입니다.");
        } else if (!(check.get().getPassword().equals(password))) {
            return exceptionResponse("비밀번호가 일치 하지 않습니다. ");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(check.get().getUsername(), check.get().getRole()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(new StatusResponseDto(HttpStatus.OK.value(), "success Login!"));
    }

    //공통 예외처리 가능
    private ResponseEntity<StatusResponseDto> exceptionResponse(String message){
        return  ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), message));
    }
}
