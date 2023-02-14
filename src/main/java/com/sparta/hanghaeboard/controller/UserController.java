package com.sparta.hanghaeboard.controller;

import com.sparta.hanghaeboard.dto.LoginRequestDto;
import com.sparta.hanghaeboard.dto.SignupRequestDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

//    @GetMapping("/signup")
//    public ModelAndView signupPage(){
//        return new ModelAndView("signup");
//    }
//
//    @GetMapping("/login")
//    public ModelAndView loginPage(){
//        return new ModelAndView("login");
//    }

    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<StatusResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), result.getFieldError().getDefaultMessage()));
        }
        return userService.signup(signupRequestDto);
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<StatusResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){

        return userService.login(loginRequestDto);
    }
}

