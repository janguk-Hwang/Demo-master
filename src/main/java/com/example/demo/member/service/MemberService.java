package com.example.demo.member.service;

import com.example.demo.admin.dto.MemberDto;
import com.example.demo.admin.model.MemberParam;
import com.example.demo.member.model.MemberInput;
import com.example.demo.member.model.ResetPasswordInput;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

public interface MemberService extends UserDetailsService {

    boolean register(MemberInput parameter);

    //uuid에 해당하는 계정을 활성화함.
    boolean emailAuth(String uuid);

    /* 입력한 이메일로 비밀번호 초기화 정보를 전송 */
    boolean sendResetPassword(ResetPasswordInput parameter);

    /*
    -입력 받은 uuid에 대해서 password로 초기화함
    */
    boolean resetPassword(String id, String password);

    /*
     *입력받은 uuid값이 유효한지 확인
     */
    boolean checkResetPassword(String uuid);

    /*회원의 목록을 리턴함(관리자페이지에서만 사용가능)*/
    List<MemberDto> list(MemberParam parameter);

    /*회원 상세 정보*/
    MemberDto detail(String userId);
    List<String> apiResponseX(String query, String year, String  month1, String day1, String year2, String month2, String day2, String timeunit, String coverage, String gender, String[] age);
    List<String> apiResponseY(String query, String year, String  month1, String day1, String year2, String month2, String day2, String timeunit, String coverage, String gender, String[] age);

    boolean setDbFavoriteURL(String url, String username);
    ArrayList<String> getDbFavriteURL(String username);
    ArrayList<String> extractUrl(String str);
    boolean setRemoveAllUrl(String username);

    boolean setRemoveUrl1(String username);

    boolean setRemoveUrl2(String username);

    boolean setRemoveUrl3(String username);

    boolean setRemoveUrl4(String username);

    boolean setRemoveUrl5(String username);

}