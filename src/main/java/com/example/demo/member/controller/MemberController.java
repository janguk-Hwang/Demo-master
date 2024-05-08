package com.example.demo.member.controller;

import com.example.demo.member.model.MemberInput;
import com.example.demo.member.model.ResetPasswordInput;
import com.example.demo.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MemberController {
    private final MemberService memberService;
    private JSONObject[] resultsArray;

    @RequestMapping("/")
    public String login() {

        //return "member/login";
        return "member/index";
    }

    @GetMapping("/member/register")
    public String register() {

        return "member/register";
    }

    @PostMapping("/member/register")
    public String registerSubmit(Model model, HttpServletRequest request
            , MemberInput parameter) {

        boolean result = memberService.register(parameter);

        model.addAttribute("result", result);

        return "member/register_complete";
        //서비스의 비지니스로직을 활용해  멤버인풋 파라미터를 받아와 데이터베이스에 정상적으로 저장되면 회원가입이 완료되었습니다 아니면 실패하였습니다.
        //그리고 이메일을 보낸다
    }


    @GetMapping("/member/email-auth")
    public String emailAuth(Model model, HttpServletRequest request) {

        String uuid = request.getParameter("id");

        boolean result = memberService.emailAuth(uuid);
        model.addAttribute("result",result);

        return "member/email_auth";
        //받은 이메일에 가입완료를 클릭하면 정해진 링크로 이동하게되는데 그링크에 id가 랜덤으로 지정되어 보내지게된다.
        //거기서 id를 서비스의 비지니스 로직(emailAuth)을 활용해 boolean 타입으로 반환한다.
        //email_auth.html에서 result의 boolean타입을 활용해 뷰를 출력한다.
    }

    @GetMapping("/index_result")
    public String search(Model model,
                         @RequestParam(name = "query1", required = false, defaultValue = "") String query1,
                         @RequestParam(name = "query2", required = false, defaultValue = "") String query2,
                         @RequestParam(name = "query3", required = false, defaultValue = "") String query3,
                         @RequestParam(name = "query4", required = false, defaultValue = "") String query4,
                         @RequestParam(name = "query5", required = false, defaultValue = "") String query5,
                         @RequestParam(name = "year", required = false, defaultValue = "") String year,
                         @RequestParam(name = "month", required = false, defaultValue = "") String month1,
                         @RequestParam(name = "day", required = false, defaultValue = "") String day1,
                         @RequestParam(name = "year2", required = false, defaultValue = "") String year2,
                         @RequestParam(name = "month2", required = false, defaultValue = "") String month2,
                         @RequestParam(name = "day2", required = false, defaultValue = "") String day2,
                         @RequestParam(name = "select_day_week_month", required = false, defaultValue = "") String timeunit,
                         @RequestParam(name = "device[]", required = false, defaultValue = "") String coverage,
                         @RequestParam(name = "gender[]", required = false, defaultValue = "") String gender,
                         @RequestParam(name = "age[]", required = false, defaultValue = "") String[] age,
                         Principal principal) throws JSONException {

        List<String> query1XAxisData = memberService.apiResponseX(query1, year, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
        List<String> query1SeriesData = memberService.apiResponseY(query1, year, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);

        model.addAttribute("xAxisData", query1XAxisData);
        model.addAttribute("seriesData1", query1SeriesData);
        model.addAttribute("query1", query1);
        model.addAttribute("query2", query2);
        model.addAttribute("query3", query3);
        model.addAttribute("query4", query4);
        model.addAttribute("query5", query5);

        String userName = principal.getName();
        String favoriteURL = "";

        memberService.getDbFavriteURL(userName);

        for (int i = 0; i < memberService.getDbFavriteURL(userName).size(); i++) {
            favoriteURL = memberService.getDbFavriteURL(userName).get(i);
            if (i==0){
                model.addAttribute("url1", favoriteURL);
            } else if (i==1) {
                model.addAttribute("url2", favoriteURL);
            } else if (i==2) {
                model.addAttribute("url3", favoriteURL);
            } else if (i==3) {
                model.addAttribute("url4", favoriteURL);
            } else if (i==4) {
                model.addAttribute("url5", favoriteURL);
            }
        }


        if (query2!=""){
            List<String> query2SeriesData = memberService.apiResponseY(query2, year, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            model.addAttribute("seriesData2", query2SeriesData);
        }

        if (query3!=""){
            List<String> query3SeriesData = memberService.apiResponseY(query3, year, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            model.addAttribute("seriesData3", query3SeriesData);
        }

        if (query4!=""){
            List<String> query4SeriesData = memberService.apiResponseY(query4, year, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            model.addAttribute("seriesData4", query4SeriesData);
        }

        if (query5!=""){
            List<String> query5SeriesData = memberService.apiResponseY(query5, year, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            model.addAttribute("seriesData5", query5SeriesData);
        }

        return "index_result";
    }

    @PostMapping("/index_result")
    public String searchURL(@RequestParam(name = "url", required = false) String url,
                            Principal principal) {
        String userName = principal.getName();

        if (url!=""){
            memberService.setDbFavoritesURL(url,userName);
        }

        return "index_result";
    }


    @GetMapping("/member/reset/password")
    public String resetPassword(Model model, HttpServletRequest request) {
        String uuid = request.getParameter("id");

        boolean result = memberService.checkResetPassword(uuid);

        model.addAttribute("result", result);

        return "member/reset_password";
    }

    @GetMapping("/member/info")
    public String memberInfo() {

        return "member/info";
    }

    @GetMapping("/member/find/password")
    public String findPassword() {

        return "member/find_password";
    }

    @PostMapping("/member/find/password")
    public String findPasswordSubmit(Model model, ResetPasswordInput parameter) {

        boolean result = false;
        try {
            result = memberService.sendResetPassword(parameter);
        }catch (Exception e) {
        }

        model.addAttribute("result", result);

        return "member/find_password_result";
    }

    @PostMapping("/member/reset/password")
    public String resetPasswordSubmit(Model model, ResetPasswordInput parameter) {
        boolean result = false;
        try{
            result = memberService.resetPassword(parameter.getId(), parameter.getPassword());
        } catch (Exception e) {
        }

        model.addAttribute("result", result);

        return "member/reset_password_result";
    }

}