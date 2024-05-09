package com.example.demo.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberDto  {

    String userName;
    String userId;
    String phone;
    String password;
    LocalDateTime regDt;

    boolean emailAuthYn;
    LocalDateTime emailAuthDt;
    String emailAuthKey;

    String resetPasswordKey;
    LocalDateTime resetPasswordLimitDt;

    boolean adminYn;

    //추가컬럼
    long totalCount;
    long seq;

}
