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

        return "member/login";
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

    @RequestMapping("/member/search_result")
    public String search_result( Model model,
                                 @RequestParam(name = "query1", required = false, defaultValue = "") String query1,
                                 @RequestParam(name = "query2", required = false, defaultValue = "") String query2,
                                 @RequestParam(name = "query3", required = false, defaultValue = "") String query3,
                                 @RequestParam(name = "query4", required = false, defaultValue = "") String query4,
                                 @RequestParam(name = "query5", required = false, defaultValue = "") String query5,
                                 @RequestParam(name = "year", required = false, defaultValue = "") String year1,
                                 @RequestParam(name = "month", required = false, defaultValue = "") String month1,
                                 @RequestParam(name = "day", required = false, defaultValue = "") String day1,
                                 @RequestParam(name = "year2", required = false, defaultValue = "") String year2,
                                 @RequestParam(name = "month2", required = false, defaultValue = "") String month2,
                                 @RequestParam(name = "day2", required = false, defaultValue = "") String day2,
                                 @RequestParam(name = "select_day_week_month", required = false, defaultValue = "") String timeunit,
                                 @RequestParam(name = "device", required = false, defaultValue = "") String coverage,
                                 @RequestParam(name = "gender", required = false, defaultValue = "") String gender,
                                 @RequestParam(name = "age", required = false, defaultValue = "") String[] age,
                                 @RequestParam(name = "year3", required = false, defaultValue = "") String year3,
                                 @RequestParam(name = "month3", required = false, defaultValue = "") String month3,
                                 @RequestParam(name = "day3", required = false, defaultValue = "") String day3,
                                 @RequestParam(name = "year4", required = false, defaultValue = "") String year4,
                                 @RequestParam(name = "month4", required = false, defaultValue = "") String month4,
                                 @RequestParam(name = "day4", required = false, defaultValue = "") String day4,
                                 @RequestParam(name = "select_day_week_month2", required = false, defaultValue = "") String timeunit2,
                                 @RequestParam(name = "device2", required = false, defaultValue = "") String coverage2,
                                 @RequestParam(name = "gender2", required = false, defaultValue = "") String gender2,
                                 @RequestParam(name = "age2", required = false, defaultValue = "") String[] age2,
                                 @RequestParam(name = "addFavorite", required = false, defaultValue = "") String addFavorite) throws JSONException  {
        List<String> query1XAxisData = memberService.apiResponseX(query1, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
        List<String> query1XAxis2Data = memberService.apiResponseX(query1, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
        List<String> query1SeriesData = memberService.apiResponseY(query1, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
        List<String> query1Series2Data = memberService.apiResponseY(query1, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);

        model.addAttribute("xAxisData", query1XAxisData);
        model.addAttribute("xAxis2Data", query1XAxis2Data);
        model.addAttribute("seriesData1", query1SeriesData);
        model.addAttribute("series2Data1", query1Series2Data);
        model.addAttribute("query1", query1);
        model.addAttribute("query2", query2);
        model.addAttribute("query3", query3);
        model.addAttribute("query4", query4);
        model.addAttribute("query5", query5);

        if (query2!=""){
            List<String> query2SeriesData = memberService.apiResponseY(query2, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query2Series2Data = memberService.apiResponseY(query2, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData2", query2SeriesData);
            model.addAttribute("series2Data2", query2Series2Data);
        }

        if (query3!=""){
            List<String> query3SeriesData = memberService.apiResponseY(query3, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query3Series2Data = memberService.apiResponseY(query3, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData3", query3SeriesData);
            model.addAttribute("series2Data3", query3Series2Data);
        }

        if (query4!=""){
            List<String> query4SeriesData = memberService.apiResponseY(query4, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query4Series2Data = memberService.apiResponseY(query4, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData4", query4SeriesData);
            model.addAttribute("series2Data4", query4Series2Data);
        }

        if (query5!=""){
            List<String> query5SeriesData = memberService.apiResponseY(query5, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query5Series2Data = memberService.apiResponseY(query5, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData5", query5SeriesData);
            model.addAttribute("series2Data5", query5Series2Data);
        }
        return "member/search_result";
    }


    @GetMapping("/index_result")
    public String search(){
        return "index_result";
    }

    @PostMapping("/index_result")
    public String searchURL(Principal principal,
                            Model model) throws JSONException {
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