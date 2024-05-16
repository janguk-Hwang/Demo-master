package com.example.demo.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Member {

    @Id
    private String userId;

    private String userName;
    private String phone;
    private String password;
    private LocalDateTime regDt;

    private boolean emailAuthYn;
    private LocalDateTime emailAuthDt;
    private String emailAuthKey;

    private String resetPasswordKey;
    private LocalDateTime resetPasswordLimitDt;

    @Column(length = 50000)
    private String favorite1;
    @Column(length = 50000)
    private String favorite2;
    @Column(length = 50000)
    private String favorite3;
    @Column(length = 50000)
    private String favorite4;
    @Column(length = 50000)
    private String favorite5;

    //관리자 여부를 지정할꺼냐?
    //회원에 따른 ROLE을 지정할거냐??
    //준회원/정회원/특별회원/관리자
    //ROLE_SEMIM_USER, ROLE_USER, ROLE_SPECIAL_USER, ROLE_ADMIN
    //준회원/정회원/특별회원
    //관리자???
    private boolean adminYn;
}