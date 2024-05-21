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
import java.util.ArrayList;
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

    @GetMapping("/favorite_url")
    public String favorite_url( Model model,
                                @RequestParam(name = "url", required = false, defaultValue = "") String url,
                                Principal principal) throws JSONException  {
        String userName = principal.getName();
        String favoriteURL = "";

        if (!url.equals("")){
            memberService.setDbFavoriteURL(url,userName);
        }

        boolean isTrue1 = true;
        boolean isTrue2 = true;
        boolean isTrue3 = true;
        boolean isTrue4 = true;
        boolean isTrue5 = true;

        for (int i = 0; i < memberService.getDbFavriteURL(userName).size(); i++) {
            favoriteURL = memberService.getDbFavriteURL(userName).get(i);
            ArrayList<String> arrayList = memberService.extractUrl(favoriteURL);
            ArrayList<String> arrayList2 = memberService.getAge1(favoriteURL);
            ArrayList<String> arrayList3 = memberService.getAge2(favoriteURL);

            if (arrayList2.size()==0){
                arrayList2.add("전체");
            }

            if (arrayList3.size()==0){
                arrayList3.add("전체");
            }

            if (favoriteURL!=""){
                for (int j = 0; j < arrayList2.size(); j++) {
                    if (arrayList2.get(j).equals("1")){
                        arrayList2.set(j,"~12");
                    } else if (arrayList2.get(j).equals("2")) {
                        arrayList2.set(j,"13~18");
                    } else if (arrayList2.get(j).equals("3")) {
                        arrayList2.set(j,"19~24");
                    } else if (arrayList2.get(j).equals("4")) {
                        arrayList2.set(j,"25~29");
                    } else if (arrayList2.get(j).equals("5")) {
                        arrayList2.set(j,"30~34");
                    } else if (arrayList2.get(j).equals("6")) {
                        arrayList2.set(j,"35~39");
                    } else if (arrayList2.get(j).equals("7")) {
                        arrayList2.set(j,"40~44");
                    } else if (arrayList2.get(j).equals("8")) {
                        arrayList2.set(j,"45~49");
                    } else if (arrayList2.get(j).equals("9")) {
                        arrayList2.set(j,"50~54");
                    } else if (arrayList2.get(j).equals("10")) {
                        arrayList2.set(j,"54~59");
                    } else if (arrayList2.get(j).equals("11")) {
                        arrayList2.set(j,"60~");
                    }
                }
                for (int j = 0; j < arrayList3.size(); j++) {
                    if (arrayList3.get(j).equals("1")){
                        arrayList3.set(j,"~12");
                    } else if (arrayList3.get(j).equals("2")) {
                        arrayList3.set(j,"13~18");
                    } else if (arrayList3.get(j).equals("3")) {
                        arrayList3.set(j,"19~24");
                    } else if (arrayList3.get(j).equals("4")) {
                        arrayList3.set(j,"25~29");
                    } else if (arrayList3.get(j).equals("5")) {
                        arrayList3.set(j,"30~34");
                    } else if (arrayList3.get(j).equals("6")) {
                        arrayList3.set(j,"35~39");
                    } else if (arrayList3.get(j).equals("7")) {
                        arrayList3.set(j,"40~44");
                    } else if (arrayList3.get(j).equals("8")) {
                        arrayList3.set(j,"45~49");
                    } else if (arrayList3.get(j).equals("9")) {
                        arrayList3.set(j,"50~54");
                    } else if (arrayList3.get(j).equals("10")) {
                        arrayList3.set(j,"54~59");
                    } else if (arrayList3.get(j).equals("11")) {
                        arrayList3.set(j,"60~");
                    }
                }
            }

            if (i == 0&&favoriteURL!="") {
                model.addAttribute("url1", favoriteURL);
                model.addAttribute("url1query1", arrayList.get(0));
                model.addAttribute("url1query2", arrayList.get(1));
                model.addAttribute("url1query3", arrayList.get(2));
                model.addAttribute("url1query4", arrayList.get(3));
                model.addAttribute("url1query5", arrayList.get(4));
                model.addAttribute("url1year1", arrayList.get(5));
                model.addAttribute("url1month1", arrayList.get(6));
                model.addAttribute("url1day1", arrayList.get(7));
                model.addAttribute("url1year2", arrayList.get(8));
                model.addAttribute("url1month2", arrayList.get(9));
                model.addAttribute("url1day2", arrayList.get(10));
                model.addAttribute("url1select_day_week_month", arrayList.get(11));
                model.addAttribute("url1device", arrayList.get(12));
                model.addAttribute("url1gender", arrayList.get(13));
                model.addAttribute("url1age1", arrayList2);
                model.addAttribute("url1year3", arrayList.get(14));
                model.addAttribute("url1month3", arrayList.get(15));
                model.addAttribute("url1day3", arrayList.get(16));
                model.addAttribute("url1year4", arrayList.get(17));
                model.addAttribute("url1month4", arrayList.get(18));
                model.addAttribute("url1day4", arrayList.get(19));
                model.addAttribute("url1select_day_week_month2", arrayList.get(20));
                model.addAttribute("url1device2", arrayList.get(21));
                model.addAttribute("url1gender2", arrayList.get(22));
                model.addAttribute("url1age2", arrayList3);
                model.addAttribute("isTrue1", isTrue1);
            } else if (i==0&&favoriteURL==""){
                isTrue1 = false;
                model.addAttribute("isTrue1", isTrue1);
            }
            if (i == 1&&favoriteURL!="") {
                model.addAttribute("url2", favoriteURL);
                model.addAttribute("url2query1", arrayList.get(0));
                model.addAttribute("url2query2", arrayList.get(1));
                model.addAttribute("url2query3", arrayList.get(2));
                model.addAttribute("url2query4", arrayList.get(3));
                model.addAttribute("url2query5", arrayList.get(4));
                model.addAttribute("url2year1", arrayList.get(5));
                model.addAttribute("url2month1", arrayList.get(6));
                model.addAttribute("url2day1", arrayList.get(7));
                model.addAttribute("url2year2", arrayList.get(8));
                model.addAttribute("url2month2", arrayList.get(9));
                model.addAttribute("url2day2", arrayList.get(10));
                model.addAttribute("url2select_day_week_month", arrayList.get(11));
                model.addAttribute("url2device", arrayList.get(12));
                model.addAttribute("url2gender", arrayList.get(13));
                model.addAttribute("url2age1", arrayList2);
                model.addAttribute("url2year3", arrayList.get(14));
                model.addAttribute("url2month3", arrayList.get(15));
                model.addAttribute("url2day3", arrayList.get(16));
                model.addAttribute("url2year4", arrayList.get(17));
                model.addAttribute("url2month4", arrayList.get(18));
                model.addAttribute("url2day4", arrayList.get(19));
                model.addAttribute("url2select_day_week_month2", arrayList.get(20));
                model.addAttribute("url2device2", arrayList.get(21));
                model.addAttribute("url2gender2", arrayList.get(22));
                model.addAttribute("url2age2", arrayList3);
                model.addAttribute("isTrue2", isTrue2);
            } else if (i==1&&favoriteURL==""){
                isTrue2 = false;
                model.addAttribute("isTrue2", isTrue2);
            }
            if (i == 2&&favoriteURL!="") {
                model.addAttribute("url3", favoriteURL);
                model.addAttribute("url3query1", arrayList.get(0));
                model.addAttribute("url3query2", arrayList.get(1));
                model.addAttribute("url3query3", arrayList.get(2));
                model.addAttribute("url3query4", arrayList.get(3));
                model.addAttribute("url3query5", arrayList.get(4));
                model.addAttribute("url3year1", arrayList.get(5));
                model.addAttribute("url3month1", arrayList.get(6));
                model.addAttribute("url3day1", arrayList.get(7));
                model.addAttribute("url3year2", arrayList.get(8));
                model.addAttribute("url3month2", arrayList.get(9));
                model.addAttribute("url3day2", arrayList.get(10));
                model.addAttribute("url3select_day_week_month", arrayList.get(11));
                model.addAttribute("url3device", arrayList.get(12));
                model.addAttribute("url3gender", arrayList.get(13));
                model.addAttribute("url3age1", arrayList2);
                model.addAttribute("url3year3", arrayList.get(14));
                model.addAttribute("url3month3", arrayList.get(15));
                model.addAttribute("url3day3", arrayList.get(16));
                model.addAttribute("url3year4", arrayList.get(17));
                model.addAttribute("url3month4", arrayList.get(18));
                model.addAttribute("url3day4", arrayList.get(19));
                model.addAttribute("url3select_day_week_month2", arrayList.get(20));
                model.addAttribute("url3device2", arrayList.get(21));
                model.addAttribute("url3gender2", arrayList.get(22));
                model.addAttribute("url3age2", arrayList3);
                model.addAttribute("isTrue3", isTrue3);
            } else if (i==2&&favoriteURL==""){
                isTrue3 = false;
                model.addAttribute("isTrue3", isTrue3);
            }
            if (i == 3&&favoriteURL!="") {
                model.addAttribute("url4", favoriteURL);
                model.addAttribute("url4query1", arrayList.get(0));
                model.addAttribute("url4query2", arrayList.get(1));
                model.addAttribute("url4query3", arrayList.get(2));
                model.addAttribute("url4query4", arrayList.get(3));
                model.addAttribute("url4query5", arrayList.get(4));
                model.addAttribute("url4year1", arrayList.get(5));
                model.addAttribute("url4month1", arrayList.get(6));
                model.addAttribute("url4day1", arrayList.get(7));
                model.addAttribute("url4year2", arrayList.get(8));
                model.addAttribute("url4month2", arrayList.get(9));
                model.addAttribute("url4day2", arrayList.get(10));
                model.addAttribute("url4select_day_week_month", arrayList.get(11));
                model.addAttribute("url4device", arrayList.get(12));
                model.addAttribute("url4gender", arrayList.get(13));
                model.addAttribute("url4age1", arrayList2);
                model.addAttribute("url4year3", arrayList.get(14));
                model.addAttribute("url4month3", arrayList.get(15));
                model.addAttribute("url4day3", arrayList.get(16));
                model.addAttribute("url4year4", arrayList.get(17));
                model.addAttribute("url4month4", arrayList.get(18));
                model.addAttribute("url4day4", arrayList.get(19));
                model.addAttribute("url4select_day_week_month2", arrayList.get(20));
                model.addAttribute("url4device2", arrayList.get(21));
                model.addAttribute("url4gender2", arrayList.get(22));
                model.addAttribute("url4age2", arrayList3);
                model.addAttribute("isTrue4", isTrue4);
            } else if (i==3&&favoriteURL==""){
                isTrue4 = false;
                model.addAttribute("isTrue4", isTrue4);
            }
            if (i == 4&&favoriteURL!="") {
                model.addAttribute("url5", favoriteURL);
                model.addAttribute("url5query1", arrayList.get(0));
                model.addAttribute("url5query2", arrayList.get(1));
                model.addAttribute("url5query3", arrayList.get(2));
                model.addAttribute("url5query4", arrayList.get(3));
                model.addAttribute("url5query5", arrayList.get(4));
                model.addAttribute("url5year1", arrayList.get(5));
                model.addAttribute("url5month1", arrayList.get(6));
                model.addAttribute("url5day1", arrayList.get(7));
                model.addAttribute("url5year2", arrayList.get(8));
                model.addAttribute("url5month2", arrayList.get(9));
                model.addAttribute("url5day2", arrayList.get(10));
                model.addAttribute("url5select_day_week_month", arrayList.get(11));
                model.addAttribute("url5device", arrayList.get(12));
                model.addAttribute("url5gender", arrayList.get(13));
                model.addAttribute("url5age1", arrayList2);
                model.addAttribute("url5year3", arrayList.get(14));
                model.addAttribute("url5month3", arrayList.get(15));
                model.addAttribute("url5day3", arrayList.get(16));
                model.addAttribute("url5year4", arrayList.get(17));
                model.addAttribute("url5month4", arrayList.get(18));
                model.addAttribute("url5day4", arrayList.get(19));
                model.addAttribute("url5select_day_week_month2", arrayList.get(20));
                model.addAttribute("url5device2", arrayList.get(21));
                model.addAttribute("url5gender2", arrayList.get(22));
                model.addAttribute("url5age2", arrayList3);
                model.addAttribute("isTrue5", isTrue5);
            } else if (i==4&&favoriteURL==""){
                isTrue5 = false;
                model.addAttribute("isTrue5", isTrue5);
            }
        }

        return "favorite_url";
    }

    @PostMapping("/favorite_url")
    public String search_result(@RequestParam(name = "removeAllUrl", required = false, defaultValue = "") String removeAllUrl,
                                @RequestParam(name = "removeUrl1", required = false, defaultValue = "") String removeUrl1,
                                @RequestParam(name = "removeUrl2", required = false, defaultValue = "") String removeUrl2,
                                @RequestParam(name = "removeUrl3", required = false, defaultValue = "") String removeUrl3,
                                @RequestParam(name = "removeUrl4", required = false, defaultValue = "") String removeUrl4,
                                @RequestParam(name = "removeUrl5", required = false, defaultValue = "") String removeUrl5,
                                Model model,
                                Principal principal) throws JSONException {
        String userName = principal.getName();

        if (removeAllUrl.equals("1")){
            memberService.setRemoveAllUrl(userName);
        }
        if (removeUrl1.equals("1")) {
            memberService.setRemoveUrl1(userName);
        }
        if (removeUrl2.equals("2")) {
            memberService.setRemoveUrl2(userName);
        }
        if (removeUrl3.equals("3")) {
            memberService.setRemoveUrl3(userName);
        }
        if (removeUrl4.equals("4")) {
            memberService.setRemoveUrl4(userName);
        }
        if (removeUrl5.equals("5")) {
            memberService.setRemoveUrl5(userName);
        }

        String favoriteURL = "";
        boolean isTrue1 = true;
        boolean isTrue2 = true;
        boolean isTrue3 = true;
        boolean isTrue4 = true;
        boolean isTrue5 = true;
        boolean isTrueAll = true;

        for (int i = 0; i < memberService.getDbFavriteURL(userName).size(); i++) {
            favoriteURL = memberService.getDbFavriteURL(userName).get(i);
            ArrayList<String> arrayList = memberService.extractUrl(favoriteURL);

            if (i == 0&&favoriteURL!="") {
                model.addAttribute("url1", favoriteURL);
                model.addAttribute("url1query1", arrayList.get(0));
                model.addAttribute("url1query2", arrayList.get(1));
                model.addAttribute("url1query3", arrayList.get(2));
                model.addAttribute("url1query4", arrayList.get(3));
                model.addAttribute("url1query5", arrayList.get(4));
                model.addAttribute("url1year1", arrayList.get(5));
                model.addAttribute("url1month1", arrayList.get(6));
                model.addAttribute("url1day1", arrayList.get(7));
                model.addAttribute("url1year2", arrayList.get(8));
                model.addAttribute("url1month2", arrayList.get(9));
                model.addAttribute("url1day2", arrayList.get(10));
                model.addAttribute("url1select_day_week_month", arrayList.get(11));
                model.addAttribute("url1device", arrayList.get(12));
                model.addAttribute("url1gender", arrayList.get(13));
                model.addAttribute("url1year3", arrayList.get(14));
                model.addAttribute("url1month3", arrayList.get(15));
                model.addAttribute("url1day3", arrayList.get(16));
                model.addAttribute("url1year4", arrayList.get(17));
                model.addAttribute("url1month4", arrayList.get(18));
                model.addAttribute("url1day4", arrayList.get(19));
                model.addAttribute("url1select_day_week_month2", arrayList.get(20));
                model.addAttribute("url1device2", arrayList.get(21));
                model.addAttribute("url1gender2", arrayList.get(22));
                model.addAttribute("isTrue1", isTrue1);
            } else if (i==0&&favoriteURL==""){
                isTrue1 = false;
                model.addAttribute("isTrue1", isTrue1);
            }
            if (i == 1&&favoriteURL!="") {
                model.addAttribute("url2", favoriteURL);
                model.addAttribute("url2query1", arrayList.get(0));
                model.addAttribute("url2query2", arrayList.get(1));
                model.addAttribute("url2query3", arrayList.get(2));
                model.addAttribute("url2query4", arrayList.get(3));
                model.addAttribute("url2query5", arrayList.get(4));
                model.addAttribute("url2year1", arrayList.get(5));
                model.addAttribute("url2month1", arrayList.get(6));
                model.addAttribute("url2day1", arrayList.get(7));
                model.addAttribute("url2year2", arrayList.get(8));
                model.addAttribute("url2month2", arrayList.get(9));
                model.addAttribute("url2day2", arrayList.get(10));
                model.addAttribute("url2select_day_week_month", arrayList.get(11));
                model.addAttribute("url2device", arrayList.get(12));
                model.addAttribute("url2gender", arrayList.get(13));
                model.addAttribute("url2year3", arrayList.get(14));
                model.addAttribute("url2month3", arrayList.get(15));
                model.addAttribute("url2day3", arrayList.get(16));
                model.addAttribute("url2year4", arrayList.get(17));
                model.addAttribute("url2month4", arrayList.get(18));
                model.addAttribute("url2day4", arrayList.get(19));
                model.addAttribute("url2select_day_week_month2", arrayList.get(20));
                model.addAttribute("url2device2", arrayList.get(21));
                model.addAttribute("url2gender2", arrayList.get(22));
                model.addAttribute("isTrue2", isTrue2);
            } else if (i==1&&favoriteURL==""){
                isTrue2 = false;
                model.addAttribute("isTrue2", isTrue2);
            }
            if (i == 2&&favoriteURL!="") {
                model.addAttribute("url3", favoriteURL);
                model.addAttribute("url3query1", arrayList.get(0));
                model.addAttribute("url3query2", arrayList.get(1));
                model.addAttribute("url3query3", arrayList.get(2));
                model.addAttribute("url3query4", arrayList.get(3));
                model.addAttribute("url3query5", arrayList.get(4));
                model.addAttribute("url3year1", arrayList.get(5));
                model.addAttribute("url3month1", arrayList.get(6));
                model.addAttribute("url3day1", arrayList.get(7));
                model.addAttribute("url3year2", arrayList.get(8));
                model.addAttribute("url3month2", arrayList.get(9));
                model.addAttribute("url3day2", arrayList.get(10));
                model.addAttribute("url3select_day_week_month", arrayList.get(11));
                model.addAttribute("url3device", arrayList.get(12));
                model.addAttribute("url3gender", arrayList.get(13));
                model.addAttribute("url3year3", arrayList.get(14));
                model.addAttribute("url3month3", arrayList.get(15));
                model.addAttribute("url3day3", arrayList.get(16));
                model.addAttribute("url3year4", arrayList.get(17));
                model.addAttribute("url3month4", arrayList.get(18));
                model.addAttribute("url3day4", arrayList.get(19));
                model.addAttribute("url3select_day_week_month2", arrayList.get(20));
                model.addAttribute("url3device2", arrayList.get(21));
                model.addAttribute("url3gender2", arrayList.get(22));
                model.addAttribute("isTrue3", isTrue3);
            } else if (i==2&&favoriteURL==""){
                isTrue3 = false;
                model.addAttribute("isTrue3", isTrue3);
            }
            if (i == 3&&favoriteURL!="") {
                model.addAttribute("url4", favoriteURL);
                model.addAttribute("url4query1", arrayList.get(0));
                model.addAttribute("url4query2", arrayList.get(1));
                model.addAttribute("url4query3", arrayList.get(2));
                model.addAttribute("url4query4", arrayList.get(3));
                model.addAttribute("url4query5", arrayList.get(4));
                model.addAttribute("url4year1", arrayList.get(5));
                model.addAttribute("url4month1", arrayList.get(6));
                model.addAttribute("url4day1", arrayList.get(7));
                model.addAttribute("url4year2", arrayList.get(8));
                model.addAttribute("url4month2", arrayList.get(9));
                model.addAttribute("url4day2", arrayList.get(10));
                model.addAttribute("url4select_day_week_month", arrayList.get(11));
                model.addAttribute("url4device", arrayList.get(12));
                model.addAttribute("url4gender", arrayList.get(13));
                model.addAttribute("url4year3", arrayList.get(14));
                model.addAttribute("url4month3", arrayList.get(15));
                model.addAttribute("url4day3", arrayList.get(16));
                model.addAttribute("url4year4", arrayList.get(17));
                model.addAttribute("url4month4", arrayList.get(18));
                model.addAttribute("url4day4", arrayList.get(19));
                model.addAttribute("url4select_day_week_month2", arrayList.get(20));
                model.addAttribute("url4device2", arrayList.get(21));
                model.addAttribute("url4gender2", arrayList.get(22));
                model.addAttribute("isTrue4", isTrue4);
            } else if (i==3&&favoriteURL==""){
                isTrue4 = false;
                model.addAttribute("isTrue4", isTrue4);
            }
            if (i == 4&&favoriteURL!="") {
                model.addAttribute("url5", favoriteURL);
                model.addAttribute("url5query1", arrayList.get(0));
                model.addAttribute("url5query2", arrayList.get(1));
                model.addAttribute("url5query3", arrayList.get(2));
                model.addAttribute("url5query4", arrayList.get(3));
                model.addAttribute("url5query5", arrayList.get(4));
                model.addAttribute("url5year1", arrayList.get(5));
                model.addAttribute("url5month1", arrayList.get(6));
                model.addAttribute("url5day1", arrayList.get(7));
                model.addAttribute("url5year2", arrayList.get(8));
                model.addAttribute("url5month2", arrayList.get(9));
                model.addAttribute("url5day2", arrayList.get(10));
                model.addAttribute("url5select_day_week_month", arrayList.get(11));
                model.addAttribute("url5device", arrayList.get(12));
                model.addAttribute("url5gender", arrayList.get(13));
                model.addAttribute("url5year3", arrayList.get(14));
                model.addAttribute("url5month3", arrayList.get(15));
                model.addAttribute("url5day3", arrayList.get(16));
                model.addAttribute("url5year4", arrayList.get(17));
                model.addAttribute("url5month4", arrayList.get(18));
                model.addAttribute("url5day4", arrayList.get(19));
                model.addAttribute("url5select_day_week_month2", arrayList.get(20));
                model.addAttribute("url5device2", arrayList.get(21));
                model.addAttribute("url5gender2", arrayList.get(22));
                model.addAttribute("isTrue5", isTrue5);
            } else if (i==4&&favoriteURL==""){
                isTrue5 = false;
                model.addAttribute("isTrue5", isTrue5);
            }
        }

        return "favorite_url";
    }

    @GetMapping("/index_result")
    public String search(Principal principal,
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
                         Model model){
        String userName = principal.getName();

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

        String favoriteURL = "";

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

        if (!query2.equals("")){
            List<String> query2SeriesData = memberService.apiResponseY(query2, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query2Series2Data = memberService.apiResponseY(query2, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData2", query2SeriesData);
            model.addAttribute("series2Data2", query2Series2Data);
        }

        if (!query3.equals("")){
            List<String> query3SeriesData = memberService.apiResponseY(query3, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query3Series2Data = memberService.apiResponseY(query3, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData3", query3SeriesData);
            model.addAttribute("series2Data3", query3Series2Data);
        }

        if (!query4.equals("")){
            List<String> query4SeriesData = memberService.apiResponseY(query4, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query4Series2Data = memberService.apiResponseY(query4, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData4", query4SeriesData);
            model.addAttribute("series2Data4", query4Series2Data);
        }

        if (!query5.equals("")){
            List<String> query5SeriesData = memberService.apiResponseY(query5, year1, month1, day1, year2, month2, day2, timeunit, coverage, gender,  age);
            List<String> query5Series2Data = memberService.apiResponseY(query5, year3, month3, day3, year4, month4, day4, timeunit2, coverage2, gender2,  age2);
            model.addAttribute("seriesData5", query5SeriesData);
            model.addAttribute("series2Data5", query5Series2Data);
        }

        boolean isTrue1 = true;
        boolean isTrue2 = true;
        boolean isTrue3 = true;
        boolean isTrue4 = true;
        boolean isTrue5 = true;
        boolean isTrueAll = true;

        for (int i = 0; i < memberService.getDbFavriteURL(userName).size(); i++) {
            favoriteURL = memberService.getDbFavriteURL(userName).get(i);

            if (i==0&&favoriteURL==""){
                isTrue1 = false;
            }
            if (i==1&&favoriteURL==""){
                isTrue2 = false;
            }
            if (i==2&&favoriteURL==""){
                isTrue3 = false;
            }
            if (i==3&&favoriteURL==""){
                isTrue4 = false;
            }
            if (i==4&&favoriteURL==""){
                isTrue5 = false;
            }

            if (isTrue1==true&&isTrue2==true&&isTrue3==true&&isTrue4==true&&isTrue5==true){
                model.addAttribute("isTrueAll", isTrueAll);
            } else {
                isTrueAll = false;
                model.addAttribute("isTrueAll", isTrueAll);
            }
        }

        return "index_result";
    }
    @GetMapping("/home")
    public String home_service() {
        return "home";
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